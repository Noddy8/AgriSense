import os
import joblib
import numpy as np

# Get the project root:
# crop-recommendation/
BASE_DIR = os.path.dirname(
    os.path.dirname(os.path.abspath(__file__))
)

MODEL_PATH = os.path.join(
    BASE_DIR,
    "model",
    "crop_model.pkl"
)

print("Loading model from:")
print(MODEL_PATH)

model = joblib.load(MODEL_PATH)

print("\nModel loaded successfully.")

print("Model type:")
print(type(model))

print("\nNumber of features:")
print(model.n_features_in_)

print("\nFeature names:")
print(model.feature_names_in_)

print("\nCrop classes:")
print(model.classes_)


# --------------------------------------------------
# TEST INPUT
# --------------------------------------------------
# IMPORTANT:
# N
# P
# K
# temperature
# humidity
# ph
# rainfall

sample = np.array([[
    90,
    42,
    43,
    20.8,
    82.0,
    6.5,
    202.9
]])


# Prediction
prediction = model.predict(sample)

print("\n-----------------------------")
print("Predicted Crop:")
print(prediction[0])
print("-----------------------------")


# Prediction probabilities
probabilities = model.predict_proba(sample)[0]

# Sort from highest probability to lowest
indices = np.argsort(probabilities)[::-1]


print("\nTop 3 Recommendations:")

for position, index in enumerate(indices[:3], start=1):

    crop = model.classes_[index]
    probability = probabilities[index]

    print(
        f"{position}. "
        f"{crop} - "
        f"{probability * 100:.2f}%"
    )