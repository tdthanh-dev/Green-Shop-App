@echo off
echo ======================================
echo     TESTING DYNAMIC FEATURES
echo ======================================
echo.

echo 1. Building debug APK...
call gradlew assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    exit /b 1
)

echo.
echo 2. Installing base app only...
adb install -r "app\build\outputs\apk\debug\app-debug.apk"
if %ERRORLEVEL% NEQ 0 (
    echo Base app install failed!
    exit /b 1
)

echo.
echo 3. Checking app size after base install...
adb shell pm list packages | findstr com.tdthanh.greenshop
adb shell dumpsys package com.tdthanh.greenshop | findstr "codeSize\|dataSize"

echo.
echo 4. Starting app...
adb shell am start -n com.tdthanh.greenshop/.SplashActivity

echo.
echo 5. Testing instructions:
echo    - Navigate to "Quản lý tính năng" from main app
echo    - Try downloading each feature (featureanalytics, featurepremium, featureadvancedsearch)
echo    - Verify that "Tải xuống" button appears for uninstalled features
echo    - After download, verify "Mở" button appears
echo    - Click "Mở" to navigate to feature UI
echo    - Try "Gỡ" to uninstall and verify state changes back to "Tải xuống"
echo.

echo 6. Monitor logs with:
echo    adb logcat -s "FeatureDownloadManager"
echo.

echo Test completed! The app is now running on your device.
pause
