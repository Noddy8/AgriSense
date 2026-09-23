# AgriSense AI — Project Bible

## 1. Project Overview

AgriSense AI is an MCA-level smart agriculture project combining:

- IoT architecture
- Machine Learning
- Generative AI
- Android mobile application
- C# .NET backend
- Microsoft SQL Server
- Python/FastAPI AI services
- Agricultural recommendations
- Irrigation automation architecture
- Disease image analysis
- AI farmer assistant

The project is being developed as a hackathon/academic prototype.

IMPORTANT:
There is currently NO physical ESP32/sensor hardware available.

Therefore, sensor data must currently be simulated.

The architecture must remain hardware-ready so that simulated data can later be replaced by ESP32 sensor data without redesigning the application.

---

# 2. FINAL TECHNOLOGY STACK

## Mobile

- Android Studio
- Java
- XML layouts
- Retrofit for REST communication
- Android Activities/Fragments
- Firebase Cloud Messaging later for notifications

DO NOT use:

- React Native
- React
- Expo
- Flutter
- FlutterFlow
- Ionic

The mobile application must remain Android Java + XML.

---

# 3. Backend

Backend:

- C# .NET
- Existing project created using Visual Studio 2017
- Current project is the older Web API 2 / .NET Framework style project

The Visual Studio project currently contains:

- App_Start
- Controllers
- DTOs
- Services
- Models
- Views
- Global.asax
- Web.config

The backend acts as the central application gateway.

Responsibilities:

- Authentication
- Farm management
- Sensor data
- Simulation data
- Database access
- AI service orchestration
- Recommendation APIs
- Weather integration later
- Notifications later
- Android API

Do not redesign the backend as a different framework unless explicitly requested.

---

# 4. Database

Database:

- Microsoft SQL Server / MS SQL

Planned entities:

- Users
- Farms
- Devices
- SoilProfiles
- SensorReadings
- NutrientReadings
- IrrigationEvents
- CropRecommendations
- FertilizerRecommendations
- IrrigationRecommendations
- DiseaseAnalyses
- Notifications
- WeatherData

Because physical hardware is not currently available, simulated sensor readings will be used.

---

# 5. AI SERVICE

AI service:

- Python
- FastAPI
- scikit-learn
- joblib
- NumPy

The AI service is separate from the C# backend.

Architecture:

Android
    ↓
C# .NET Backend
    ↓
Python FastAPI
    ↓
ML model

The Android application must NOT directly call the Python service in the final architecture.

---

# 6. CURRENT CROP MODEL

A ready-made `.pkl` model is being used.

File:

ai/crop-recommendation/model/crop_model.pkl

Model type:

RandomForestClassifier

Number of trees:

100

Number of input features:

7

IMPORTANT:

This model does NOT use the previously discussed 10-feature architecture.

It expects exactly these 7 features in this exact order:

1. N
2. P
3. K
4. temperature
5. humidity
6. ph
7. rainfall

DO NOT change the order.

DO NOT send EC, soil moisture, season, historical yield, regional success, month, etc. to this model unless the model is retrained specifically for those features.

Current model classes:

- Soyabeans
- apple
- banana
- beans
- coffee
- cotton
- cowpeas
- grapes
- groundnuts
- maize
- mango
- orange
- peas
- rice
- watermelon

---

# 7. PYTHON SERVICE

Current directory:

ai/crop-recommendation/

Structure:

model/
    crop_model.pkl

api/
    crop_api.py

test/
    test_model.py

requirements.txt

Python requirements include:

fastapi
uvicorn
scikit-learn==1.6.1
joblib
numpy
pydantic

The model was serialized using scikit-learn 1.6.1.

Prefer scikit-learn 1.6.1 for compatibility.

---

# 8. CURRENT PYTHON API

FastAPI runs on:

http://127.0.0.1:8000

Main endpoint:

POST /predict/crop

Request:

{
    "N": 90,
    "P": 42,
    "K": 43,
    "temperature": 20.8,
    "humidity": 82,
    "ph": 6.5,
    "rainfall": 202.9
}

Response structure:

{
    "success": true,
    "recommendedCrop": "...",
    "recommendations": [
        {
            "crop": "...",
            "confidence": 0.XX,
            "confidencePercentage": XX.XX
        }
    ],
    "input": {
        "N": 90,
        "P": 42,
        "K": 43,
        "temperature": 20.8,
        "humidity": 82,
        "ph": 6.5,
        "rainfall": 202.9
    }
}

FastAPI Swagger is already working successfully.

Swagger:

http://127.0.0.1:8000/docs

Health:

GET /health

---

# 9. CURRENT C# BACKEND STATUS

The C# backend project already exists.

Current important files:

Controllers/
    CropRecommendationController.cs

DTOs/
    Croprecommendationrequest.cs
    CropRecommendationResponse.cs

Services/
    CropAiServices.cs

The C# backend has already been connected to the Python FastAPI service.

The C# endpoint is:

POST /api/recommendations/crop

The flow has already been tested successfully using PowerShell.

Current working flow:

PowerShell
    ↓
C# Web API
    ↓
CropAiServices.cs
    ↓
Python FastAPI
    ↓
crop_model.pkl
    ↓
Prediction
    ↓
C#
    ↓
PowerShell

Therefore DO NOT rebuild this part from scratch.

First inspect the existing implementation before modifying it.

---

# 10. C# PYTHON COMMUNICATION

The C# AI service currently calls:

http://127.0.0.1:8000/predict/crop

The Python service must remain a separate process.

Do not embed Python inside the C# backend.

Do not move the .pkl model into the C# project.

---

# 11. ANDROID ARCHITECTURE

Android must communicate with C#.

Final architecture:

Android Java/XML
    ↓
C# .NET Backend
    ↓
Python FastAPI
    ↓
crop_model.pkl

Android must NOT:

- load crop_model.pkl
- call Python directly
- contain Python
- contain AI API secrets

For Android Emulator, localhost of the Windows machine is accessed using:

10.0.2.2

Therefore if C# runs on:

http://localhost:PORT/

Android emulator should use:

http://10.0.2.2:PORT/

---

# 12. NO PHYSICAL HARDWARE CURRENTLY

There is currently no physical:

- ESP32
- NPK sensor
- pH sensor
- EC sensor
- DHT22
- rain sensor
- water flow sensor
- water level sensor
- pump
- solenoid valve

Therefore:

DO NOT block development waiting for hardware.

Use simulated sensor data.

The architecture should support:

SIMULATED DATA
    ↓
C# BACKEND
    ↓
SQL SERVER
    ↓
AI
    ↓
ANDROID

Later:

ESP32
    ↓
C# BACKEND
    ↓
SQL SERVER
    ↓
AI
    ↓
ANDROID

The simulated source should be replaceable by ESP32 later.

---

# 13. SIMULATION MODE

Create a simulation mode in the application.

It should allow:

- Manual sensor values
- Preset scenarios
- Simulated sensor updates

Example values:

N = 90
P = 42
K = 43
Temperature = 20.8
Humidity = 82
pH = 6.5
Rainfall = 202.9

Example scenarios can include:

- Normal farm
- Low rainfall
- High temperature
- Low nitrogen
- High humidity

Always label simulated values as:

"Simulation Mode"

or:

"Simulated IoT Data"

Do not claim simulated data is real sensor data.

---

# 14. CROP RECOMMENDATION

Current crop recommendation uses only the 7 features required by crop_model.pkl.

Do not add the previously discussed 10 features to the current model.

The previously discussed 10 features were:

1. NPK match
2. pH proximity
3. temperature suitability
4. rainfall suitability
5. humidity suitability
6. soil match
7. historical yield
8. season alignment
9. regional success
10. month

These are NOT part of the current .pkl model.

They may be considered for a future retrained model.

---

# 15. CURRENT DEVELOPMENT STATUS

COMPLETED:

- Project architecture defined
- C# backend project created in Visual Studio 2017
- Python AI service created
- crop_model.pkl integrated
- Model tested independently
- FastAPI service created
- FastAPI Swagger tested successfully
- C# → Python integration completed
- C# crop endpoint created
- C# → Python → model pipeline tested successfully using PowerShell
- No hardware requirement for current development

CURRENT NEXT TASK:

Android Java/XML integration.

Target:

Android
    ↓
C# /api/recommendations/crop
    ↓
Python /predict/crop
    ↓
crop_model.pkl
    ↓
Crop result
    ↓
Android UI

---

# 16. DEVELOPMENT ORDER

Continue development in this order:

1. Android Java/XML foundation
2. Android crop recommendation screen
3. Retrofit integration
4. Android → C# integration
5. Simulation mode
6. SQL Server database
7. Store simulated sensor data
8. Dashboard
9. Sensor history
10. Fertilizer recommendation model/service
11. Irrigation recommendation model/service
12. Irrigation simulation
13. Disease image analysis
14. AI farmer assistant
15. Weather
16. Notifications
17. Authentication
18. Final integration
19. Optional ESP32 hardware integration

---

# 17. API ARCHITECTURE

Current:

POST /api/recommendations/crop

Python:

POST /predict/crop

Planned backend endpoints:

POST /api/auth/login
POST /api/auth/register

GET /api/farms
POST /api/farms

POST /api/sensors/readings
GET /api/sensors/latest/{farmId}

GET /api/recommendations/crop/{farmId}
POST /api/recommendations/crop

POST /api/recommendations/fertilizer
POST /api/recommendations/irrigation

POST /api/disease/analyze
POST /api/assistant/chat

GET /api/weather/{farmId}

GET /api/notifications

Do not implement all endpoints at once.

Build incrementally.

---

# 18. IMPORTANT DEVELOPMENT RULES

Before changing existing code:

1. Inspect the existing code.
2. Understand the current architecture.
3. Do not recreate existing functionality.
4. Do not replace working components unnecessarily.
5. Preserve the existing crop model.
6. Preserve the existing FastAPI API unless a change is required.
7. Preserve the existing C# crop API unless a change is required.
8. Keep Android, C#, Python and database layers separated.
9. Use simulated IoT data until hardware becomes available.
10. Keep secrets out of GitHub.
11. Do not commit API keys.
12. Do not commit passwords.
13. Do not commit database credentials.
14. Do not commit .env secrets.

---

# 19. CODE STYLE

Prefer simple, understandable MCA-level code.

Do not introduce unnecessary:

- Microservices
- Docker
- Kubernetes
- Redis
- Kafka
- RabbitMQ
- complex cloud infrastructure

unless explicitly requested.

The goal is a working, demonstrable hackathon prototype.

---

# 20. PRIMARY IMMEDIATE TASK

The next task is:

Connect the existing Android Studio Java/XML application to the already-working C# crop recommendation API.

Do not modify the Python model.

Do not retrain the crop model.

Do not add hardware.

Use simulated/manual values.

The first successful Android test should send:

{
    "N": 90,
    "P": 42,
    "K": 43,
    "temperature": 20.8,
    "humidity": 82,
    "ph": 6.5,
    "rainfall": 202.9
}

to:

POST /api/recommendations/crop

and display:

- recommended crop
- top 3 recommendations
- confidence percentages

After that works, build the Simulation Mode.

---

# 21. HARDWARE FUTURE

Future hardware architecture:

ESP32 DevKit V1
    ↓
Sensors
    ↓
Wi-Fi / HTTP or MQTT
    ↓
C# Backend

Planned sensors:

- RS485 NPK
- capacitive soil moisture
- pH
- EC
- DHT22
- rain sensor
- water flow
- water level

Planned actuators:

- 12V pump
- 12V solenoid valve
- 2-channel relay

Hardware is NOT currently available and must not be treated as a prerequisite.

---

# 22. PROJECT GOAL

AgriSense AI should eventually demonstrate:

1. Farm management
2. Simulated/live sensor monitoring
3. Crop recommendation
4. Fertilizer recommendation
5. Irrigation recommendation
6. Disease image analysis
7. AI farmer assistant
8. Weather information
9. Notifications
10. Irrigation automation architecture

The system must be modular and demonstrable even without physical hardware.