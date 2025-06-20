@echo off
echo ======================================
echo     TESTING FULL APP (ALL FEATURES)
echo ======================================
echo.

echo 1. Uninstalling existing app...
adb uninstall com.tdthanh.greenshop
echo.

echo 2. Installing FULL APP with all features...
adb install -r "app\build\outputs\apk\debug\app-debug.apk"
adb install -r "featureanalytics\build\outputs\apk\debug\featureanalytics-debug.apk"
adb install -r "featurepremium\build\outputs\apk\debug\featurepremium-debug.apk"
adb install -r "featureadvancedsearch\build\outputs\apk\debug\featureadvancedsearch-debug.apk"

if %ERRORLEVEL% NEQ 0 (
    echo Install failed!
    exit /b 1
)

echo.
echo 3. Starting app...
adb shell am start -n com.tdthanh.greenshop/.SplashActivity

echo.
echo 4. Monitoring logs for FeatureDownloadManager...
echo Look for lines showing installed modules and feature states.
echo.
echo Press Ctrl+C to stop monitoring
adb logcat -s "FeatureDownloadManager"
