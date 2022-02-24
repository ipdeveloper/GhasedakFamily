# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

#
#-keep class com.yalantis.ucrop.view.** { *; }
#-keep class com.google.android.gms.** { *; }
#-keep class okio.** { *; }
#-dontwarn okio.**
#-dontwarn org.apache.http.**
#-dontwarn android.net.**
#-dontwarn android.content.res.**
#-keep class org.apache.** {*;}
#-keep class org.apache.http.** { *; }
#-keep class com.j256.ormlite.** { *; }
#-keep class org.mapsforge.map.android.** { *; }
#-keep class android.content.res.** { *; }
#-keep class ar.com.hjg.pngj.pixels.** { *; }
#-keep class android.view.** { *; }
#-keep class android.util.** { *; }
#-keep class android.graphics.** { *; }
#-keep class android.content.** { *; }
#-keep class com.caverock.androidsvg.** { *; }
#-keep class org.xmlpull.** { *; }
#-dontwarn com.google.android.gms.**
#-dontwarn android.view.**
#-dontwarn com.j256.ormlite.**
#-dontwarn android.graphics.**
#-dontwarn android.content.**
#-dontwarn com.caverock.androidsvg.**
#-dontwarn ar.com.hjg.pngj.pixels.**
#-dontwarn com.yalantis.ucrop.view.**
#-dontwarn android.util.**
#-dontwarn org.mapsforge.map.android.**
#-dontwarn org.xmlpull.**
#-dontnote android.net.http.*
#-dontnote android.util.*
#-dontnote android.graphics.*
#-dontnote android.view.*
#-dontnote org.xmlpull.*
#-dontnote android.content.*
#-dontnote android.content.res.*
#-dontnote org.mapsforge.map.android.*
#-dontnote com.caverock.androidsvg.*
#-dontnote com.j256.ormlite.*
#-dontnote ar.com.hjg.pngj.pixels.*
#-dontnote org.apache.commons.codec.**
#-dontnote org.apache.http.**
-keep class com.android.vending.billing
#
#-keep class com.google.android.gms.internal.** { *; }
#-dontwarn com.google.android.gms.internal.zzhu
#-keep public class pl.droidsonroids.gif.GifIOException{<init>(int);}
#-keep class pl.droidsonroids.gif.GifInfoHandle{<init>(long,int,int,int);}
#
#-dontwarn sun.misc.**

-optimizationpasses 5

   -dontusemixedcaseclassnames

 -dontskipnonpubliclibraryclasses

   -dontpreverify

  -verbose

  -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

   #keep class com.android.vending.billing

-keep public class * extends android.app.Activity

  -keep public class * extends android.app.Application

 -keep public class * extends android.app.Service

   -keep public class * extends android.content.BroadcastReceiver

   -keep public class * extends android.content.ContentProvider

  -keep public class * extends android.app.backup.BackupAgentHelper

-keep public class * extends android.preference.Preference

-keep public class com.android.vending.licensing.ILicensingService

-keep public class * extends android.support.v7.app.AppCompatActivity


 #keep all classes that might be used in XML layouts

   -keep public class * extends android.view.View

   -keep public class * extends android.app.Fragment

  -keep public class * extends android.support.v4.Fragment

-keep class okio.** { *; }
-dontwarn okio.**

  -keep class java.** { *; }
  -keep class android.** { *; }
  -keep class org.** { *; }

  -keep class com.google.** { *; }
  -keep class android.support.v4.** { *; }

#-keep class android.support.v4.internal.** { *; }
#-keep interface android.support.v4.internal.** { *; }
#-keep class android.support.v4.** { *; }
#-keep interface android.support.v4.** { *; }

-keep class android.support.v7.widget.RecyclerView.** { *; }
-keep interface android.support.v7.widget.RecyclerView.** { *; }

-keepattributes Signature
-keep class sun.misc.Unsafe { *; }

  #keep all public and protected methods that could be used by java reflection

  -keepclassmembernames class * {

       public protected <methods>;

 }



   -keepclasseswithmembernames class * {

      native <methods>;

  }



   -keepclasseswithmembernames class * {

    public <init>(android.content.Context, android.util.AttributeSet);

   }



   -keepclasseswithmembernames class * {

      public <init>(android.content.Context, android.util.AttributeSet, int);

 }





  -keepclassmembers enum * {

      public static **[] values();

      public static ** valueOf(java.lang.String);

  }



  -keep class * implements android.os.Parcelable {

    public static final android.os.Parcelable$Creator *;

  }


 -dontwarn **CompatHoneycomb

  -dontwarn org.htmlcleaner.*

-dontwarn com.squareup.okhttp.**

 -dontwarn android.support.v4.**


#--------------Countly-------------------
-keep class org.openudid.** { *; }

#--------------ACRA -------------------
# Keep all the ACRA classes
-keep class org.acra.** { *; }


#ACRA specifics
# Restore some Source file names and restore approximate line numbers in the stack traces,
# otherwise the stack traces are pretty useless
-keepattributes SourceFile,LineNumberTable

# ACRA needs "annotations" so add this...
# Note: This may already be defined in the default "proguard-android-optimize.txt"
# file in the SDK. If it is, then you don't need to duplicate it. See your
# "project.properties" file to get the path to the default "proguard-android-optimize.txt".
-keepattributes *Annotation*

# keep this class so that logging will show 'ACRA' and not a obfuscated name like 'a'.
# Note: if you are removing log messages elsewhere in this file then this isn't necessary
-keep class org.acra.ACRA {
    *;
}

# keep this around for some enums that ACRA needs
-keep class org.acra.ReportingInteractionMode {
    *;
}

-keepnames class org.acra.sender.HttpSender$** {
    *;
}

-keepnames class org.acra.ReportField {
    *;
}

-keep class com.android.volley.** { *; }

-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# keep this otherwise it is removed by ProGuard
-keep public class org.acra.ErrorReporter{
    public void addCustomData(java.lang.String,java.lang.String);
    public void putCustomData(java.lang.String,java.lang.String);
    public void removeCustomData(java.lang.String);
}

# keep this otherwise it is removed by ProGuard
-keep public class org.acra.ErrorReporter{
    public void handleSilentException(java.lang.Throwable);
}

-keep public class * implements com.bumptech.glide.module.GlideModule

-keep class com.crashlytics.** { *; }
-keep class com.crashlytics.android.**
-keepattributes SourceFile, LineNumberTable, *Annotation*

-keep public class * extends java.lang.Exception

-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

-dontwarn org.hamcrest.**
-dontwarn android.test.**
-dontwarn android.support.test.**

-keep class org.hamcrest.** {
   *;
}

-keep class org.junit.** { *; }
-dontwarn org.junit.**

-keep class junit.** { *; }
-dontwarn junit.**

-keep class sun.misc.** { *; }
-dontwarn sun.misc.**


#---------------------------

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

# Preserve some attributes that may be required for reflection.
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.google.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService
-dontnote com.google.vending.licensing.ILicensingService
-dontnote com.google.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep setters in Views so that animations can still work.
-keepclassmembers public class * extends android.view.View {
    void set*(***);
    *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick.
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# Preserve annotated Javascript interface methods.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# The support libraries contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version. We know about them, and they are safe.
-dontnote android.support.**
-dontwarn android.support.**

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

#Retrofit
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

#Dagger 2
-dontwarn com.google.errorprone.annotations.*

-ignorewarnings

