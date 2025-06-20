package com.tdthanh.greenshop.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tdthanh.greenshop.feature.FeatureDownloadManager
import com.tdthanh.greenshop.feature.FeatureDownloadState
import com.tdthanh.greenshop.feature.FeatureInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturesScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToFeature: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val featureManager = remember { FeatureDownloadManager(context) }
    val downloadState by featureManager.downloadState.collectAsState()
      // Refresh danh sách features khi có thay đổi trạng thái
    val availableFeatures by remember {
        derivedStateOf {
            // Force refresh khi download state thay đổi
            downloadState
            featureManager.getAvailableFeatures()
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            featureManager.cleanup()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý tính năng") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Tính năng bổ sung",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tải về các tính năng bổ sung để nâng cao trải nghiệm mua sắm",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Hiển thị trạng thái download nếu có
            if (downloadState !is FeatureDownloadState.Idle) {
                item {
                    DownloadStatusCard(downloadState)
                }
            }
              items(availableFeatures) { feature ->
                FeatureCard(
                    feature = feature,
                    onDownload = { featureManager.downloadFeature(feature.name) },
                    onUninstall = { featureManager.uninstallFeature(feature.name) },
                    onNavigateToFeature = { onNavigateToFeature(feature.name) },
                    isDownloading = downloadState is FeatureDownloadState.Downloading ||
                                  downloadState is FeatureDownloadState.Installing
                )
            }
        }
    }
}

@Composable
private fun DownloadStatusCard(downloadState: FeatureDownloadState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            when (downloadState) {
                is FeatureDownloadState.Downloading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(
                            progress = downloadState.progress / 100f,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Đang tải xuống... ${downloadState.progress}%")
                    }
                }
                is FeatureDownloadState.Installing -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Đang cài đặt...")
                    }
                }
                is FeatureDownloadState.Installed -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Đã cài đặt: ${downloadState.moduleNames.joinToString(", ")}")
                    }
                }
                is FeatureDownloadState.Failed -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Lỗi tải xuống (${downloadState.errorCode})")
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun FeatureCard(
    feature: FeatureInfo,
    onDownload: () -> Unit,
    onUninstall: () -> Unit,
    onNavigateToFeature: () -> Unit,
    isDownloading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = feature.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = feature.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = "Kích thước: ${feature.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                if (feature.isInstalled) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Đã cài đặt",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Đã cài",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Icon(
                        Icons.Default.CloudDownload,
                        contentDescription = "Chưa cài đặt",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                if (feature.isInstalled) {                    // Nút mở feature
                    Button(
                        onClick = onNavigateToFeature,
                        enabled = !isDownloading
                    ) {
                        Icon(
                            Icons.Default.Launch,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mở")
                    }
                    
                    // Nút gỡ cài đặt
                    OutlinedButton(
                        onClick = onUninstall,
                        enabled = !isDownloading
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Gỡ")
                    }
                } else {
                    Button(
                        onClick = onDownload,
                        enabled = !isDownloading
                    ) {
                        if (isDownloading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isDownloading) "Đang tải..." else "Tải xuống")
                    }
                }
            }
        }
    }
}
