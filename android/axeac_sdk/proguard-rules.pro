#-------------------------------------------定制化区域----------------------------------------------
#---------------------------------1.实体类---------------------------------

#-dontwarn cn.kuaihusoft.workcenter.sdk.utils.**
#-dontwarn cn.kuaihusoft.workcenter.sdk.activity.**
#-dontwarn cn.kuaihusoft.workcenter.sdk.retrofit.**
#-dontwarn cn.kuaihusoft.workcenter.sdk.scanner.**
#-dontwarn cn.kuaihusoft.workcenter.sdk.scanner.decode.**
#-dontwarn cn.kuaihusoft.workcenter.sdk.retrofit.**
#-dontwarn cn.kuaihusoft.workcenter.sdk.dialog.**
#-dontwarn cn.kuaihusoft.workcenter.sdk.fragment.**
#-dontwarn cn.kuaihusoft.workcenter.sdk.customview.**
#-dontwarn cn.kuaihusoft.workcenter.sdk.adapters.**
#-dontwarn cn.kuaihusoft.workcenter.sdk.retrofit.**
#
-keep class cn.kuaihusoft.workcenter.sdk.tools.** {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.jhsp.** {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.analysis.** {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.scanner.** {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.scanner.decode.** {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.activity.** {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.fragment.** {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.adapters.** {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.dialog.** {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.retrofit.** {*;}
-keep class cn.kuaihusoft.workcenter.sdk.ui.** {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.ui.base.** {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.ui.button.** {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.ui.container.** {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.ui.datetime.** {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.ui.dynamiccharts.** {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.ui.refreshview.** {*;}
-keep class cn.kuaihusoft.workcenter.sdk.customview.** {*;}
-keep class cn.kuaihusoft.workcenter.sdk.KhinfSDK {*;}
-keep class cn.kuaihusoft.workcenter.sdk.retrofit.UIHelper {*;}
-keep class cn.kuaihusoft.workcenter.sdk.utils.** {*;}
#-keep interface cn.kuaihusoft.workcenter.sdk.retrofit.OnRequestCallBack { *; }
#-keep class cn.kuaihusoft.workcenter.sdk.activity.**
#-keep class cn.kuaihusoft.workcenter.sdk.activity.BaseActivity {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.activity.ComponentActivity {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.activity.MainActivity1 {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.activity.ImageShowActivity {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.activity.FileSelectorActivity {*;}
#-keep class cn.kuaihusoft.workcenter.sdk.activity.CaptureActivity {*;}

#-keep class * extends cn.kuaihusoft.workcenter.sdk.activity.BaseActivity {*;}


#-------------------------------------------------------------------------

#---------------------------------2.第三方包-------------------------------
#-libraryjars /libs/core-3.1.1-SNAPSHOT.jar
-dontwarn com.google.zxing.**
-keep class com.google.zxing.** {*;}

#-libraryjars /libs/gaodemap 1.4.2.jar
-dontwarn com.amap.api.**
-keep class com.amap.api.**{*;}
-dontwarn com.autonavi.**
-keep class com.autonavi.**{*;}

#-libraryjars /libs/lite-orm-1.9.2.jar
-dontwarn com.litesuits.orm.**
-keep class  com.litesuits.orm.** {*;}


-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

#glide
-dontwarn com.bumptech.glide.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-dontwarn retrofit2.**
-keep class retrofit2.** {*;}
-keepattributes Signature
-keepattributes Exceptions

	# For RxJava:
-dontwarn org.mockito.**
-dontwarn org.junit.**
-dontwarn org.robolectric.**

-dontwarn rx.**
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keep class rx.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
    @retrofit2.http.* <methods>;
}


-keep class sun.misc.Unsafe { *; }

-dontwarn java.lang.invoke.*

#-dontwarn rx.**
#-keep class rx.** {*;}

-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-dontwarn com.squareup.okhttp3.**
-dontwarn okhttp3.**
-keep class com.squareup.okhttp3.** {*;}
-keep class okhttp3.** {*;}
-dontwarn okio.**
-keep class okio.** {*;}

-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-dontwarn okio.**
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**


-dontwarn cn.finalteam.galleryfinal.**
-keep class cn.finalteam.galleryfinal.widget.*{*;}
-keep class cn.finalteam.galleryfinal.widget.crop.*{*;}
-keep class cn.finalteam.galleryfinal.widget.zoonview.*{*;}

-dontwarn com.github.ikidou.fragmentBackHandler.**
-keep class com.github.ikidou.fragmentBackHandler.*{*;}

#-------------------------------------------------------------------------

#---------------------------------3.与js互相调用的类------------------------



#-------------------------------------------------------------------------

#---------------------------------4.反射相关的类和方法-----------------------



#----------------------------------------------------------------------------
#---------------------------------------------------------------------------------------------------

#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes *JavascriptInterface*
-keepattributes SourceFile,LineNumberTable
#----------------------------------------------------------------------------

#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.support.v4.**
-keep interface android.support.** { *; }
-keep public class * extends android.support.annotation.**
-keep public class * extends android.support.v7.**
-keep public class * implements java.io.Serializable {*;}
-keep class android.support.** {*;}
-keep class * extends android.view.animation.Animation{ *; }

#如果引用了v4或者v7包
-dontwarn android.support.**
#忽略警告
-ignorewarning
####################

#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class * extends android.app.Dialog

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}
-keep class **.R$* {
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}

#----------------------------------------------------------------------------

#---------------------------------webview------------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepclassmembers class * extends android.webkit.WebChromeClient{
        public void openFileChooser(...);
}

# Remove logging calls
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}
#----------------------------------------------------------------------------
#---------------------------------------------------------------------------------------------------
# 如果你需要兼容6.0系统，请不要混淆org.apache.http.legacy.jar

-dontwarn android.net.compatibility.**
-dontwarn android.net.http.**
-dontwarn com.android.internal.http.multipart.**
-dontwarn org.apache.commons.**
-dontwarn org.apache.http.**
-keep class android.net.compatibility.{*;}
-keep class android.net.http.{*;}
-keep class com.android.internal.http.multipart.{*;}
-keep class org.apache.commons.{*;}
-keep class org.apache.http.**{*;}






