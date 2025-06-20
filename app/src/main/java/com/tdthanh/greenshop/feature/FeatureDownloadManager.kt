package com.tdthanh.greenshop.feature

import android.content.Context
import android.util.Log
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.zip.ZipInputStream
import java.io.InputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Quản lý việc tải và giải nén Dynamic Features với tài nguyên
 */
class FeatureDownloadManager(private val context: Context) {
    
    private val splitInstallManager = SplitInstallManagerFactory.create(context)
    
    private val _downloadState = MutableStateFlow<FeatureDownloadState>(FeatureDownloadState.Idle)
    val downloadState: StateFlow<FeatureDownloadState> = _downloadState.asStateFlow()
    
    private val TAG = "FeatureDownloadManager"
      // Thêm biến để track manually installed features
    private val manuallyInstalledFeatures = mutableSetOf<String>()    // Sử dụng Play Core API thực tế cho release, simulation cho debug
    private val isDebugMode = context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0
    
    private val installListener = SplitInstallStateUpdatedListener { state ->
        Log.d(TAG, "Install state updated: ${state.status()}, modules: ${state.moduleNames()}")
        when (state.status()) {
            SplitInstallSessionStatus.DOWNLOADING -> {
                val progress = if (state.totalBytesToDownload() > 0) {
                    (state.bytesDownloaded() * 100 / state.totalBytesToDownload()).toInt()
                } else 0
                _downloadState.value = FeatureDownloadState.Downloading(progress)
            }
            SplitInstallSessionStatus.INSTALLING -> {
                _downloadState.value = FeatureDownloadState.Installing
            }            SplitInstallSessionStatus.INSTALLED -> {
                _downloadState.value = FeatureDownloadState.Installed(state.moduleNames())
                // Sau khi cài đặt xong, giải nén tài nguyên nếu cần
                state.moduleNames().forEach { moduleName ->
                    decompressModuleResources(moduleName)
                }
                // Reset state sau 2 giây
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    _downloadState.value = FeatureDownloadState.Idle
                }, 2000)
            }
            SplitInstallSessionStatus.FAILED -> {
                Log.e(TAG, "Install failed with error code: ${state.errorCode()}")
                _downloadState.value = FeatureDownloadState.Failed(state.errorCode())
                // Reset state sau 3 giây
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    _downloadState.value = FeatureDownloadState.Idle
                }, 3000)
            }
            SplitInstallSessionStatus.CANCELED -> {
                _downloadState.value = FeatureDownloadState.Cancelled
                // Reset state sau 2 giây
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    _downloadState.value = FeatureDownloadState.Idle
                }, 2000)
            }
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                Log.d(TAG, "Requires user confirmation")
                // Có thể xử lý xác nhận từ user ở đây
            }
            else -> {
                Log.d(TAG, "Other status: ${state.status()}")
            }
        }
    }
      init {
        splitInstallManager.registerListener(installListener)
        Log.d(TAG, "FeatureDownloadManager initialized")
        Log.d(TAG, "Debug mode: $isDebugMode")
        Log.d(TAG, "Currently installed modules at startup: ${splitInstallManager.installedModules}")
    }/**
     * Tải xuống và cài đặt dynamic feature
     */
    fun downloadFeature(featureName: String) {
        Log.d(TAG, "Attempting to download feature: $featureName")
        
        if (isFeatureInstalled(featureName)) {
            Log.d(TAG, "Feature $featureName is already installed")
            _downloadState.value = FeatureDownloadState.AlreadyInstalled
            return
        }
        
        // Trong debug mode, giả lập quá trình download
        if (isDebugMode) {
            Log.d(TAG, "Debug mode: Simulating download for $featureName")
            simulateDownload(featureName)
            return
        }
        
        // Kiểm tra xem SplitInstall có khả dụng không
        Log.d(TAG, "Currently installed modules: ${splitInstallManager.installedModules}")
        
        val request = SplitInstallRequest.newBuilder()
            .addModule(featureName)
            .build()
            
        _downloadState.value = FeatureDownloadState.Pending
        Log.d(TAG, "Starting install request for: $featureName")
        
        splitInstallManager.startInstall(request)
            .addOnSuccessListener { sessionId ->
                Log.d(TAG, "Install request successful, session ID: $sessionId")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Install request failed", exception)
                _downloadState.value = FeatureDownloadState.Failed(-1)
            }
    }
    
    /**
     * Giả lập quá trình download cho debug mode
     */
    private fun simulateDownload(featureName: String) {
        _downloadState.value = FeatureDownloadState.Pending
        
        // Giả lập delay downloading
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            _downloadState.value = FeatureDownloadState.Downloading(30)
        }, 500)
        
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            _downloadState.value = FeatureDownloadState.Downloading(60)
        }, 1000)
        
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            _downloadState.value = FeatureDownloadState.Downloading(90)
        }, 1500)
        
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            _downloadState.value = FeatureDownloadState.Installing
        }, 2000)
          android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            _downloadState.value = FeatureDownloadState.Installed(listOf(featureName))
            Log.d(TAG, "Debug mode: Feature $featureName simulated as installed")
            // Thêm feature vào danh sách đã cài đặt giả lập
            simulatedInstalledFeatures.add(featureName)
            // Reset state sau 2 giây
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                _downloadState.value = FeatureDownloadState.Idle
            }, 2000)
        }, 2500)
    }
    
    // Danh sách features đã cài đặt giả lập cho debug mode
    private val simulatedInstalledFeatures = mutableSetOf<String>()    /**
     * Kiểm tra xem feature đã được cài đặt chưa
     */
    fun isFeatureInstalled(featureName: String): Boolean {
        Log.d(TAG, "Checking if feature $featureName is installed")
        Log.d(TAG, "Debug mode: $isDebugMode")
        Log.d(TAG, "Split install manager installed modules: ${splitInstallManager.installedModules}")
        Log.d(TAG, "Simulated installed features: $simulatedInstalledFeatures")
        
        return if (isDebugMode) {
            // Trong debug mode, kiểm tra cả split install manager và danh sách giả lập
            val installedInSplitManager = splitInstallManager.installedModules.contains(featureName)
            val installedInSimulation = simulatedInstalledFeatures.contains(featureName)
            val result = installedInSplitManager || installedInSimulation
            
            Log.d(TAG, "Feature $featureName - Split manager: $installedInSplitManager, Simulation: $installedInSimulation, Result: $result")
            result
        } else {
            splitInstallManager.installedModules.contains(featureName)
        }
    }/**
     * Gỡ cài đặt dynamic feature để giải phóng dung lượng
     */
    fun uninstallFeature(featureName: String) {
        Log.d(TAG, "Attempting to uninstall feature: $featureName")
        
        if (isDebugMode) {
            // Trong debug mode, giả lập việc gỡ cài đặt
            Log.d(TAG, "Debug mode: Simulating uninstall for $featureName")
            simulatedInstalledFeatures.remove(featureName)
            _downloadState.value = FeatureDownloadState.Idle
            return
        }
        
        val modules = listOf(featureName)
        splitInstallManager.deferredUninstall(modules)
            .addOnSuccessListener {
                Log.d(TAG, "Uninstall successful for $featureName")
                _downloadState.value = FeatureDownloadState.Idle
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Uninstall failed for $featureName", exception)
            }
    }
    
    /**
     * Giải nén tài nguyên của module sau khi cài đặt
     */
    private fun decompressModuleResources(moduleName: String) {
        try {
            val moduleDir = File(context.filesDir, "modules/$moduleName")
            if (!moduleDir.exists()) {
                moduleDir.mkdirs()
            }
            
            // Giải nén từ assets của module
            val assetPath = "${moduleName}_resources.zip"
            context.assets.open(assetPath).use { inputStream ->
                decompressZip(inputStream, moduleDir)
            }
        } catch (e: Exception) {
            // Log error hoặc xử lý lỗi
        }
    }
    
    /**
     * Giải nén file ZIP
     */
    private fun decompressZip(inputStream: InputStream, outputDir: File) {
        ZipInputStream(inputStream).use { zipInput ->
            var entry = zipInput.nextEntry
            
            while (entry != null) {
                val file = File(outputDir, entry.name)
                
                if (entry.isDirectory) {
                    file.mkdirs()
                } else {
                    // Tạo thư mục parent nếu cần
                    file.parentFile?.mkdirs()
                    
                    // Ghi file
                    FileOutputStream(file).use { output ->
                        zipInput.copyTo(output)
                    }
                }
                
                zipInput.closeEntry()
                entry = zipInput.nextEntry
            }
        }
    }    /**
     * Lấy danh sách các features có thể tải
     */
    fun getAvailableFeatures(): List<FeatureInfo> {
        Log.d(TAG, "Getting available features...")
        val features = listOf(
            FeatureInfo(
                name = "featureanalytics",
                displayName = "Phân tích nâng cao",
                description = "Báo cáo chi tiết về hoạt động mua sắm",
                size = "2.1 MB",
                isInstalled = isFeatureInstalled("featureanalytics")
            ),
            FeatureInfo(
                name = "featurepremium",
                displayName = "Tính năng VIP",
                description = "Ưu đãi đặc biệt và giao hàng miễn phí",
                size = "1.8 MB",
                isInstalled = isFeatureInstalled("featurepremium")
            ),
            FeatureInfo(
                name = "featureadvancedsearch",
                displayName = "Tìm kiếm thông minh",
                description = "AI tìm kiếm sản phẩm theo hình ảnh và giọng nói",
                size = "3.2 MB",
                isInstalled = isFeatureInstalled("featureadvancedsearch")
            )
        )
        
        Log.d(TAG, "Available features created:")
        features.forEach { feature ->
            Log.d(TAG, "- ${feature.name}: installed=${feature.isInstalled}")
        }
        
        return features
    }
    
    fun cleanup() {
        splitInstallManager.unregisterListener(installListener)
    }
}

/**
 * Trạng thái tải xuống feature
 */
sealed class FeatureDownloadState {
    object Idle : FeatureDownloadState()
    object Pending : FeatureDownloadState()
    data class Downloading(val progress: Int) : FeatureDownloadState()
    object Installing : FeatureDownloadState()
    data class Installed(val moduleNames: List<String>) : FeatureDownloadState()
    data class Failed(val errorCode: Int) : FeatureDownloadState()
    object Cancelled : FeatureDownloadState()
    object AlreadyInstalled : FeatureDownloadState()
}

/**
 * Thông tin về feature
 */
data class FeatureInfo(
    val name: String,
    val displayName: String,
    val description: String,
    val size: String,
    val isInstalled: Boolean
)
