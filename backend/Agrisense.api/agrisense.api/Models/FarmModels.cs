using System;
using Newtonsoft.Json;

namespace agrisense.api.Models
{
    public class Farm
    {
        public int FarmId { get; set; }
        public int UserId { get; set; }
        public string Name { get; set; }
        public string Location { get; set; }
        public double AreaHectares { get; set; }
        public string SoilType { get; set; }
        public DateTime CreatedAt { get; set; }
    }

    public class FarmCreateRequest
    {
        public string Name { get; set; }
        public string Location { get; set; }
        public double AreaHectares { get; set; }
        public string SoilType { get; set; }
    }

    public class AccountBootstrapRequest
    {
        public string FullName { get; set; }
        public string FarmName { get; set; }
        public string MobileNumber { get; set; }
    }

    public class SensorReading
    {
        public long ReadingId { get; set; }
        public int FarmId { get; set; }
        public string Source { get; set; }
        public double N { get; set; }
        public double P { get; set; }
        public double K { get; set; }
        public double Temperature { get; set; }
        public double Humidity { get; set; }
        public double Ph { get; set; }
        public double Rainfall { get; set; }
        public double? SoilMoisture { get; set; }
        public DateTime RecordedAt { get; set; }
    }

    public class SensorReadingRequest
    {
        public int FarmId { get; set; }
        public string Source { get; set; }
        public double N { get; set; }
        public double P { get; set; }
        public double K { get; set; }
        public double Temperature { get; set; }
        public double Humidity { get; set; }
        public double Ph { get; set; }
        public double Rainfall { get; set; }
        public double? SoilMoisture { get; set; }
    }

    public class DashboardSummary
    {
        public Farm Farm { get; set; }
        public SensorReading LatestReading { get; set; }
        public string DataMode { get; set; }
    }

    public class ApiError { public string Message { get; set; } }
}
