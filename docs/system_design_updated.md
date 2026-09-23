# AgriSense AI --- System Design

## 1. Purpose

This document describes the technical design of the AgriSense AI system,
including its architecture, modules, data flow, AI services, IoT layer,
backend, database, mobile application, notifications, and development
boundaries.

This is a living design document. Changes to major architecture
decisions should be recorded separately as Architecture Decision Records
(ADRs).

------------------------------------------------------------------------

# 2. System Architecture

``` text
                         +----------------------+
                         |    Android App (Java + XML)|
                         |      Java + XML      |
                         +----------+-----------+
                                    |
                                    | HTTPS / REST
                                    v
                         +----------------------+
                         |   C# .NET Backend API   |
                         |   Authentication     |
                         |   Business Logic     |
                         +----+------------+----+
                              |            |
                     SQL      |            | AI requests
                              v            v
                    +---------+--+   +-----+-------------+
                    | SQL Server |   | Python FastAPI   |
                    |            |   | AI Services      |
                    +------------+   +--+----+-----+-----+
                                      |    |     |
                                      v    v     v
                                    Crop Fert. Irrigation
                                    Model Model  Model

ESP32 + Sensors
      |
      | Wi-Fi / HTTP or MQTT
      v
C# .NET Backend API
      |
      +----> SQL Server
      |
      +----> AI Services
      |
      +----> Mobile Application
```

------------------------------------------------------------------------

# 2.1 Current Technology Decisions

The current project architecture uses:

-   **Mobile:** Android Studio, Java, XML layouts
-   **Backend API:** C# .NET
-   **Database:** Microsoft SQL Server
-   **AI service:** Python/FastAPI for the trained ML models
-   **IoT controller:** ESP32
-   **Push notifications:** Firebase Cloud Messaging (FCM)

The Android application communicates with the C# .NET backend over
HTTPS/REST. The backend handles authentication, business logic, database
access, IoT data ingestion, and orchestration of AI services.

------------------------------------------------------------------------

# 3. Architectural Layers

## 3.1 Hardware Layer

Responsible for:

-   Sensor acquisition
-   Actuator control
-   Wi-Fi connectivity
-   RS485/Modbus communication
-   Local safety checks

Main controller:

**ESP32 DevKit V1**

------------------------------------------------------------------------

## 3.2 Communication Layer

Communication between hardware and backend can use:

-   HTTP REST for straightforward sensor submission
-   MQTT where persistent IoT messaging is useful

RS485 is used locally between the ESP32 and compatible digital sensors
such as the NPK sensor.

------------------------------------------------------------------------

## 3.3 Backend Layer

The C# .NET backend acts as the central application gateway.

Responsibilities:

-   Authentication
-   User management
-   Farm management
-   Device management
-   Sensor data ingestion
-   Data validation
-   Database operations
-   AI service orchestration
-   Weather integration
-   Notification orchestration
-   Mobile API

The mobile application should not directly contain secret AI/weather API
keys.

------------------------------------------------------------------------

# 4. AI Service Architecture

Python/FastAPI provides a separate AI service.

``` text
C# .NET Backend
     |
     +---- POST /predict/crop
     |
     +---- POST /predict/fertilizer
     |
     +---- POST /predict/irrigation
     |
     +---- POST /assistant
     |
     +---- POST /disease/analyze
     |
     v
Python AI Service
```

The exact endpoint names can be adjusted when the API contract is
finalized.

------------------------------------------------------------------------

# 5. Crop Recommendation Design

## Inputs

The initial trained model uses:

``` text
N
P
K
temperature
humidity
ph
rainfall
```

## Processing

``` text
Sensor / soil data
        |
        v
Validation
        |
        v
Feature ordering
        |
        v
Crop model
        |
        v
Prediction
```

## Output

The API should return a structured response rather than only a string.

Example:

``` json
{
  "recommendedCrop": "rice",
  "confidence": 0.94
}
```

If top-N recommendations are implemented, the response can contain an
array of recommendations.

------------------------------------------------------------------------

# 6. Fertilizer Recommendation Design

## Expected Inputs

``` text
N
P
K
pH
EC
crop
season/context
```

The exact feature list must match the selected training dataset and
trained model.

## Processing

``` text
Soil Profile
     |
     v
Feature Validation
     |
     v
Fertilizer Model
     |
     v
Recommendation
```

Potential output:

``` json
{
  "fertilizer": "example",
  "confidence": 0.91
}
```

Quantity and application schedule should only be returned if the
model/dataset and agricultural rules support those outputs.

------------------------------------------------------------------------

# 7. Irrigation Recommendation Design

## Candidate Inputs

``` text
soilMoisture
temperature
humidity
rainfall/weather forecast
crop
soilEC
previousIrrigation
waterFlow
```

## Processing

``` text
Live/Recent Sensor Data
          |
          v
Validation
          |
          v
Irrigation Model
          |
          v
Recommendation
          |
          +------> Mobile App
          |
          +------> Irrigation Controller
```

The final target variable must be defined before training. Possible
designs include:

-   irrigation required/not required
-   irrigation duration
-   water quantity

These are different ML problems and should not be mixed without
appropriate training data.

------------------------------------------------------------------------

# 8. Generative AI Design

The generative AI API is used for:

-   Farmer assistant
-   Natural-language explanation
-   Leaf-image disease analysis
-   Contextual explanation of ML recommendations

The system should provide structured context to the AI API.

Example:

``` json
{
  "crop": "rice",
  "soil": {
    "N": 90,
    "P": 42,
    "K": 43,
    "ph": 6.5,
    "ec": 1.2
  },
  "environment": {
    "temperature": 29,
    "humidity": 75,
    "soilMoisture": 31
  },
  "recommendations": {
    "crop": "rice",
    "fertilizer": "model-result",
    "irrigation": "model-result"
  },
  "question": "Why is my soil moisture decreasing quickly?"
}
```

The backend should control what information is sent to the external AI
service.

------------------------------------------------------------------------

# 9. Disease Analysis Design

``` text
Mobile Camera
      |
      v
Image Upload
      |
      v
C# .NET Backend
      |
      v
Multimodal AI API
      |
      v
Structured Analysis
      |
      v
Mobile UI
```

The system should display appropriate uncertainty language and should
not present an AI image analysis as guaranteed identification.

------------------------------------------------------------------------

# 10. IoT Sensor Architecture

## Sensor Groups

### Soil

``` text
NPK
pH
EC
Soil Moisture
```

### Environment

``` text
DHT22
Rain Sensor
```

### Water

``` text
Water Flow
Water Level
```

------------------------------------------------------------------------

# 11. Preliminary GPIO Design

    GPIO Device                     Interface
  ------ -------------------------- -----------
      32 Capacitive Soil Moisture   ADC
      33 Water Level                ADC
      34 EC                         ADC
      35 pH                         ADC
       4 DHT22                      Digital
      27 Rain Sensor                Digital
       5 Water Flow                 Pulse
      16 RS485 RX                   UART
      17 RS485 TX                   UART
      18 RS485 DE/RE                Digital
      26 Relay CH1                  Digital
      14 Relay CH2                  Digital

This is a preliminary project-level pin allocation. It must be verified
against exact hardware modules.

------------------------------------------------------------------------

# 12. RS485 NPK Design

``` text
+-------------------+
| RS485 NPK Sensor  |
|                   |
| A ---------------+----------------+
| B ---------------+------------+   |
+-------------------+            |   |
                                  v   v
                            +-----------+
                            | RS485     |
                            | Interface |
                            +-----+-----+
                                  |
                         UART RX/TX/DE/RE
                                  |
                                  v
                              ESP32
```

The final transceiver must be electrically compatible with ESP32 logic
levels.

The NPK sensor's Modbus register addresses, baud rate, parity, slave ID,
power requirements, and measurement units must be obtained from the
selected sensor's documentation.

------------------------------------------------------------------------

# 13. Actuator Design

## Pump

``` text
ESP32 GPIO26
      |
      v
Relay CH1
      |
      v
12V Pump
```

## Solenoid

``` text
ESP32 GPIO14
      |
      v
Relay CH2
      |
      v
12V Solenoid Valve
```

Pump and solenoid power must come from a suitable external supply.

The ESP32 should only provide the control signal to the relay/interface.

------------------------------------------------------------------------

# 14. Irrigation Safety Logic

AI recommendations should not be the only safety mechanism.

A possible control structure is:

``` text
AI Recommendation
        |
        v
Safety Rules
        |
        +---- Tank level sufficient?
        |
        +---- Sensor valid?
        |
        +---- Pump fault?
        |
        +---- Manual override?
        |
        v
Actuator Control
```

Examples of safety rules:

-   Do not run the pump when the tank is empty.
-   Stop irrigation if a critical sensor fault is detected.
-   Stop the pump if abnormal flow is detected.
-   Provide manual override.
-   Prevent indefinite pump operation using a maximum runtime.

These rules are deterministic safety controls, not ML predictions.

------------------------------------------------------------------------

# 15. Database Design

The database should distinguish relatively stable soil-profile
information from frequently changing telemetry.

## Suggested entities

``` text
Users
Farms
Devices
SoilProfiles
SensorReadings
NutrientReadings
IrrigationEvents
CropRecommendations
FertilizerRecommendations
IrrigationRecommendations
DiseaseAnalyses
Notifications
WeatherData
```

------------------------------------------------------------------------

# 16. Data Separation

## Soil Profile

Changes relatively slowly:

``` text
FarmId
N
P
K
pH
EC
TestDate
Source
```

`Source` can distinguish between sensor-derived and
manually/laboratory-entered information.

## Sensor Telemetry

Changes frequently:

``` text
DeviceId
Timestamp
SoilMoisture
Temperature
Humidity
Rain
WaterFlow
WaterLevel
```

This separation avoids treating NPK as if it were a high-frequency
sensor like temperature.

------------------------------------------------------------------------

# 17. API Layer

Example backend endpoints:

``` text
POST   /api/auth/login
POST   /api/auth/register

GET    /api/farms
POST   /api/farms

POST   /api/sensors/readings
GET    /api/sensors/latest/{farmId}

GET    /api/recommendations/crop/{farmId}
POST   /api/recommendations/crop

POST   /api/recommendations/fertilizer
POST   /api/recommendations/irrigation

POST   /api/disease/analyze
POST   /api/assistant/chat

GET    /api/weather/{farmId}

GET    /api/notifications
```

These are proposed endpoints and should be finalized through API
contracts before implementation.

------------------------------------------------------------------------

# 18. Mobile Application Design

## Main screens

``` text
Login
  |
Dashboard
  |
  +-- Live Sensor Data
  |
  +-- Crop Recommendation
  |
  +-- Fertilizer Recommendation
  |
  +-- Irrigation Recommendation
  |
  +-- Disease Analysis
  |
  +-- AI Farmer Assistant
  |
  +-- Weather
  |
  +-- Notifications
  |
  +-- History
  |
  +-- Profile
```

------------------------------------------------------------------------

# 19. Notification Architecture

Primary planned push notification:

``` text
C# .NET Backend
      |
      v
Firebase Cloud Messaging (FCM)
      |
      v
Android App (Java + XML)
```

Possible events:

-   Low soil moisture
-   Low tank level
-   Irrigation started
-   Irrigation stopped
-   Abnormal water flow
-   New recommendation
-   AI/disease analysis completed

SignalR may be used separately for real-time dashboard updates.

------------------------------------------------------------------------

# 20. Security Design

## Mobile

-   JWT access token
-   Secure token storage
-   HTTPS

## Backend

-   JWT authentication
-   Role-based authorization
-   Input validation
-   Rate limiting where appropriate
-   API key protection

## External AI/Weather APIs

Secrets must remain on the server.

Do not commit:

``` text
API keys
Passwords
Connection strings containing credentials
Private certificates
.env secrets
```

------------------------------------------------------------------------

# 21. Git and Collaboration Design

``` text
main
  |
  +--- develop
          |
          +--- feature/member-feature
          |
          +--- feature/member-feature
          |
          +--- feature/member-feature
```

Rules:

1.  Do not directly push to `main`.
2.  Do not directly push to `develop`.
3.  Create feature branches from `develop`.
4.  Test locally before pushing.
5.  Push the feature branch.
6.  Open a pull request targeting `develop`.
7.  Other team members review it.
8.  Merge only after approval.
9.  Test the integrated `develop` branch.
10. Merge stable releases into `main`.

------------------------------------------------------------------------

# 22. Hardware Documentation

The GitHub repository should contain:

``` text
hardware/
|
+-- pin-diagrams/
|   +-- ESP32_Pin_Assignment.pdf
|   +-- ESP32_Pin_Assignment.drawio
|
+-- wiring/
|   +-- System_Wiring.drawio
|   +-- System_Wiring.pdf
|
+-- firmware/
|
+-- sensors/
|
+-- images/
```

The final wiring diagram should only be marked as **FINAL** after the
exact sensor/interface modules have been selected and their electrical
specifications verified.

------------------------------------------------------------------------

# 23. Testing Strategy

## Unit Testing

Backend services and business logic.

## Integration Testing

-   Backend ↔ database
-   Backend ↔ AI service
-   Backend ↔ ESP32
-   Backend ↔ notification service

## Hardware Testing

Test each sensor independently before integrating all sensors.

## AI Testing

Evaluate:

-   Accuracy
-   Precision
-   Recall
-   F1-score
-   Cross-validation
-   Confusion matrix where appropriate

For regression models, use appropriate regression metrics rather than
classification accuracy.

## End-to-End Testing

``` text
Sensor
  |
  v
ESP32
  |
  v
Backend
  |
  v
Database / AI
  |
  v
Recommendation
  |
  v
Mobile Application
```

------------------------------------------------------------------------

# 24. Development Sequence

Recommended implementation order:

``` text
1. GitHub foundation
        |
2. AI datasets/models
        |
3. AI FastAPI service
        |
4. Database
        |
5. C# .NET Backend backend
        |
6. ESP32 individual sensors
        |
7. ESP32 communication
        |
8. Android (Java + XML) foundation
        |
9. Backend/mobile integration
        |
10. Irrigation actuator integration
        |
11. Gemini/disease integration
        |
12. Notifications
        |
13. End-to-end testing
        |
14. Final prototype
```

------------------------------------------------------------------------

# 25. Design Principles

The project follows these principles:

### Separation of concerns

IoT, backend, AI, database and mobile responsibilities remain separated.

### API-first integration

Modules communicate through documented APIs rather than depending
directly on each other's implementation.

### Safety before automation

ML recommendations do not bypass deterministic actuator safety checks.

### Replaceable AI

AI models and external AI APIs remain behind service interfaces so they
can be replaced without redesigning the mobile application.

### Hardware abstraction

Sensor-specific code remains inside the firmware/sensor layer.

### Secure secrets

Secrets remain outside Git and are supplied through
environment/configuration mechanisms.

### Reproducibility

Training notebooks, datasets metadata, model versions and evaluation
results should be documented.

------------------------------------------------------------------------

# 26. Current Design Status

**Status: Architecture under active development**

Frozen/accepted at project level:

-   ESP32-based IoT architecture
-   Android (Java + XML) mobile application
-   C# .NET Backend backend
-   SQL Server
-   Python/FastAPI AI service
-   Crop recommendation model
-   Fertilizer recommendation model planned
-   Irrigation recommendation model planned
-   Generative AI API for assistant
-   Multimodal AI API for disease analysis
-   RS485 digital NPK sensor
-   Two-channel relay for pump and solenoid
-   GitHub monorepo
-   `main` + `develop` + feature branch workflow

Not yet final:

-   Exact NPK sensor model and Modbus register map
-   Exact pH sensor module
-   Exact EC sensor module
-   Exact water-level sensor
-   Final power distribution
-   Final electrical schematic
-   Final irrigation ML target
-   Final fertilizer dataset/model features
-   Final API contracts
