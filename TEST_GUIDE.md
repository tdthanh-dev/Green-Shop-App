# GreenShop Dynamic Features - Test Guide

## Kích thước App
- **Base App**: ~22.7 MB
- **Analytics Feature**: ~48 KB
- **Premium Feature**: ~56 KB  
- **Advanced Search Feature**: ~1.6 MB
- **Bundle (Production)**: ~23.3 MB

## Test Local (Debug Mode)

### 1. Cài đặt trên thiết bị/emulator
```bash
# Cài đặt tất cả APKs
.\install_debug.bat

# Hoặc thủ công:
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb install -r featureanalytics\build\outputs\apk\debug\featureanalytics-debug.apk
adb install -r featurepremium\build\outputs\apk\debug\featurepremium-debug.apk
adb install -r featureadvancedsearch\build\outputs\apk\debug\featureadvancedsearch-debug.apk
```

### 2. Khởi động app
```bash
adb shell am start -n com.tdthanh.greenshop/.SplashActivity
```

### 3. Xem logs
```bash
.\watch_logs.bat
# Hoặc:
adb logcat -s FeatureDownloadManager:D GreenShop:D MainActivity:D
```

### 4. Test Features
1. Mở app → tap Profile tab → tap "Quản lý tính năng"
2. Thấy 3 dynamic features: Analytics, Premium, Advanced Search
3. Tap "Tải xuống" để test simulation download
4. Sau khi download xong, tap "Mở" để test UI loading

## Chế độ hoạt động

### Debug Mode (Test Local)
- ✅ **Simulation mode** - Giả lập download process
- ✅ **Không cần Play Store** 
- ✅ **Load UI thật từ dynamic features**
- ✅ **Có thể test offline**

### Release Mode (Production)
- ✅ **Play Core API thực tế**
- ✅ **Download từ Play Store**
- ✅ **Thực sự tăng/giảm app size**
- ✅ **On-demand installation**

## Test Scenarios

### 1. App Size Verification
- Install chỉ base app → đo app size
- Download từng feature → đo sự thay đổi app size
- Uninstall features → verify size giảm

### 2. UI Loading Test  
- Kiểm tra UI được load từ dynamic feature thực tế
- Không phải placeholder UI
- Navigation hoạt động đúng

### 3. Network Conditions
- Test download với network chậm
- Test offline behavior
- Test download failure handling

## Commands hữu ích

```bash
# Build commands
.\gradlew assembleDebug          # Build debug APKs
.\gradlew bundleRelease         # Build production bundle

# Device management
adb devices                     # List devices
adb shell pm list packages | findstr greenshop  # Check installed packages
adb shell dumpsys package com.tdthanh.greenshop # Package details

# Size checking
.\check_sizes.bat              # Check APK/Bundle sizes
```

## Troubleshooting

### Play Core Errors trong Debug
- Normal - debug mode sử dụng simulation
- Real Play Core chỉ hoạt động với signed builds từ Play Store

### Dynamic Feature không load
- Check logs với `.\watch_logs.bat`
- Verify all APKs installed correctly
- Restart app sau khi install features

### UI không hiện
- Check DynamicFeatureEntry classes exists
- Verify navigation setup
- Check reflection loading trong MainActivity

## Test trên Android Studio

### 1. Run Configurations
Bạn sẽ thấy 2 run configurations trong Android Studio:
- **"Base App Only"** - Chỉ cài base app (để test dynamic download)
- **"Full App (All Features)"** - Cài tất cả features

### 2. Test Dynamic Features trong Android Studio

#### A. Test Base App + Dynamic Download
1. **Chọn "Base App Only" configuration**
2. **Run app** (Shift+F10)
3. **Mở Logcat** để xem logs
4. **Test download:**
   - Tap Profile → "Quản lý tính năng"
   - Tap "Tải xuống" cho feature bất kỳ
   - Xem simulation process trong Logcat
   - Tap "Mở" để test UI loading

#### B. Test Full App
1. **Chọn "Full App (All Features)" configuration**
2. **Run app** - tất cả features sẽ được pre-installed
3. **Test UI** của tất cả features

### 3. Debugging trong Android Studio

#### Đặt Breakpoints
```kotlin
// Trong FeatureDownloadManager.kt
fun downloadFeature(featureName: String) {
    // Đặt breakpoint ở đây để debug download logic
    Log.d(TAG, "Attempting to download feature: $featureName")
    
    if (isDebugMode) {
        // Đặt breakpoint để xem simulation flow
        simulateDownload(featureName)
    }
}

// Trong MainActivity.kt
private fun openDynamicFeature(featureName: String) {
    // Đặt breakpoint để debug UI loading
    val entry = DynamicFeatureEntryFactory.getEntryForFeature(featureName)
}
```

#### Logcat Filters
Tạo filter trong Logcat với tags:
- `FeatureDownloadManager` - Download/install logs
- `MainActivity` - Navigation logs
- `GreenShop` - General app logs

### 4. Testing Scenarios

#### Scenario 1: App Size Verification
```kotlin
// Trong Android Studio Terminal:
adb shell pm list packages -s | findstr greenshop  // Check installed splits
adb shell dumpsys package com.tdthanh.greenshop   // Check app details
```

#### Scenario 2: Memory Profiling
1. **Start Profiler** từ Android Studio
2. **Monitor memory usage** trước và sau download
3. **Check UI performance** khi load dynamic features

#### Scenario 3: Network Simulation
1. **Device Manager** → Advanced → Network Speed
2. **Set to slow network** để test download progress
3. **Monitor download states** trong debug mode

### 5. Live Templates (Shortcuts)

Tạo live templates trong Android Studio:

#### Template: Feature Log
```kotlin
Log.d("FeatureDownloadManager", "$feature$ - $message$")
```

#### Template: Breakpoint Log
```kotlin
// Feature: $feature$ | State: $state$ | Debug: $debug$
```

### 6. Troubleshooting trong Android Studio

#### Problem: Dynamic Feature không load
**Solution:**
1. Check Build Variants: app, featureanalytics, featurepremium, featureadvancedsearch
2. Clean Project → Rebuild Project
3. Invalidate Caches and Restart

#### Problem: Simulation không hoạt động
**Solution:**
1. Verify `isDebugMode = true` trong debug build
2. Check Logcat cho error messages
3. Set breakpoint trong `simulateDownload()`

#### Problem: UI không render
**Solution:**
1. Check DynamicFeatureEntry classes
2. Verify Compose dependencies
3. Debug reflection loading

### 7. Performance Testing

#### Memory Usage
- **Base app memory**: Monitor trước download
- **After download**: Verify memory increase
- **UI rendering**: Check for memory leaks

#### Build Time Analysis
```bash
# Trong Android Studio Terminal:
.\gradlew build --profile    # Generate build profile
```

### 8. Advanced Testing

#### Custom Run Configurations
Tạo custom config cho specific testing:
- **Only Analytics**: Disable premium + search
- **Only Premium**: Disable analytics + search  
- **Only Search**: Disable analytics + premium

#### Gradle Tasks
```bash
# Test specific modules
.\gradlew :app:testDebugUnitTest
.\gradlew :featureanalytics:testDebugUnitTest
.\gradlew :featurepremium:testDebugUnitTest
.\gradlew :featureadvancedsearch:testDebugUnitTest
```
