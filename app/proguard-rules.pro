# Keep Koin
-keepclassmembers class * { @org.koin.core.annotation.Single *; }
-keepclassmembers class * { @org.koin.core.annotation.Module *; }

# Keep Room
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class *
-keepclassmembers @androidx.room.Entity class * { *; }

# Keep Compose
-keep class androidx.compose.** { *; }

# Keep Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.zhiyu.app.**$$serializer { *; }
-keepclassmembers class com.zhiyu.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.zhiyu.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep MIUIX
-keep class top.yukonga.miuix.** { *; }

# Keep Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
