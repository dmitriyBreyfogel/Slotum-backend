@echo off
setlocal EnableExtensions EnableDelayedExpansion

cd /d "%~dp0.."

set "APP_JAR="

for /f "delims=" %%f in ('dir /b /o:-d build\libs\*.jar ^| findstr /v /i "plain"') do (
  set "APP_JAR=build\libs\%%f"
  goto :jar_found
)

echo [SMOKE] Boot jar not found in build\libs
exit /b 1

:jar_found
echo [SMOKE] Starting %APP_JAR%
start "slotum-smoke" /b cmd /c java -jar "%APP_JAR%" --server.port=8081

echo [SMOKE] Waiting for startup...
ping 127.0.0.1 -n 16 >nul

netstat -ano | findstr ":8081" >nul 2>&1
if errorlevel 1 (
  echo [SMOKE] Port 18081 is not listening. Startup failed.
  taskkill /FI "WINDOWTITLE eq slotum-smoke" /T /F >nul 2>&1
  exit /b 1
)

echo [SMOKE] Startup check passed.
taskkill /FI "WINDOWTITLE eq slotum-smoke" /T /F >nul 2>&1
exit /b 0
