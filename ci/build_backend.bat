@echo off
setlocal

cd /d "%~dp0.."
call gradlew.bat clean build -x test
exit /b %errorlevel%
