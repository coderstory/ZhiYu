# Keep Koin
-keepclassmembers class * { @org.koin.core.annotation.Single *; }

# Keep Room
-keep class * extends androidx.room.RoomDatabase { *; }

# Keep Compose
-keep class androidx.compose.** { *; }
