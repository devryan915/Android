#include <jni.h>
extern "C" jstring Java_com_example_testjni_MainActivity_helloFromJni(JNIEnv *env, jobject thiz) {
return env->NewStringUTF("Hello From Jni");
}
