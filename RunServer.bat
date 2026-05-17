@echo off
title JoeMarket - Grocery Server
cd /d "%~dp0"
echo ==============================================
echo        Starting JoeMarket Grocery Server
echo ==============================================
echo.
java -cp target\JoeMarket-1.0.jar server.GroceryServer
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Failed to start server. Make sure Java is installed and target\JoeMarket-1.0.jar exists.
    pause
)
