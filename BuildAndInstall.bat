@echo off
chcp 65001 >nul
title JoeMarket - Build Tool

echo.
echo  +------------------------------------------+
echo  ^|   JoeMarket - Build ^& Installer Tool     ^|
echo  +------------------------------------------+
echo.

REM ── Move to project root (where pom.xml is) ─────────────
REM The .bat must be placed next to pom.xml
set PROJECT_DIR=%~dp0
cd /d "%PROJECT_DIR%"

REM Verify pom.xml exists here
if not exist "pom.xml" (
    echo  [ERROR] pom.xml not found in: %PROJECT_DIR%
    echo  Make sure BuildAndInstall.bat is in the same folder as pom.xml
    pause
    exit /b 1
)

REM ── 1. Clean old build ───────────────────────────────────
echo [1/4] Cleaning old build...
if exist "target" (
    rmdir /s /q "target"
    echo       Old target folder deleted.
) else (
    echo       No old build found.
)
if exist "installer_output" (
    rmdir /s /q "installer_output"
    echo       Old installer deleted.
) else (
    echo       No old installer found.
)
echo.

REM ── 2. Build JAR ─────────────────────────────────────────
echo [2/4] Building JAR with Maven...
call mvn package -q
if %errorlevel% neq 0 (
    echo.
    echo  [ERROR] Maven build failed!
    pause
    exit /b 1
)
echo       JAR built successfully.
echo.

REM ── 3. Copy JAR to installer_assets ──────────────────────
echo [3/4] Copying JAR...
if not exist "installer_assets" mkdir "installer_assets"
copy /y "target\JoeMarket-1.0.jar" "installer_assets\JoeMarket-1.0.jar" >nul
echo       Done.
echo.

REM ── 3.5 Compile Native GUI Launcher EXE ─────────────────
echo [3.5/4] Compiling native GUI launcher JoeMarket.exe...
"C:\Windows\Microsoft.NET\Framework64\v4.0.30319\csc.exe" /target:winexe /out:"installer_assets\JoeMarket.exe" /win32icon:"installer_assets\icon.ico" /reference:System.Windows.Forms.dll "installer_assets\Launcher.cs" >nul
if %errorlevel% neq 0 (
    echo  [ERROR] Native launcher compilation failed!
    pause
    exit /b 1
)
echo       Native launcher JoeMarket.exe compiled successfully.
echo.

REM ── 4. Build installer with Inno Setup ───────────────────
echo [4/4] Building installers with Inno Setup...

set ISCC=""
if exist "C:\Program Files (x86)\Inno Setup 6\ISCC.exe" set ISCC="C:\Program Files (x86)\Inno Setup 6\ISCC.exe"
if exist "C:\Program Files\Inno Setup 6\ISCC.exe"       set ISCC="C:\Program Files\Inno Setup 6\ISCC.exe"
if exist "C:\Program Files (x86)\Inno Setup 5\ISCC.exe" set ISCC="C:\Program Files (x86)\Inno Setup 5\ISCC.exe"

if %ISCC%== "" (
    echo  [ERROR] Inno Setup not found!
    echo  Download from: https://jrsoftware.org/isdl.php
    pause
    exit /b 1
)

echo       Building 64-bit Installer...
%ISCC% /DAppArch=x64 "JoeMarket.iss"
if %errorlevel% neq 0 (
    echo  [ERROR] 64-bit Inno Setup build failed!
    pause
    exit /b 1
)

echo.
echo       Building 32-bit Installer...
%ISCC% /DAppArch=x86 "JoeMarket.iss"
if %errorlevel% neq 0 (
    echo  [ERROR] 32-bit Inno Setup build failed!
    pause
    exit /b 1
)

echo.
echo  +-----------------------------------------------+
echo  ^| Done! Installers are in installer_output\     ^|
echo  +-----------------------------------------------+
echo.
explorer "installer_output"
pause