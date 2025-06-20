@echo off
echo ================================
echo  APK Sizes Analysis
echo ================================

echo Base APK:
for %%I in ("app\build\outputs\apk\debug\app-debug.apk") do echo   Size: %%~zI bytes (%%~zI / 1024 / 1024 MB)

echo.
echo Feature APKs:
for %%I in ("featureanalytics\build\outputs\apk\debug\featureanalytics-debug.apk") do echo   Analytics: %%~zI bytes
for %%I in ("featurepremium\build\outputs\apk\debug\featurepremium-debug.apk") do echo   Premium: %%~zI bytes  
for %%I in ("featureadvancedsearch\build\outputs\apk\debug\featureadvancedsearch-debug.apk") do echo   Advanced Search: %%~zI bytes

echo.
echo Total if all installed:
set /a total=0
for %%I in ("app\build\outputs\apk\debug\app-debug.apk") do set /a total+=%%~zI
for %%I in ("featureanalytics\build\outputs\apk\debug\featureanalytics-debug.apk") do set /a total+=%%~zI
for %%I in ("featurepremium\build\outputs\apk\debug\featurepremium-debug.apk") do set /a total+=%%~zI
for %%I in ("featureadvancedsearch\build\outputs\apk\debug\featureadvancedsearch-debug.apk") do set /a total+=%%~zI
echo   Total: %total% bytes

echo.
echo ================================
echo  Bundle Size (for Production)
echo ================================
for %%I in ("app\build\outputs\bundle\release\app-release.aab") do echo   Bundle: %%~zI bytes

echo.
pause
