/* Run once in SSMS after 001_initial_schema.sql. Safe to re-run. */
USE AgriSense;
GO
IF COL_LENGTH(N'dbo.Users', N'FirebaseUid') IS NULL
    ALTER TABLE dbo.Users ADD FirebaseUid NVARCHAR(128) NULL;
GO
IF COL_LENGTH(N'dbo.Users', N'MobileNumber') IS NULL
    ALTER TABLE dbo.Users ADD MobileNumber NVARCHAR(32) NULL;
GO
IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID(N'dbo.Users') AND name = N'PasswordHash' AND is_nullable = 0)
    ALTER TABLE dbo.Users ALTER COLUMN PasswordHash NVARCHAR(255) NULL;
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id = OBJECT_ID(N'dbo.Users') AND name = N'UX_Users_FirebaseUid')
    CREATE UNIQUE INDEX UX_Users_FirebaseUid ON dbo.Users(FirebaseUid) WHERE FirebaseUid IS NOT NULL;
GO
