@echo off
setlocal

cd /d "%~dp0.."
call gradlew.bat --gradle-user-home .gradle-user-home test
exit /b %errorlevel%
