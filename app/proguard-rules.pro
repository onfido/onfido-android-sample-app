# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/ricardo.freitas/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
# public *;
#}

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-keepattributes InnerClasses
-dontoptimize

-dontwarn okio.**
-dontwarn okhttp3.internal.http.HttpHeaders
-dontwarn okhttp3.internal.platform.Platform

-dontwarn com.google.android.gms.security.**

-keep class okhttp3.Interceptor$Chain
-keep class okhttp3.RequestBody
-keep class okhttp3.MultipartBody$Builder
-keep class com.google.gson.JsonElement
-keep class com.google.gson.JsonSerializationContext
-keep class com.google.gson.JsonDeserializationContext
-keep class com.google.gson.Gson
-keep class retrofit2.Call
-keep class retrofit2.Response
-keep class retrofit2.Retrofit
-keep class io.reactivex.Observable
-keep class io.reactivex.Scheduler
-keep class io.reactivex.disposables.Disposable
-keep class kotlin.jvm.internal.DefaultConstructorMarker
-keep class kotlin.jvm.functions.Function0
-keep class kotlin.jvm.functions.Function1
-keep class kotlin.Pair
-keep class com.mixpanel.android.mpmetrics.MixpanelAPI
-keep class android.support.v7.widget.SearchView { *; }
-keep class android.support.v7.app.AppCompatViewInflater { *; }

-keep class com.onfido.** { *; }

-keep class com.androidnetworking.error.ANError
-keep class com.androidnetworking.interfaces.JSONObjectRequestListener
