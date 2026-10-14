# AgriSense sensor simulator

This standalone Python process generates bounded random-walk sensor readings and stores them through the C# API. The reading source is always `simulation` and is shown as Simulation Mode in the app. The input vector remains compatible with the existing crop model (N, P, K, temperature, humidity, pH, rainfall).

Start the C# API and SQL Server first, then run:

```powershell
python ai/sensor-simulator/simulator.py --api http://localhost:64719 --farm-id 1 --scenario normal
```

Scenarios: `normal`, `low-rainfall`, `high-temperature`, `low-nitrogen`, `high-humidity`.
Use `--once` for a single sample. Default updates are every 10 seconds.
