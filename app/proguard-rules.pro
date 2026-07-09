# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Rules for Antídoto app

# Keep Hilt generated classes
-keepclasseswithmembernames class com.antidoto.** {
    <init>(...);
}

# Keep Room entities and DAOs
-keepclasseswithmembernames class com.antidoto.data.db.** {
    *;
}

# Keep Compose internals
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }

# Keep Kotlin metadata for reflection
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
