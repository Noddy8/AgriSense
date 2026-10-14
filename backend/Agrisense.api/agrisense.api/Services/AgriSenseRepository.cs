using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Data.SqlClient;
using agrisense.api.DTOs;
using agrisense.api.Models;
using Newtonsoft.Json;

namespace agrisense.api.Services
{
    public class AgriSenseRepository
    {
        private readonly string _connectionString;
        public AgriSenseRepository()
        {
            // Cloud hosts inject secrets as environment variables. Keep Web.config as the
            // local-development fallback so the existing SQL Server setup still works.
            _connectionString = Environment.GetEnvironmentVariable("AGRISENSE_DB_CONNECTION_STRING");
            if (String.IsNullOrWhiteSpace(_connectionString))
                _connectionString = ConfigurationManager.ConnectionStrings["AgriSenseDb"].ConnectionString;
            if (String.IsNullOrWhiteSpace(_connectionString))
                throw new ConfigurationErrorsException("Set AGRISENSE_DB_CONNECTION_STRING or configure the AgriSenseDb connection string.");
        }
        private SqlConnection Open() { var connection = new SqlConnection(_connectionString); connection.Open(); return connection; }

        public int GetOrCreateFirebaseUserId(string firebaseUid, string email, bool emailVerified, string fullName, string mobileNumber)
        {
            var suppliedEmail = String.IsNullOrWhiteSpace(email) ? null : email.Trim();
            var syntheticEmail = "firebase-" + firebaseUid + "@firebase.local";
            var profileName = String.IsNullOrWhiteSpace(fullName) ? null : fullName.Trim();
            if (profileName != null && profileName.Length > 120) profileName = profileName.Substring(0, 120);
            var canonicalName = profileName ?? (suppliedEmail == null ? "Farmer" : suppliedEmail.Split('@')[0]);
            using (var connection = Open()) using (var transaction = connection.BeginTransaction(IsolationLevel.Serializable))
            {
                int? userId = null;
                using (var command = new SqlCommand("SELECT UserId FROM dbo.Users WITH (UPDLOCK,HOLDLOCK) WHERE FirebaseUid=@uid", connection, transaction))
                {
                    command.Parameters.Add("@uid", SqlDbType.NVarChar, 128).Value = firebaseUid;
                    var value = command.ExecuteScalar(); if (value != null && value != DBNull.Value) userId = Convert.ToInt32(value);
                }
                if (!userId.HasValue && emailVerified && suppliedEmail != null)
                {
                    using (var command = new SqlCommand("SELECT UserId FROM dbo.Users WITH (UPDLOCK,HOLDLOCK) WHERE Email=@email AND FirebaseUid IS NULL", connection, transaction))
                    {
                        command.Parameters.Add("@email", SqlDbType.NVarChar, 255).Value = suppliedEmail;
                        var value = command.ExecuteScalar(); if (value != null && value != DBNull.Value) userId = Convert.ToInt32(value);
                    }
                }

                if (userId.HasValue)
                {
                    const string updateSql = "UPDATE dbo.Users SET FirebaseUid=@uid, Email=CASE WHEN @emailVerified=1 AND @email IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dbo.Users OtherUser WHERE OtherUser.Email=@email AND OtherUser.UserId<>@userId) THEN @email ELSE Email END, FullName=COALESCE(@name,FullName), MobileNumber=COALESCE(@mobile,MobileNumber) WHERE UserId=@userId";
                    using (var command = new SqlCommand(updateSql, connection, transaction))
                    {
                        command.Parameters.Add("@uid", SqlDbType.NVarChar, 128).Value = firebaseUid;
                        command.Parameters.Add("@email", SqlDbType.NVarChar, 255).Value = (object)suppliedEmail ?? DBNull.Value;
                        command.Parameters.Add("@emailVerified", SqlDbType.Bit).Value = emailVerified;
                        command.Parameters.Add("@name", SqlDbType.NVarChar, 120).Value = (object)profileName ?? DBNull.Value;
                        command.Parameters.Add("@mobile", SqlDbType.NVarChar, 32).Value = (object)mobileNumber ?? DBNull.Value;
                        command.Parameters.Add("@userId", SqlDbType.Int).Value = userId.Value;
                        command.ExecuteNonQuery();
                    }
                }
                else
                {
                    var canonicalEmail = emailVerified && suppliedEmail != null ? suppliedEmail : syntheticEmail;
                    if (emailVerified && suppliedEmail != null)
                    {
                        using (var command = new SqlCommand("SELECT CASE WHEN EXISTS (SELECT 1 FROM dbo.Users WITH (UPDLOCK,HOLDLOCK) WHERE Email=@email) THEN 1 ELSE 0 END", connection, transaction))
                        {
                            command.Parameters.Add("@email", SqlDbType.NVarChar, 255).Value = suppliedEmail;
                            if (Convert.ToInt32(command.ExecuteScalar()) != 0) canonicalEmail = syntheticEmail;
                        }
                    }
                    using (var command = new SqlCommand("INSERT dbo.Users(FullName,Email,PasswordHash,FirebaseUid,MobileNumber) VALUES(@name,@email,NULL,@uid,@mobile); SELECT CAST(SCOPE_IDENTITY() AS INT);", connection, transaction))
                    {
                        command.Parameters.Add("@uid", SqlDbType.NVarChar, 128).Value = firebaseUid;
                        command.Parameters.Add("@email", SqlDbType.NVarChar, 255).Value = canonicalEmail;
                        command.Parameters.Add("@name", SqlDbType.NVarChar, 120).Value = canonicalName;
                        command.Parameters.Add("@mobile", SqlDbType.NVarChar, 32).Value = (object)mobileNumber ?? DBNull.Value;
                        userId = Convert.ToInt32(command.ExecuteScalar());
                    }
                }
                transaction.Commit();
                return userId.Value;
            }
        }

        public IList<Farm> GetFarms(int userId)
        {
            var farms = new List<Farm>();
            using (var connection = Open()) using (var command = new SqlCommand("SELECT FarmId, UserId, Name, Location, AreaHectares, SoilType, CreatedAt FROM dbo.Farms WHERE UserId=@userId ORDER BY FarmId", connection))
            { command.Parameters.Add("@userId", SqlDbType.Int).Value = userId; using (var reader = command.ExecuteReader()) while (reader.Read()) farms.Add(ReadFarm(reader)); }
            return farms;
        }

        public Farm GetFarm(int id, int userId)
        {
            const string sql = "SELECT FarmId, UserId, Name, Location, AreaHectares, SoilType, CreatedAt FROM dbo.Farms WHERE FarmId=@id AND UserId=@userId";
            using (var connection = Open()) using (var command = new SqlCommand(sql, connection))
            { command.Parameters.Add("@id", SqlDbType.Int).Value = id; command.Parameters.Add("@userId", SqlDbType.Int).Value = userId; using (var reader = command.ExecuteReader()) return reader.Read() ? ReadFarm(reader) : null; }
        }

        public Farm CreateFarm(FarmCreateRequest request, int userId)
        {
            using (var connection = Open()) using (var command = new SqlCommand("INSERT dbo.Farms(UserId,Name,Location,AreaHectares,SoilType) OUTPUT INSERTED.FarmId, INSERTED.UserId, INSERTED.Name, INSERTED.Location, INSERTED.AreaHectares, INSERTED.SoilType, INSERTED.CreatedAt VALUES(@userId,@name,@location,@area,@soil)", connection))
            {
                command.Parameters.Add("@userId", SqlDbType.Int).Value = userId;
                command.Parameters.Add("@name", SqlDbType.NVarChar, 120).Value = request.Name;
                command.Parameters.Add("@location", SqlDbType.NVarChar, 200).Value = (object)request.Location ?? DBNull.Value;
                command.Parameters.Add("@area", SqlDbType.Decimal).Value = request.AreaHectares;
                command.Parameters.Add("@soil", SqlDbType.NVarChar, 80).Value = (object)request.SoilType ?? DBNull.Value;
                using (var reader = command.ExecuteReader()) return reader.Read() ? ReadFarm(reader) : null;
            }
        }

        public Farm EnsureDefaultFarm(int userId, string preferredName)
        {
            using (var connection = Open()) using (var transaction = connection.BeginTransaction(IsolationLevel.Serializable))
            {
                const string selectSql = "SELECT TOP 1 FarmId, UserId, Name, Location, AreaHectares, SoilType, CreatedAt FROM dbo.Farms WITH (UPDLOCK,HOLDLOCK) WHERE UserId=@userId ORDER BY FarmId";
                Farm existing = null;
                using (var command = new SqlCommand(selectSql, connection, transaction))
                {
                    command.Parameters.Add("@userId", SqlDbType.Int).Value = userId;
                    using (var reader = command.ExecuteReader()) if (reader.Read()) existing = ReadFarm(reader);
                }
                if (existing != null) { transaction.Commit(); return existing; }
                const string insertSql = "INSERT dbo.Farms(UserId,Name,AreaHectares) OUTPUT INSERTED.FarmId, INSERTED.UserId, INSERTED.Name, INSERTED.Location, INSERTED.AreaHectares, INSERTED.SoilType, INSERTED.CreatedAt VALUES(@userId,@name,0)";
                using (var command = new SqlCommand(insertSql, connection, transaction))
                {
                    command.Parameters.Add("@userId", SqlDbType.Int).Value = userId;
                    command.Parameters.Add("@name", SqlDbType.NVarChar, 120).Value = String.IsNullOrWhiteSpace(preferredName) ? "My Farm" : preferredName.Trim();
                    Farm created;
                    using (var reader = command.ExecuteReader()) created = reader.Read() ? ReadFarm(reader) : null;
                    transaction.Commit();
                    return created;
                }
            }
        }

        public SensorReading GetLatest(int farmId, int userId)
        {
            const string sql = "SELECT TOP 1 R.ReadingId,R.FarmId,R.Source,R.N,R.P,R.K,R.Temperature,R.Humidity,R.Ph,R.Rainfall,R.SoilMoisture,R.RecordedAt FROM dbo.SensorReadings R INNER JOIN dbo.Farms F ON F.FarmId=R.FarmId WHERE R.FarmId=@farmId AND F.UserId=@userId ORDER BY R.RecordedAt DESC,R.ReadingId DESC";
            using (var connection = Open()) using (var command = new SqlCommand(sql, connection))
            { command.Parameters.Add("@farmId", SqlDbType.Int).Value = farmId; command.Parameters.Add("@userId", SqlDbType.Int).Value = userId; using (var reader = command.ExecuteReader()) return reader.Read() ? ReadSensor(reader) : null; }
        }

        public IList<SensorReading> GetHistory(int farmId, int userId, int limit)
        {
            var items = new List<SensorReading>();
            const string sql = "SELECT TOP (@limit) R.ReadingId,R.FarmId,R.Source,R.N,R.P,R.K,R.Temperature,R.Humidity,R.Ph,R.Rainfall,R.SoilMoisture,R.RecordedAt FROM dbo.SensorReadings R INNER JOIN dbo.Farms F ON F.FarmId=R.FarmId WHERE R.FarmId=@farmId AND F.UserId=@userId ORDER BY R.RecordedAt DESC,R.ReadingId DESC";
            using (var connection = Open()) using (var command = new SqlCommand(sql, connection))
            { command.Parameters.Add("@limit", SqlDbType.Int).Value = limit; command.Parameters.Add("@farmId", SqlDbType.Int).Value = farmId; command.Parameters.Add("@userId", SqlDbType.Int).Value = userId; using (var reader = command.ExecuteReader()) while (reader.Read()) items.Add(ReadSensor(reader)); }
            return items;
        }

        public SensorReading AddReading(SensorReadingRequest request)
        {
            const string sql = "INSERT dbo.SensorReadings(FarmId,Source,N,P,K,Temperature,Humidity,Ph,Rainfall,SoilMoisture) OUTPUT INSERTED.ReadingId,INSERTED.FarmId,INSERTED.Source,INSERTED.N,INSERTED.P,INSERTED.K,INSERTED.Temperature,INSERTED.Humidity,INSERTED.Ph,INSERTED.Rainfall,INSERTED.SoilMoisture,INSERTED.RecordedAt VALUES(@farmId,@source,@n,@p,@k,@temp,@humidity,@ph,@rainfall,@moisture)";
            using (var connection = Open()) using (var command = new SqlCommand(sql, connection))
            {
                command.Parameters.Add("@farmId", SqlDbType.Int).Value = request.FarmId;
                command.Parameters.Add("@source", SqlDbType.NVarChar, 30).Value = request.Source;
                AddNumber(command,"@n",request.N); AddNumber(command,"@p",request.P); AddNumber(command,"@k",request.K); AddNumber(command,"@temp",request.Temperature); AddNumber(command,"@humidity",request.Humidity); AddNumber(command,"@ph",request.Ph); AddNumber(command,"@rainfall",request.Rainfall);
                command.Parameters.Add("@moisture", SqlDbType.Float).Value = (object)request.SoilMoisture ?? DBNull.Value;
                using (var reader = command.ExecuteReader()) return reader.Read() ? ReadSensor(reader) : null;
            }
        }

        public void SaveCropRecommendation(int farmId, long? readingId, CropRecommendationResponse response)
        {
            const string sql = "INSERT dbo.CropRecommendations(FarmId,ReadingId,RecommendedCrop,Confidence,ModelVersion) VALUES(@farmId,@readingId,@crop,@confidence,N'crop-model-v1')";
            using (var connection = Open()) using (var command = new SqlCommand(sql, connection))
            {
                command.Parameters.Add("@farmId", SqlDbType.Int).Value = farmId;
                command.Parameters.Add("@readingId", SqlDbType.BigInt).Value = readingId.HasValue ? (object)readingId.Value : DBNull.Value;
                command.Parameters.Add("@crop", SqlDbType.NVarChar, 80).Value = response.RecommendedCrop;
                var confidence = response.Recommendations != null && response.Recommendations.Count > 0 ? response.Recommendations[0].Confidence : 0d;
                command.Parameters.Add("@confidence", SqlDbType.Float).Value = confidence;
                command.ExecuteNonQuery();
            }
        }

        public void SaveFertilizerRecommendation(int farmId, FertilizerRecommendationResponse response)
        {
            const string sql = "INSERT dbo.FertilizerRecommendations(FarmId,Recommendation,Source) VALUES(@farmId,@recommendation,N'ai-model')";
            using (var connection = Open()) using (var command = new SqlCommand(sql, connection))
            {
                command.Parameters.Add("@farmId", SqlDbType.Int).Value = farmId;
                command.Parameters.Add("@recommendation", SqlDbType.NVarChar, -1).Value = JsonConvert.SerializeObject(response);
                command.ExecuteNonQuery();
            }
        }

        private static void AddNumber(SqlCommand command, string name, double value) { command.Parameters.Add(name, SqlDbType.Float).Value = value; }
        private static Farm ReadFarm(IDataRecord r) { return new Farm { FarmId=(int)r["FarmId"], UserId=(int)r["UserId"], Name=(string)r["Name"], Location=r["Location"] as string, AreaHectares=Convert.ToDouble(r["AreaHectares"]), SoilType=r["SoilType"] as string, CreatedAt=(DateTime)r["CreatedAt"] }; }
        private static SensorReading ReadSensor(IDataRecord r) { return new SensorReading { ReadingId=Convert.ToInt64(r["ReadingId"]), FarmId=(int)r["FarmId"], Source=(string)r["Source"], N=Convert.ToDouble(r["N"]), P=Convert.ToDouble(r["P"]), K=Convert.ToDouble(r["K"]), Temperature=Convert.ToDouble(r["Temperature"]), Humidity=Convert.ToDouble(r["Humidity"]), Ph=Convert.ToDouble(r["Ph"]), Rainfall=Convert.ToDouble(r["Rainfall"]), SoilMoisture=r["SoilMoisture"] == DBNull.Value ? (double?)null : Convert.ToDouble(r["SoilMoisture"]), RecordedAt=(DateTime)r["RecordedAt"] }; }
    }
}
