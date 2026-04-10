# Default ProGuard rules for Android project.
# Add project-specific ProGuard rules here.
-keepattributes *Annotation*
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public static <fields>;
}
