package com.tdthanh.greenshop.premium

import androidx.compose.runtime.Composable
import com.tdthanh.greenshop.feature.DynamicFeatureEntry

/**
 * Entry point cho Premium Feature
 */
class PremiumFeatureEntry : DynamicFeatureEntry {
    @Composable
    override fun Content(onNavigateBack: () -> Unit) {
        PremiumScreen(onNavigateBack = onNavigateBack)
    }
}
