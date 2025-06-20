@echo off
echo ================================
echo  Installing Debug APKs
echo ================================

echo Installing Base APK...
adb install -r "app\build\outputs\apk\debug\app-debug.apk"
if %ERRORLEVEL% neq 0 (
    echo Failed to install base APK
    pause
    exit /b 1
)

echo Installing Analytics Feature APK...
adb install -r "featureanalytics\build\outputs\apk\debug\featureanalytics-debug.apk"

echo Installing Premium Feature APK...
adb install -r "featurepremium\build\outputs\apk\debug\featurepremium-debug.apk"

echo Installing Advanced Search Feature APK...
adb install -r "featureadvancedsearch\build\outputs\apk\debug\featureadvancedsearch-debug.apk"

echo ================================
echo  Installation Complete!
echo ================================
echo.
echo The app is now installed with simulation mode enabled.
echo You can test dynamic feature download without needing Play Store.
echo.
echo To start the app:
echo adb shell am start -n com.tdthanh.greenshop/.SplashActivity
echo.
pause
