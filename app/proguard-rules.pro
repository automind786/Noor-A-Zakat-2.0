# WebView JavaScript bridge: ye methods R8 se rename/remove NA hon
-keepattributes JavascriptInterface
-keepattributes *Annotation*
-keep class com.hanafi.noorezakat.MainActivity$SmartBridge { *; }
-keepclassmembers class com.hanafi.noorezakat.MainActivity$SmartBridge {
    @android.webkit.JavascriptInterface <methods>;
}
