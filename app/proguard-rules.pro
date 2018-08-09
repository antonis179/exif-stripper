-keepattributes SourceFile,LineNumberTable, *Annotation*, EnclosingMethod

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-optimizations code/removal/simple, code/removal/advanced, class/unboxing/enum
-optimizationpasses 3
#-dontobfuscate
#-dontwarn **
-ignorewarnings

### Speed things up!
-libraryjars <java.home>/lib/rt.jar


### Keep
-keepclassmembers class * extends java.lang.Enum {
	public static **[] values();
	public static ** valueOf(java.lang.String);
}
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
-keep class **.R$*
-keepclassmembers class **.R$* {
	public static <fields>;
}
-keep public class * {
    public protected *;
}
-keepclasseswithmembernames class * {
    native <methods>;
}

### Realm
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keepnames public class * extends io.realm.RealmObject
-keep class io.realm.annotations.RealmModule
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class * { *; }
-dontwarn javax.**
-dontwarn io.realm.**
-keep class com.vicpin.krealmextensions.**

### Butterknife
-keep @interface butterknife.*
-keepclasseswithmembers class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembers class * {
    @butterknife.* <methods>;
}
-keepclasseswithmembers class * {
    @butterknife.On* <methods>;
}
-keep class **$$ViewInjector {
    public static void inject(...);
    public static void reset(...);
}
-keep class **$$ViewBinder {
    public static void bind(...);
    public static void unbind(...);
}


### Firebase
-keepattributes Signature
-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**


### Dagger
-keepclassmembers, allowobfuscation class * {
    @dagger.** *;
}
-keep class **$$ModuleAdapter
-keep class **$$InjectAdapter
-keep class **$$StaticInjection
-keep class **$$ModuleAdapter
-keep class **$$InjectAdapter
-keep class **$$StaticInjection

-keepnames class dagger.Lazy


### Generic
-keep class com.google.errorprone.annotations.** { *; }
-dontwarn com.google.errorprone.annotations.**


### Drawable fixes


### GSON / Models
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class org.amoustakos.models.** { *; }
# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer


### Retrofit 2
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions
## Retrofit 2.1.0
-keepclasseswithmembers class * { #https://github.com/square/retrofit/issues/2129
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.adapter.rxjava.CompletableHelper$** # https://github.com/square/retrofit/issues/2034
-keepnames class rx.Single

### OkHttp3
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase


### Crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**


### Android Architecture Components
# Ref: https://issuetracker.google.com/issues/62113696
# LifecycleObserver's empty constructor is considered to be unused by proguard
#-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
#    <init>(...);
#}
-keep class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}
# ViewModel's empty constructor is considered to be unused by proguard
-keepclassmembers class * extends android.arch.lifecycle.ViewModel {
    <init>(...);
}
# keep Lifecycle State and Event enums values
-keepclassmembers class android.arch.lifecycle.Lifecycle$State { *; }
-keepclassmembers class android.arch.lifecycle.Lifecycle$Event { *; }
# keep methods annotated with @OnLifecycleEvent even if they seem to be unused
# (Mostly for LiveData.LifecycleBoundObserver.onStateChange(), but who knows)
-keepclassmembers class * {
    @android.arch.lifecycle.OnLifecycleEvent *;
}



### Glide, Glide Okttp Module, Glide Transformations
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}