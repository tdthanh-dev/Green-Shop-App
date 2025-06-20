package com.tdthanh.greenshop

import android.content.Context
import android.util.Log
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manager for Play Feature Delivery - allows downloading modules on demand
 * This is the technology that enables smaller initial app downloads
 */
class FeatureDeliveryManager(private val context: Context) {

    private val splitInstallManager = SplitInstallManagerFactory.create(context)
    
    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0f)
    val downloadProgress: StateFlow<Float> = _downloadProgress.asStateFlow()

    sealed class DownloadState {
        object Idle : DownloadState()
        object Downloading : DownloadState()
        object Downloaded : DownloadState()
        data class Failed(val message: String) : DownloadState()
    }

    /**
     * Check if a dynamic feature module is installed
     */
    fun isModuleInstalled(moduleName: String): Boolean {
        return splitInstallManager.installedModules.contains(moduleName)
    }

    /**
     * Download a dynamic feature module on demand
     */
    fun downloadModule(moduleName: String) {
        if (isModuleInstalled(moduleName)) {
            _downloadState.value = DownloadState.Downloaded
            return
        }

        val request = SplitInstallRequest.newBuilder()
            .addModule(moduleName)
            .build()

        _downloadState.value = DownloadState.Downloading

        splitInstallManager.startInstall(request)
            .addOnSuccessListener { sessionId ->
                Log.d("FeatureDelivery", "Started download for module: $moduleName, sessionId: $sessionId")
            }
            .addOnFailureListener { exception ->
                Log.e("FeatureDelivery", "Failed to start download for module: $moduleName", exception)
                _downloadState.value = DownloadState.Failed(exception.message ?: "Unknown error")
            }

        // Register listener for download progress
        splitInstallManager.registerListener(installStateUpdateListener)
    }

    /**
     * Uninstall a dynamic feature module to save space
     */
    fun uninstallModule(moduleName: String) {
        if (!isModuleInstalled(moduleName)) return

        splitInstallManager.deferredUninstall(listOf(moduleName))
            .addOnSuccessListener {
                Log.d("FeatureDelivery", "Successfully uninstalled module: $moduleName")
            }
            .addOnFailureListener { exception ->
                Log.e("FeatureDelivery", "Failed to uninstall module: $moduleName", exception)
            }
    }    private val installStateUpdateListener: SplitInstallStateUpdatedListener = SplitInstallStateUpdatedListener { state ->
        when (state.status()) {
            SplitInstallSessionStatus.DOWNLOADING -> {
                val progress: Float = state.bytesDownloaded().toFloat() / state.totalBytesToDownload().toFloat()
                _downloadProgress.value = progress
                Log.d("FeatureDelivery", "Downloading: ${(progress * 100).toInt()}%")
            }
            SplitInstallSessionStatus.INSTALLING -> {
                _downloadProgress.value = 1f
                Log.d("FeatureDelivery", "Installing module...")
            }
            SplitInstallSessionStatus.INSTALLED -> {
                _downloadState.value = DownloadState.Downloaded
                _downloadProgress.value = 0f
                Log.d("FeatureDelivery", "Module installed successfully")
                splitInstallManager.unregisterListener(installStateUpdateListener)
            }
            SplitInstallSessionStatus.FAILED -> {
                _downloadState.value = DownloadState.Failed("Installation failed")
                _downloadProgress.value = 0f
                Log.e("FeatureDelivery", "Module installation failed")
                splitInstallManager.unregisterListener(installStateUpdateListener)
            }
            SplitInstallSessionStatus.CANCELED -> {
                _downloadState.value = DownloadState.Idle
                _downloadProgress.value = 0f
                Log.d("FeatureDelivery", "Module installation canceled")
                splitInstallManager.unregisterListener(installStateUpdateListener)
            }
        }
    }

    /**
     * Get size of installed modules (for analytics)
     */
    fun getInstalledModulesInfo(): List<String> {
        return splitInstallManager.installedModules.toList()
    }

    /**
     * Cancel ongoing downloads
     */
    fun cancelDownloads() {
        // This would cancel ongoing session if we tracked session IDs
        _downloadState.value = DownloadState.Idle
        _downloadProgress.value = 0f
    }

    fun cleanup() {
        splitInstallManager.unregisterListener(installStateUpdateListener)
    }
}
