@echo off
echo ================================
echo  Device App Status Check
echo ================================

echo Checking connected devices...
adb devices
echo.

echo Checking if GreenShop is installed...
adb shell pm list packages | findstr greenshop
if %ERRORLEVEL% neq 0 (
    echo GreenShop is NOT installed
) else (
    echo GreenShop is installed
)
echo.

echo Checking app details...
adb shell dumpsys package com.tdthanh.greenshop | findstr "versionName\|versionCode\|targetSdk\|splits"
echo.

echo Checking running processes...
adb shell ps | findstr greenshop
echo.

echo App data location:
adb shell ls -la /data/data/com.tdthanh.greenshop 2>nul
echo.

echo ================================
echo  Quick Actions
echo ================================
echo 1. Start app: adb shell am start -n com.tdthanh.greenshop/.SplashActivity
echo 2. Stop app: adb shell am force-stop com.tdthanh.greenshop
echo 3. Clear data: adb shell pm clear com.tdthanh.greenshop
echo 4. Uninstall: adb uninstall com.tdthanh.greenshop
echo.
pause
