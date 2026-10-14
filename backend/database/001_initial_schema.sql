/* Run with SQL Server Management Studio or sqlcmd. Creates an isolated prototype database. */
IF DB_ID(N'AgriSense') IS NULL CREATE DATABASE AgriSense;
GO
USE AgriSense;
GO
IF OBJECT_ID(N'dbo.Users', N'U') IS NULL
CREATE TABLE dbo.Users (UserId INT IDENTITY PRIMARY KEY, FullName NVARCHAR(120) NOT NULL, Email NVARCHAR(255) NOT NULL UNIQUE, PasswordHash NVARCHAR(255) NULL, FirebaseUid NVARCHAR(128) NULL, MobileNumber NVARCHAR(32) NULL, CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME());
IF COL_LENGTH(N'dbo.Users', N'FirebaseUid') IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id = OBJECT_ID(N'dbo.Users') AND name = N'UX_Users_FirebaseUid')
    CREATE UNIQUE INDEX UX_Users_FirebaseUid ON dbo.Users(FirebaseUid) WHERE FirebaseUid IS NOT NULL;
IF OBJECT_ID(N'dbo.Farms', N'U') IS NULL
CREATE TABLE dbo.Farms (FarmId INT IDENTITY PRIMARY KEY, UserId INT NOT NULL REFERENCES dbo.Users(UserId), Name NVARCHAR(120) NOT NULL, Location NVARCHAR(200) NULL, AreaHectares DECIMAL(10,2) NOT NULL DEFAULT 0, SoilType NVARCHAR(80) NULL, CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME());
IF OBJECT_ID(N'dbo.Devices', N'U') IS NULL
CREATE TABLE dbo.Devices (DeviceId INT IDENTITY PRIMARY KEY, FarmId INT NOT NULL REFERENCES dbo.Farms(FarmId), DeviceKey NVARCHAR(100) NOT NULL UNIQUE, Name NVARCHAR(120) NOT NULL, DeviceType NVARCHAR(40) NOT NULL DEFAULT N'ESP32', IsActive BIT NOT NULL DEFAULT 1, LastSeenAt DATETIME2 NULL, CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME());
IF OBJECT_ID(N'dbo.SoilProfiles', N'U') IS NULL
CREATE TABLE dbo.SoilProfiles (SoilProfileId INT IDENTITY PRIMARY KEY, FarmId INT NOT NULL REFERENCES dbo.Farms(FarmId), N FLOAT NOT NULL, P FLOAT NOT NULL, K FLOAT NOT NULL, Ph FLOAT NOT NULL, EC FLOAT NULL, SoilType NVARCHAR(80) NULL, UpdatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME());
IF OBJECT_ID(N'dbo.SensorReadings', N'U') IS NULL
CREATE TABLE dbo.SensorReadings (ReadingId BIGINT IDENTITY PRIMARY KEY, FarmId INT NOT NULL REFERENCES dbo.Farms(FarmId), DeviceId INT NULL REFERENCES dbo.Devices(DeviceId), Source NVARCHAR(30) NOT NULL CHECK (Source IN (N'simulation',N'device')), N FLOAT NOT NULL, P FLOAT NOT NULL, K FLOAT NOT NULL, Temperature FLOAT NOT NULL, Humidity FLOAT NOT NULL, Ph FLOAT NOT NULL, Rainfall FLOAT NOT NULL, SoilMoisture FLOAT NULL, EC FLOAT NULL, RecordedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME());
CREATE INDEX IX_SensorReadings_Farm_RecordedAt ON dbo.SensorReadings(FarmId, RecordedAt DESC);
IF OBJECT_ID(N'dbo.NutrientReadings', N'U') IS NULL
CREATE TABLE dbo.NutrientReadings (NutrientReadingId BIGINT IDENTITY PRIMARY KEY, ReadingId BIGINT NOT NULL REFERENCES dbo.SensorReadings(ReadingId), N FLOAT NOT NULL, P FLOAT NOT NULL, K FLOAT NOT NULL);
IF OBJECT_ID(N'dbo.IrrigationEvents', N'U') IS NULL
CREATE TABLE dbo.IrrigationEvents (IrrigationEventId BIGINT IDENTITY PRIMARY KEY, FarmId INT NOT NULL REFERENCES dbo.Farms(FarmId), StartedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(), EndedAt DATETIME2 NULL, WaterLitres FLOAT NULL, Mode NVARCHAR(20) NOT NULL DEFAULT N'manual', Status NVARCHAR(30) NOT NULL DEFAULT N'completed');
IF OBJECT_ID(N'dbo.CropRecommendations', N'U') IS NULL
CREATE TABLE dbo.CropRecommendations (CropRecommendationId BIGINT IDENTITY PRIMARY KEY, FarmId INT NULL REFERENCES dbo.Farms(FarmId), ReadingId BIGINT NULL REFERENCES dbo.SensorReadings(ReadingId), RecommendedCrop NVARCHAR(80) NOT NULL, Confidence FLOAT NULL, ModelVersion NVARCHAR(40) NOT NULL DEFAULT N'crop-model-v1', CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME());
IF OBJECT_ID(N'dbo.FertilizerRecommendations', N'U') IS NULL
CREATE TABLE dbo.FertilizerRecommendations (FertilizerRecommendationId BIGINT IDENTITY PRIMARY KEY, FarmId INT NOT NULL REFERENCES dbo.Farms(FarmId), Recommendation NVARCHAR(MAX) NOT NULL, Source NVARCHAR(30) NOT NULL, CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME());
IF OBJECT_ID(N'dbo.IrrigationRecommendations', N'U') IS NULL
CREATE TABLE dbo.IrrigationRecommendations (IrrigationRecommendationId BIGINT IDENTITY PRIMARY KEY, FarmId INT NOT NULL REFERENCES dbo.Farms(FarmId), Recommendation NVARCHAR(MAX) NOT NULL, Source NVARCHAR(30) NOT NULL, CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME());
IF OBJECT_ID(N'dbo.DiseaseAnalyses', N'U') IS NULL
CREATE TABLE dbo.DiseaseAnalyses (DiseaseAnalysisId BIGINT IDENTITY PRIMARY KEY, FarmId INT NULL REFERENCES dbo.Farms(FarmId), ImageReference NVARCHAR(500) NULL, Result NVARCHAR(MAX) NOT NULL, Confidence FLOAT NULL, CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME());
IF OBJECT_ID(N'dbo.Notifications', N'U') IS NULL
CREATE TABLE dbo.Notifications (NotificationId BIGINT IDENTITY PRIMARY KEY, UserId INT NOT NULL REFERENCES dbo.Users(UserId), FarmId INT NULL REFERENCES dbo.Farms(FarmId), Title NVARCHAR(160) NOT NULL, Message NVARCHAR(1000) NOT NULL, IsRead BIT NOT NULL DEFAULT 0, CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME());
IF OBJECT_ID(N'dbo.WeatherData', N'U') IS NULL
CREATE TABLE dbo.WeatherData (WeatherDataId BIGINT IDENTITY PRIMARY KEY, FarmId INT NOT NULL REFERENCES dbo.Farms(FarmId), Temperature FLOAT NULL, Humidity FLOAT NULL, Rainfall FLOAT NULL, ForecastAt DATETIME2 NOT NULL, Provider NVARCHAR(60) NULL, CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME());
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE Email=N'demo@agrisense.local')
BEGIN
 INSERT dbo.Users(FullName,Email,PasswordHash) VALUES(N'AgriSense Demo',N'demo@agrisense.local',N'not-for-auth-demo');
 INSERT dbo.Farms(UserId,Name,Location,AreaHectares,SoilType) VALUES(SCOPE_IDENTITY(),N'My Demo Farm',N'Pune, Maharashtra',2.50,N'Loamy');
 INSERT dbo.SensorReadings(FarmId,Source,N,P,K,Temperature,Humidity,Ph,Rainfall,SoilMoisture) SELECT TOP 1 FarmId,N'simulation',90,42,43,20.8,82,6.5,202.9,42 FROM dbo.Farms ORDER BY FarmId DESC;
END;
GO
