# Ktor / Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

# For Enum.entries in Kotlin
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
