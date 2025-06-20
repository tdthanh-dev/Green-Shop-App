package com.tdthanh.greenshop.feature

import androidx.compose.runtime.Composable

/**
 * Interface để định nghĩa entry point cho dynamic features
 */
interface DynamicFeatureEntry {
    @Composable
    fun Content(onNavigateBack: () -> Unit)
}

/**
 * Factory để tạo instance của dynamic features
 */
object DynamicFeatureEntryFactory {
    
    fun createAnalyticsEntry(): DynamicFeatureEntry? {
        return try {
            val clazz = Class.forName("com.tdthanh.greenshop.analytics.AnalyticsFeatureEntry")
            clazz.newInstance() as DynamicFeatureEntry
        } catch (e: Exception) {
            null
        }
    }
    
    fun createPremiumEntry(): DynamicFeatureEntry? {
        return try {
            val clazz = Class.forName("com.tdthanh.greenshop.premium.PremiumFeatureEntry")
            clazz.newInstance() as DynamicFeatureEntry
        } catch (e: Exception) {
            null
        }
    }
    
    fun createAdvancedSearchEntry(): DynamicFeatureEntry? {
        return try {
            val clazz = Class.forName("com.tdthanh.greenshop.search.AdvancedSearchFeatureEntry")
            clazz.newInstance() as DynamicFeatureEntry
        } catch (e: Exception) {
            null
        }
    }
    
    fun getEntryForFeature(featureName: String): DynamicFeatureEntry? {
        return when (featureName) {
            "featureanalytics" -> createAnalyticsEntry()
            "featurepremium" -> createPremiumEntry()
            "featureadvancedsearch" -> createAdvancedSearchEntry()
            else -> null
        }
    }
}
