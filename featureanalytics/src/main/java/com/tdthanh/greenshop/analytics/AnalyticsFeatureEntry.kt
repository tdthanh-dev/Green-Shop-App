package com.tdthanh.greenshop.analytics

import androidx.compose.runtime.Composable
import com.tdthanh.greenshop.feature.DynamicFeatureEntry

/**
 * Entry point cho Analytics Feature
 */
class AnalyticsFeatureEntry : DynamicFeatureEntry {
    @Composable
    override fun Content(onNavigateBack: () -> Unit) {
        AnalyticsScreen(onNavigateBack = onNavigateBack)
    }
}
