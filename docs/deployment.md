# AgriSense deployment

The deployment uses Azure App Service (Windows) for the existing ASP.NET Framework API, Azure SQL Database for the existing SQL Server database, and two Render web services for the crop and fertilizer FastAPI models. The scenario simulator is already part of the ASP.NET API.

## 1. Deploy the Python services

Connect this repository to Render and create a Blueprint from `render.yaml`. It creates `agrisense-crop-ai` and `agrisense-fertilizer-ai`. In each service, set `SERVICE_API_KEY` to the same long random secret. Save the two service URLs for the API configuration. Both services expose `/health`; predictions and fertilizer metadata require the service key.

Render Free services sleep after 15 minutes without traffic, and the first request may take about a minute to wake them. The API timeout is set to allow for that cold start. Free hosting is suitable for a demo, not production availability.

## 2. Copy the existing SQL Server database

Keep the current database schema and data:

1. In SQL Server Management Studio, right-click the local `AgriSense` database and choose **Tasks > Export Data-tier Application** to create a `.bacpac` file.
2. Create an Azure SQL logical server and database using the Azure SQL free offer. Choose the option to pause the database if it reaches the monthly free allowance.
3. Import the `.bacpac` into the Azure SQL database. The schema scripts in `backend/database` are for a fresh database; do not run them over the imported database.
4. Set the Azure SQL firewall to allow the App Service's outbound addresses. Do not expose SQL Server on the home/office network.

The current schema uses ordinary SQL Server tables, identity columns, filtered indexes, and T-SQL supported by Azure SQL. Review the import report before switching the app over.

## 3. Deploy the .NET API

Create a **Windows** Azure App Service using the ASP.NET Framework runtime and the Free F1 plan. Publish `backend/Agrisense.api/agrisense.api.sln` / `agrisense.api` from Visual Studio to that Web App. The project currently targets .NET Framework 4.6.1; App Service runs the compatible .NET Framework 4.x runtime.

In **App Service > Configuration**, add these application settings:

| Name | Value |
| --- | --- |
| `AGRISENSE_DB_CONNECTION_STRING` | Azure SQL connection string with SQL authentication and `Encrypt=True;TrustServerCertificate=False` |
| `CROP_AI_BASE_URL` | Render crop service URL, ending in `/` |
| `FERTILIZER_AI_BASE_URL` | Render fertilizer service URL, ending in `/` |
| `AI_SERVICE_API_KEY` | The same secret used as `SERVICE_API_KEY` on both Render services |

Keep the connection string and key in host settings; never commit their values. Verify `https://<app-name>.azurewebsites.net/api/health` and test a crop and fertilizer prediction. Confirm the Azure SQL database has the expected existing data before changing the mobile app URL.

## 4. Build the Android app against the hosted API

From `mobile/AgriSenseAI`, build with the deployed API URL (keep `/api/` at the end):

```powershell
.\gradlew.bat assembleDebug -PAGRISENSE_API_BASE_URL=https://<app-name>.azurewebsites.net/api/
```

The default remains `http://localhost:64719/api/` for local development. The Firebase ID-token flow is unchanged.

## Local development fallbacks

The .NET API reads `AGRISENSE_DB_CONNECTION_STRING`, `CROP_AI_BASE_URL`, and `FERTILIZER_AI_BASE_URL` from the process environment. If the AI URLs are not set, it uses `127.0.0.1:8000` and `127.0.0.1:8001`. If no database environment variable is set, it uses the `AgriSenseDb` connection string in `Web.config`.
