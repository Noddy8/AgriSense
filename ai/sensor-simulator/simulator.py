"""Generate realistic, clearly labelled sensor-like readings for AgriSense."""
import argparse
import json
import random
import time
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen

SCENARIOS = ("normal", "low-rainfall", "high-temperature", "low-nitrogen", "high-humidity")


def make_reading(scenario, previous=None):
    """Return an in-range reading with small variation around a chosen scenario."""
    base = {
        "N": 90.0, "P": 42.0, "K": 43.0, "temperature": 20.8,
        "humidity": 82.0, "ph": 6.5, "rainfall": 202.9, "soilMoisture": 42.0,
    }
    if scenario == "low-rainfall":
        base.update(rainfall=38.0, soilMoisture=24.0)
    elif scenario == "high-temperature":
        base.update(temperature=36.0, humidity=53.0, soilMoisture=30.0)
    elif scenario == "low-nitrogen":
        base.update(N=24.0)
    elif scenario == "high-humidity":
        base.update(humidity=96.0, soilMoisture=66.0)
    elif scenario != "normal":
        raise ValueError("Unknown scenario: " + scenario)

    # A bounded random walk makes updates feel continuous rather than jumping.
    ranges = {"N": (0, 140), "P": (0, 145), "K": (0, 205),
              "temperature": (-5, 50), "humidity": (0, 100), "ph": (3, 10),
              "rainfall": (0, 350), "soilMoisture": (0, 100)}
    result = {}
    for key, target in base.items():
        old = (previous or {}).get(key, target)
        value = old + (target - old) * 0.25 + random.uniform(-1.8, 1.8)
        low, high = ranges[key]
        result[key] = round(max(low, min(high, value)), 1)
    return result


def post_reading(api, farm_id, values):
    payload = {"farmId": farm_id, "source": "simulation", **values}
    request = Request(api.rstrip("/") + "/api/sensors/readings",
                      data=json.dumps(payload).encode("utf-8"),
                      headers={"Content-Type": "application/json"}, method="POST")
    with urlopen(request, timeout=8) as response:
        return json.loads(response.read().decode("utf-8"))


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--api", default="http://localhost:64719", help="C# backend root URL")
    parser.add_argument("--farm-id", type=int, default=1)
    parser.add_argument("--scenario", choices=SCENARIOS, default="normal")
    parser.add_argument("--interval", type=float, default=10.0, help="Seconds between readings")
    parser.add_argument("--once", action="store_true", help="Send one reading then exit")
    args = parser.parse_args()
    previous = None
    print("AgriSense Simulation Mode | scenario={} | farm={} | interval={}s".format(args.scenario, args.farm_id, args.interval))
    while True:
        values = make_reading(args.scenario, previous)
        try:
            saved = post_reading(args.api, args.farm_id, values)
            print("Saved simulation reading #{}: {}".format(saved.get("readingId", "?"), json.dumps(values)))
            previous = values
        except (HTTPError, URLError, TimeoutError) as error:
            print("Backend unavailable ({}); retrying.".format(error))
        if args.once:
            break
        time.sleep(max(2.0, args.interval))


if __name__ == "__main__":
    main()
