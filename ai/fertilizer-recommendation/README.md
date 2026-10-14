# Fertilizer recommendation service

This service loads the user-supplied `fertilizer_recommendation_model.pkl`. Its pipeline expects the named inputs `crop`, `soil_type`, `nitrogen`, `phosphorus`, `potassium`, `temperature`, `humidity`, `rainfall`; categorical values and output labels come from the model itself. It predicts a fertilizer category and confidence, not a safe quantity or application schedule.

The attached pickle declares scikit-learn **1.8.0**. This service has its own dependencies so the existing crop model environment remains pinned to scikit-learn 1.6.1. Use Python 3.11 or newer for this environment.

From the repository root, create and install an environment (one time):

```powershell
py -3.12 -m venv ai/fertilizer-recommendation/.venv
ai/fertilizer-recommendation/.venv/Scripts/python.exe -m pip install -r ai/fertilizer-recommendation/requirements.txt
```

Start it on port 8001 (from the repository root):

```powershell
ai/fertilizer-recommendation/.venv/Scripts/python.exe -m uvicorn api.fertilizer_api:app --app-dir ai/fertilizer-recommendation --host 127.0.0.1 --port 8001
```

Check `http://127.0.0.1:8001/health` and `/metadata/fertilizer` before using the Android screen. If `py -3.12` is unavailable, use an installed Python 3.11+ executable.
