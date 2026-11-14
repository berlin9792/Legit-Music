# Keep classes and constructors looked up from jni_bridge.cpp.
-keep class legit.music.ayush.usbaudio.model.DacFormat { <init>(...); }
-keep class legit.music.ayush.usbaudio.model.DacCapabilities { <init>(...); }

# Keep names used by the exported JNI method symbols.
-keepclasseswithmembernames class legit.music.ayush.usbaudio.UsbAudioDriver { native <methods>; }
