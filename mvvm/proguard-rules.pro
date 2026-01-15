#tkrefreshlayout
-keep class com.lcodecore.tkrefreshlayout.** { *; }
-dontwarn com.lcodecore.tkrefreshlayout.**
#support
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
-dontwarn android.support.**

#databinding
-keep class android.databinding.** { *; }
-dontwarn android.databinding.**

#annotation
-keep class android.support.annotation.** { *; }
-keep interface android.support.annotation.** { *; }

#gson
-keep class com.google.gson.stream.** { *; }
-keep class com.sunloto.shandong.bean.** { *; }

#glide-transformations
-keep class jp.wasabeef.glide.transformations.** {*;}
-dontwarn jp.wasabeef.glide.transformations.**

#okhttp
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontwarn okio.**

#=====================bindingcollectionadapter=====================
-keep class me.tatarka.bindingcollectionadapter.** { *; }
-dontwarn me.tatarka.bindingcollectionadapter.**
#Serializable 不被混淆
-keepnames class * implements java.io.Serializable
#Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}

#混淆包路径
-repackageclasses ''
-flattenpackagehierarchy ''

#保护注解
-keepattributes *Annotation*

#避免混淆泛型 如果混淆报错建议关掉
-keepattributes Signature

#保留SourceFile和LineNumber属性
-keepattributes SourceFile,LineNumberTable

#忽略警告
#-ignorewarning
#----------记录生成的日志数据,gradle build时在本项目根目录输出---------
#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt
#----------------------------------------------------------------------------

#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep class android.support.** {*;}

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
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
#----------------------------------------------------------------------------
#----------------------------------------------------------------------------

-keep class com.gyf.immersionbar.* {*;}
 -dontwarn com.gyf.immersionbar.**

 #retrofit
 -dontwarn retrofit2.**
 -keep class retrofit2.** { *; }
 -keepattributes Signature
 -keepattributes Exceptions

 #glide
 -keep public class * implements com.bumptech.glide.module.AppGlideModule
 -keep public class * implements com.bumptech.glide.module.LibraryGlideModule
 -keep class com.bumptech.glide.** { *; }
 -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
     **[] $VALUES;
     public *;
 }

 #RxJava RxAndroid
 -dontwarn rx.*
 -dontwarn sun.misc.**

 -keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
 }

 #RxLifecycle
 -keep class com.trello.rxlifecycle2.** { *; }
 -keep interface com.trello.rxlifecycle2.** { *; }
 -dontwarn com.trello.rxlifecycle2.**

 #RxPermissions
 -keep class com.tbruyelle.rxpermissions2.** { *; }
 -keep interface com.tbruyelle.rxpermissions2.** { *; }

 #material-dialogs
 -keep class com.afollestad.materialdialogs.** { *; }
 -dontwarn om.afollestad.materialdialogs.**
 #native方法不被混淆
 -keepclasseswithmembernames class * {
     native <methods>;
 }
 #Parcelable 不被混淆
 -keep class * implements android.os.Parcelable {
   public static final android.os.Parcelable$Creator *;
 }
 #保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
 -keepclassmembers enum * {
   public static **[] values();
   public static ** valueOf(java.lang.String);
 }
 #不混淆资源类
 -keepclassmembers class **.R$* {
     public static <fields>;
 }
 -keep class **.R$* {
  *;
 }
 -keep @com.ved.framework.proguard.NotProguard class * {*;}
 -keep class * {
 @com.ved.framework.proguard.NotProguard <fields>;
 }
 -keepclassmembers class * {
 @com.ved.framework.proguard.NotProguard <methods>;
 }
 -keepclassmembers class * {
     void *(**On*Event);
 }
 -keep class com.ved.framework.** { *; }
 -keepclassmembers class * extends androidx.lifecycle.ViewModel {
     <init>(...);
 }