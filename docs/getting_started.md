# AgriSense local prototype

The running slice uses Android Java/XML → C# ASP.NET Web API → SQL Server. AI inference stays in Python. Crop AI and fertilizer AI run as separate processes because the supplied models were serialized with different scikit-learn versions. Simulated readings are clearly labeled and stored with source `simulation`.

## 1. Database

In SSMS, connect to the SQL Server instance configured in `backend/Agrisense.api/agrisense.api/Web.config` (`DEEP` with Windows Authentication in this checkout). Open and execute `backend/database/001_initial_schema.sql` on that connection. It creates `AgriSense`, its tables and the demo farm (ID 1). Confirm that `SELECT FarmId, Name, SoilType FROM AgriSense.dbo.Farms` returns a row.

## 2. Start crop AI

Use the existing crop environment and run from `ai/crop-recommendation`:

```powershell
..\..\ai\crop-recommendation\venv\Scripts\python.exe -m uvicorn api.crop_api:app --app-dir . --host 127.0.0.1 --port 8000
```

Check `http://127.0.0.1:8000/health`. This environment retains scikit-learn 1.6.1 for `crop_model.pkl`.

## 3. Start fertilizer AI

The attached fertilizer model uses scikit-learn 1.8.0. Create its separate environment once, with Python 3.11 or newer:

```powershell
py -3.12 -m venv ai/fertilizer-recommendation/.venv
ai/fertilizer-recommendation/.venv/Scripts/python.exe -m pip install -r ai/fertilizer-recommendation/requirements.txt
```

Start the model API from the repository root:

```powershell
ai/fertilizer-recommendation/.venv/Scripts/python.exe -m uvicorn api.fertilizer_api:app --app-dir ai/fertilizer-recommendation --host 127.0.0.1 --port 8001
```

Check `http://127.0.0.1:8001/health` and `http://127.0.0.1:8001/metadata/fertilizer`. The metadata endpoint should list supported crop, soil and fertilizer classes before recommendations will work.

## 4. Start C# API

Open `backend/Agrisense.api/agrisense.api.sln` in Visual Studio, restore NuGet packages if needed, and run `agrisense.api`. IIS Express uses port `64719`. Check `http://localhost:64719/api/health` and `http://localhost:64719/api/farms`.

Implemented mobile API calls include farms, dashboard/latest readings, sensor history, simulation presets, crop recommendations and fertilizer recommendations. Fertilizer inference uses the selected farm's latest N/P/K/temperature/humidity/rainfall, plus crop and soil choices, and stores the model response in `FertilizerRecommendations`.

## 5. Generate simulated readings

For a continuous random-walk feed, in another repository-root terminal:

```powershell
python ai/sensor-simulator/simulator.py --api http://localhost:64719 --farm-id 1 --scenario normal
```

Use `normal`, `low-rainfall`, `high-temperature`, `low-nitrogen` or `high-humidity`; stop with Ctrl+C. The recommendation screen also lets you apply one preset immediately through the C# API before requesting a new result.

## 6. Run Android

Open `mobile/AgriSenseAI` in Android Studio and run the app on an Android Studio emulator. Its Retrofit API root is `http://10.0.2.2:64719/api/`. For a physical phone, change `BASE_URL` in `app/src/main/java/com/ninjaTurtles/agrisense/network/ApiClient.java` to the development computer's LAN IP, and make the API reachable from the phone.

The Home screen and Farms screen load server data instead of sample sensor/farm records. Crop and fertilizer cards open model-backed recommendations; fertilizer uses the model-provided options and shows confidence. It predicts fertilizer **type only**, not quantity or application schedule. Simulation readings flow into the dashboard automatically as it refreshes.

## Current boundaries

The crop and fertilizer models, simulated sensor flow, farm/dashboard/history endpoints and related Android screens are connected. Authentication is not integrated with the backend; irrigation controls, weather, notifications, plant-disease analysis and the assistant still contain demo behavior because corresponding backend services are not implemented. Unknown water-tank readings are shown as unavailable rather than invented.
