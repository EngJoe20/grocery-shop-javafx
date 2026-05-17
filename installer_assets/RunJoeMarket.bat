@echo off
cd /d "%~dp0"

REM ── Try to find javaw automatically ──────────────────────
set JAVA_CMD=javaw

REM Check if bundled JRE exists (64-bit first, then 32-bit)
if exist "%~dp0jre64\bin\javaw.exe" (
    set JAVA_CMD="%~dp0jre64\bin\javaw.exe"
    goto :run
)
if exist "%~dp0jre32\bin\javaw.exe" (
    set JAVA_CMD="%~dp0jre32\bin\javaw.exe"
    goto :run
)

REM Fall back to system Java
where javaw >nul 2>nul
if %errorlevel% neq 0 (
    echo Java is not installed or not in PATH.
    echo Please install Java from: https://adoptium.net
    pause
    exit /b 1
)

:run
REM ── Launch the JAR ──────────────────────────────────────
start "" %JAVA_CMD% -jar "%~dp0JoeMarket-1.0.jar"
