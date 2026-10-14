"""Fertilizer type inference for the AgriSense fertilizer classifier."""
from pathlib import Path

import joblib
import numpy as np
import pandas as pd
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field


ROOT = Path(__file__).resolve().parents[1]
MODEL_PATH = ROOT / "model" / "fertilizer_recommendation_model.pkl"
FEATURES = ["crop", "soil_type", "nitrogen", "phosphorus", "potassium",
            "temperature", "humidity", "ph", "rainfall"]

app = FastAPI(title="AgriSense Fertilizer Recommendation Service", version="1.0.0")
model = None
model_error = None

try:
    model = joblib.load(MODEL_PATH)
    print("Fertilizer model loaded:", type(model).__name__)
except Exception as exc:  # Keep the service health endpoint available for diagnosis.
    model_error = str(exc)
    print("ERROR loading fertilizer model:", model_error)


class FertilizerRequest(BaseModel):
    crop: str = Field(..., min_length=1, max_length=50)
    soil_type: str = Field(..., min_length=1, max_length=50)
    nitrogen: float = Field(..., ge=0)
    phosphorus: float = Field(..., ge=0)
    potassium: float = Field(..., ge=0)
    temperature: float = Field(..., ge=-30, le=70)
    humidity: float = Field(..., ge=0, le=100)
    ph: float = Field(..., ge=0, le=14)
    rainfall: float = Field(..., ge=0)


def model_options():
    if model is None:
        return {"crops": [], "soilTypes": [], "fertilizers": []}
    encoder = model.named_steps["preprocessor"].named_transformers_["cat"]
    categories = encoder.categories_
    return {
        "crops": [str(x) for x in categories[0]],
        "soilTypes": [str(x) for x in categories[1]],
        "fertilizers": [str(x) for x in model.named_steps["model"].classes_],
    }


def canonical(value, choices, field_name):
    match = next((choice for choice in choices if choice.casefold() == value.strip().casefold()), None)
    if match is None:
        raise HTTPException(status_code=422, detail=f"Unsupported {field_name}. Supported values: {', '.join(choices)}")
    return match


@app.get("/health")
def health():
    return {"status": "healthy" if model is not None else "unavailable",
            "model_loaded": model is not None, "model_error": model_error}


@app.get("/metadata/fertilizer")
def metadata():
    if model is None:
        raise HTTPException(status_code=503, detail="Fertilizer model is not loaded. See service startup output.")
    return {"success": True, **model_options(), "inputFields": FEATURES}


@app.post("/predict/fertilizer")
def predict_fertilizer(request: FertilizerRequest):
    if model is None:
        raise HTTPException(status_code=503, detail="Fertilizer model is not loaded. See service startup output.")
    options = model_options()
    values = request.model_dump()
    values["crop"] = canonical(values["crop"], options["crops"], "crop")
    values["soil_type"] = canonical(values["soil_type"], options["soilTypes"], "soil type")
    frame = pd.DataFrame([[values[name] for name in FEATURES]], columns=FEATURES)
    try:
        probabilities = model.predict_proba(frame)[0]
        prediction = str(model.predict(frame)[0])
        top = np.argsort(probabilities)[::-1][:3]
        recommendations = [{"fertilizer": str(model.classes_[i]),
                            "confidence": round(float(probabilities[i]), 4),
                            "confidencePercentage": round(float(probabilities[i]) * 100, 2)}
                           for i in top]
        return {"success": True, "recommendedFertilizer": prediction,
                "recommendations": recommendations, "input": values,
                "note": "The model predicts fertilizer type only; it does not calculate an application rate."}
    except Exception as exc:
        raise HTTPException(status_code=500, detail=f"Fertilizer prediction failed: {exc}") from exc
