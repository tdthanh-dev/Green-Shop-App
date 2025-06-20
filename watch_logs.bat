@echo off
echo ================================
echo  Watching GreenShop Logs
echo ================================
echo Press Ctrl+C to stop watching logs
echo.

adb logcat -s FeatureDownloadManager:D GreenShop:D MainActivity:D
