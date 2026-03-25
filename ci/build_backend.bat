@echo off
setlocal

cd /d "%~dp0.."
call gradlew.bat --gradle-user-home .gradle-user-home clean build -x test
exit /b %errorlevel%