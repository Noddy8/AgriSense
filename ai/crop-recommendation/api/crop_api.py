from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
import joblib
import numpy as np
import os


# --------------------------------------------------
# FASTAPI APPLICATION
# --------------------------------------------------

app = FastAPI(
    title="AgriSense AI Crop Recommendation Service",
    description="Machine Learning Crop Recommendation API",
    version="1.0.0"
)


# --------------------------------------------------
# MODEL PATH
# --------------------------------------------------

BASE_DIR = os.path.dirname(
    os.path.dirname(os.path.abspath(__file__))
)

MODEL_PATH = os.path.join(
    BASE_DIR,
    "model",
    "crop_model.pkl"
)


# --------------------------------------------------
# LOAD MODEL
# --------------------------------------------------

try:

    model = joblib.load(MODEL_PATH)

    print("Crop model loaded successfully.")
    print("Model:", type(model))
    print("Features:", model.feature_names_in_)

except Exception as e:

    print("ERROR: Could not load crop model.")
    print(e)

    model = None


# --------------------------------------------------
# REQUEST MODEL
# --------------------------------------------------

class CropRequest(BaseModel):

    N: float = Field(
        ...,
        description="Nitrogen"
    )

    P: float = Field(
        ...,
        description="Phosphorus"
    )

    K: float = Field(
        ...,
        description="Potassium"
    )

    temperature: float = Field(
        ...,
        description="Temperature in Celsius"
    )

    humidity: float = Field(
        ...,
        description="Relative humidity percentage"
    )

    ph: float = Field(
        ...,
        description="Soil pH"
    )

    rainfall: float = Field(
        ...,
        description="Rainfall"
    )


# --------------------------------------------------
# ROOT
# --------------------------------------------------

@app.get("/")
def root():

    return {
        "service": "AgriSense AI Crop Recommendation",
        "status": "running"
    }


# --------------------------------------------------
# HEALTH CHECK
# --------------------------------------------------

@app.get("/health")
def health():

    return {
        "status": "healthy",
        "model_loaded": model is not None
    }


# --------------------------------------------------
# CROP PREDICTION
# --------------------------------------------------

@app.post("/predict/crop")
def predict_crop(request: CropRequest):

    if model is None:

        raise HTTPException(
            status_code=500,
            detail="Crop model is not loaded."
        )

    try:

        # IMPORTANT:
        # Feature order MUST match the trained model.

        features = np.array([[
            request.N,
            request.P,
            request.K,
            request.temperature,
            request.humidity,
            request.ph,
            request.rainfall
        ]])

        # --------------------------------------------------
        # PREDICTION
        # --------------------------------------------------

        prediction = model.predict(features)[0]

        # --------------------------------------------------
        # PROBABILITIES
        # --------------------------------------------------

        probabilities = model.predict_proba(features)[0]

        # --------------------------------------------------
        # TOP 3 CROPS
        # --------------------------------------------------

        indices = np.argsort(
            probabilities
        )[::-1][:3]

        recommendations = []

        for index in indices:

            crop = model.classes_[index]

            confidence = float(
                probabilities[index]
            )

            recommendations.append({
                "crop": str(crop),
                "confidence": round(
                    confidence,
                    4
                ),
                "confidencePercentage": round(
                    confidence * 100,
                    2
                )
            })


        # --------------------------------------------------
        # RESPONSE
        # --------------------------------------------------

        return {

            "success": True,

            "recommendedCrop": str(
                prediction
            ),

            "recommendations":
                recommendations,

            "input": {

                "N": request.N,
                "P": request.P,
                "K": request.K,

                "temperature":
                    request.temperature,

                "humidity":
                    request.humidity,

                "ph":
                    request.ph,

                "rainfall":
                    request.rainfall
            }
        }


    except Exception as e:

        raise HTTPException(
            status_code=500,
            detail=str(e)
        )