#include <string.h>
#include <jni.h>
#include <android/log.h>
extern int value;
int value;
jintArray Java_com_broadchance_wdecgrec_test_TestJNI_testIntArray(JNIEnv* env,
		jobject obj, jintArray intArray) {
	int nIdx;
	jint nSum[10] = { 0 };
	jobjectArray joArray = 0;
	//获取数组长度
	jsize jsLen = (*env)->GetArrayLength(env, intArray);
	//获取数组指针
	jint *jpIAry1 = (*env)->GetIntArrayElements(env, intArray, 0);
	//构建输出数组
	joArray = (*env)->NewIntArray(env, jsLen);
	for (nIdx = 0; nIdx < jsLen; nIdx++) {
		//操作数组
		nSum[nIdx] = jpIAry1[nIdx] + 1;
	}
	//填充输出数组
	(*env)->SetIntArrayRegion(env, joArray, 0, jsLen, nSum);
	//释放
	(*env)->ReleaseIntArrayElements(env, intArray, jpIAry1, 0);
	return joArray;
}
void Java_com_broadchance_wdecgrec_test_TestJNI_testSetValue(JNIEnv* env,
		jobject thiz, jint inputValue) {
	__android_log_print(3, "TAG", "log info");
	jclass dpclazz = (*env)->FindClass(env, "android/util/Log");
	if (dpclazz == 0) {
		return;
	}
	jmethodID method = (*env)->GetStaticMethodID(env, dpclazz, "d",
			"(Ljava/lang/String;Ljava/lang/String;)I");
	if (method == 0) {
		return;
	}
	(*env)->CallStaticVoidMethod(env, dpclazz, method,
			(*env)->NewStringUTF(env, "LogUtil"),
			(*env)->NewStringUTF(env, "static haha in c"));
	value = inputValue;
}
jint Java_com_broadchance_wdecgrec_test_TestJNI_testGetValue(JNIEnv* env,
		jobject thiz) {
	return value;
}
jstring Java_com_broadchance_wdecgrec_test_TestJNI_stringFromJNI(JNIEnv* env,
		jobject thiz) {
	return (*env)->NewStringUTF(env,
			"Hello from JNI !  Java_com_broadchance_wdecgrec_test_TestJNI_stringFromJNI");
}
jstring Java_com_broadchance_wdecgrec_test_TestJNI_stringFromJNIStatic(
		JNIEnv* env, jclass clazz) {
	return (*env)->NewStringUTF(env,
			"Hello from JNI !  Java_com_broadchance_wdecgrec_test_TestJNI_stringFromJNIStatic");
}
/**
 * @param obj调用者本身
 */
jobject Java_com_broadchance_wdecgrec_test_TestJNI_testObjParam(JNIEnv* env,
		jobject obj) {
	jclass jcStruct;
	jfieldID jsString;
	jfieldID jnInt;
	jmethodID jmId;
	jobject joCurObj;

	char achBuf[32] = { 0 };
	jstring jsStr;
	//获取java的class
	jcStruct = (*env)->FindClass(env, "com/broadchance/wdecgrec/test/TestJNI");
	// get class method, 得到类的构造函数
	jmId = (*env)->GetMethodID(env, jcStruct, "<init>", "()V");
	//获取TestJNI类中的字段str
	jsString = (*env)->GetFieldID(env, jcStruct, "str", "Ljava/lang/String;");
	//获取TestJNI类中的字段intValue
	jnInt = (*env)->GetFieldID(env, jcStruct, "intValue", "I");
	//构建一个新的TestJNI的对象
	joCurObj = (*env)->NewObject(env, jcStruct, jmId);
	//设置新对象的字段intValue的值
	(*env)->SetIntField(env, joCurObj, jnInt, 1);
	jsStr = (*env)->NewStringUTF(env, "testout");
	//设置新对象的字段str的值
	(*env)->SetObjectField(env, joCurObj, jsString, jsStr);
	jsStr = (*env)->NewStringUTF(env, "testin");
	//填充调用者中的字段str的值
	(*env)->SetObjectField(env, obj, jsString, jsStr);
	return joCurObj;
}
