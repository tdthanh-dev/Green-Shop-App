package com.tdthanh.greenshop.search

import androidx.compose.runtime.Composable
import com.tdthanh.greenshop.feature.DynamicFeatureEntry

/**
 * Entry point cho Advanced Search Feature
 */
class AdvancedSearchFeatureEntry : DynamicFeatureEntry {
    @Composable
    override fun Content(onNavigateBack: () -> Unit) {
        AdvancedSearchScreen(onNavigateBack = onNavigateBack)
    }
}
