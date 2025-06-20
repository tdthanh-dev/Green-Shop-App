# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Advanced R8 optimization rules for smaller APK/AAB
-allowaccessmodification
-mergeinterfacesaggressively
-overloadaggressively
-repackageclasses ''

# Keep only what's needed for Compose
-keep class androidx.compose.** { *; }
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# Remove unused code aggressively
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Optimize Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep Play Core library for dynamic features
-keep class com.google.android.play.core.** { *; }

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Additional optimization for smaller bundle size
-printusage build/outputs/mapping/release/usage.txt
-printseeds build/outputs/mapping/release/seeds.txt
-printmapping build/outputs/mapping/release/mapping.txt