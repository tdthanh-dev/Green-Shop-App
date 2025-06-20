/**
 * Debug Helper cho Dynamic Features Testing
 * Copy code này vào FeatureDownloadManager để thêm debug logs
 */

// Thêm vào đầu class FeatureDownloadManager
private fun debugLog(message: String, feature: String = "Unknown") {
    if (isDebugMode) {
        Log.d("DEBUG_FEATURE", "[$feature] $message")
        // Đặt breakpoint ở đây để debug dễ dàng
        println("🔍 DEBUG: [$feature] $message") 
    }
}

// Thêm debug calls vào các methods chính:

fun downloadFeature(featureName: String) {
    debugLog("Starting download process", featureName)
    
    if (isFeatureInstalled(featureName)) {
        debugLog("Feature already installed", featureName)
        return
    }
    
    if (isDebugMode) {
        debugLog("Using simulation mode", featureName)
        simulateDownload(featureName)
    } else {
        debugLog("Using real Play Core API", featureName)
        // Play Core logic
    }
}

private fun simulateDownload(featureName: String) {
    debugLog("Simulation started", featureName)
    
    // Breakpoint ở đây để debug simulation steps
    _downloadState.value = FeatureDownloadState.Pending
    debugLog("State: Pending", featureName)
    
    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
        debugLog("State: Downloading 30%", featureName)
        _downloadState.value = FeatureDownloadState.Downloading(30)
    }, 500)
    
    // ... các steps khác
}

fun isFeatureInstalled(featureName: String): Boolean {
    val installed = if (isDebugMode) {
        simulatedInstalledFeatures.contains(featureName)
    } else {
        splitInstallManager.installedModules.contains(featureName)
    }
    
    debugLog("Installation check: $installed", featureName)
    return installed
}
