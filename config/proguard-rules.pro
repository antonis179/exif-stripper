-keepattributes SourceFile, LineNumberTable, *Annotation*, EnclosingMethod

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#-optimizations code/removal/simple, code/removal/advanced, class/unboxing/enum
-optimizationpasses 3
-dontobfuscate
#-dontwarn **
#-dontnote **
#-ignorewarnings

### Speed things up!
#-libraryjars <java.home>/lib/rt.jar


### Keep
-keep class androidx.exifinterface.media.ExifInterface {*; }

-keep class * implements android.os.Parcelable {
	public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


### Firebase / GMS
-keepattributes Signature
-keep class com.firebase.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**

-keep class com.google.errorprone.annotations.** { *; }
-dontwarn com.google.errorprone.annotations.**
-dontwarn com.google.protobuf.**
-dontnote com.google.protobuf.**
-dontwarn org.robolectric.**
-dontwarn com.google.android.gms.**

# Event bus
-keep class org.greenrobot.eventbus.**

### GSON / Models
-keepattributes *Annotation*
-keep class com.google.gson.stream.** { *; }
# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

### Crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

### Kotlin
-dontwarn kotlin.reflect.jvm.**
-keep class kotlin.jvm.internal.**