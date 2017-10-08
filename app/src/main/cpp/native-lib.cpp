#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_unipg_tlc_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "DONE";
    return env->NewStringUTF(hello.c_str());
}
