/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
#include <android/log.h>

//#define DEBUG

#ifdef DEBUG
#define ECG_DBG(format, args...) __android_log_print(3, "ECG_LF:DBG", format, ##args)
#define ECG_LOG(format, args...) __android_log_print(3, "ECG_LF:LOG", format, ##args)
#else
#define ECG_DBG(format, args...)
#define ECG_LOG(format, args...)
#endif


#include "hrate_lib/ecg_filter.c"
#include "hrate_lib/bandfilt.c"
#include "hrate_lib/hrate_detect.c"
#include "hrate_lib/pinghua.c"
#include "hrate_lib/bandfiltTEO.c"
#include "hrate_lib/mean_double_array.c"
#include "hrate_lib/find_maxv_maxloc.c"




ECG_HND * hnd_l2 = NULL;
ECG_HND * hnd_v1 = NULL;
ECG_HND * hnd_v5 = NULL;



void Java_com_broadchance_utils_FilterUtil_resetFilter(JNIEnv* env, jobject obj)
{
    ecg_close(hnd_l2); hnd_l2 = ecg_init(0);
    ecg_close(hnd_v1); hnd_v1 = ecg_init(1);
    ecg_close(hnd_v5); hnd_v5 = ecg_init(2);
}




jintArray Java_com_broadchance_utils_FilterUtil_getECGDataII(JNIEnv* env,
		jobject obj, jintArray intArray)
{
	int nIdx;
	unsigned char * buf_in;
    unsigned char * buf_out;
    int rc, outlen  = 0;
    ECG_HND * hnd   = hnd_l2;

    if(hnd == NULL)
    {
        hnd = hnd_l2 = ecg_init(0);
    }

    jint * nSum ;

	jobjectArray joArray = 0;
	//��ȡ���鳤��
	jsize jsLen = (*env)->GetArrayLength(env, intArray);
	//��ȡ����ָ��
	jint *jpIAry1 = (*env)->GetIntArrayElements(env, intArray, 0);

    buf_in = malloc(jsLen *2 *2);
    if(buf_in)
    {
        short v;
        buf_out = buf_in + jsLen * 2;

        for (nIdx = 0; nIdx < jsLen; nIdx++) {
		    //��������

            v = jpIAry1[nIdx];

		    buf_in[nIdx*2]     =  (v >> 8) & 0xff;
		    buf_in[nIdx*2 + 1] =  (v)      & 0xff;

            ECG_DBG("Input %d  0x%08x == > 0x:%02x %02x\n", nIdx, jpIAry1[nIdx], buf_in[nIdx*2], buf_in[nIdx*2+1]);
	    }

        rc = ecg_input(hnd, buf_in, jsLen *2, buf_out, &outlen);

    }

    jsLen = outlen/ECG_WORD_SIZE;

    nSum = malloc(sizeof(jint) * (jsLen + 1));
    for(nIdx = 0; nIdx < jsLen; nIdx++)
    {
        short v = (buf_out[nIdx * ECG_WORD_SIZE] << 8) | buf_out[nIdx * ECG_WORD_SIZE + 1];

        nSum[nIdx] = v;

        ECG_DBG("Putput %d  0x%08x == > 0x:%02x %02x\n", nIdx,
            buf_out[nIdx * ECG_WORD_SIZE], buf_out[nIdx * ECG_WORD_SIZE+1], nSum[nIdx]);
    }

	//�����������
	joArray = (*env)->NewIntArray(env, jsLen);

	//����������
	(*env)->SetIntArrayRegion(env, joArray, 0, jsLen, nSum);
	//�ͷ�
	(*env)->ReleaseIntArrayElements(env, intArray, jpIAry1, 0);

    free(buf_in);
    free(nSum);

	return joArray;
}
jintArray Java_com_broadchance_utils_FilterUtil_getECGDataV1(JNIEnv* env,
		jobject obj, jintArray intArray)
{
    int nIdx;
    unsigned char * buf_in;
    unsigned char * buf_out;
    int rc, outlen  = 0;
    ECG_HND * hnd   = hnd_v1;

    if(hnd == NULL)
    {
        hnd = hnd_v1 = ecg_init(1);
    }

    jint * nSum ;

    jobjectArray joArray = 0;
    //��ȡ���鳤��
    jsize jsLen = (*env)->GetArrayLength(env, intArray);
    //��ȡ����ָ��
    jint *jpIAry1 = (*env)->GetIntArrayElements(env, intArray, 0);

    buf_in = malloc(jsLen *2 *2);
    if(buf_in)
    {
        buf_out = buf_in + jsLen * 2;

        for (nIdx = 0; nIdx < jsLen; nIdx++) {
            //��������
            buf_in[nIdx*ECG_WORD_SIZE]     =  (jpIAry1[nIdx] >> 8) & 0xff;
            buf_in[nIdx*ECG_WORD_SIZE + 1] =  (jpIAry1[nIdx])      & 0xff;
        }

        rc = ecg_input(hnd, buf_in, jsLen *ECG_WORD_SIZE, buf_out, &outlen);

    }

    jsLen = outlen/ECG_WORD_SIZE;

    nSum = malloc(sizeof(jint) * (jsLen + 1));
    for(nIdx = 0; nIdx < jsLen; nIdx++)
    {
        nSum[nIdx] = (buf_out[nIdx * ECG_WORD_SIZE] << 8) | buf_out[nIdx * ECG_WORD_SIZE + 1];
    }

    //�����������
    joArray = (*env)->NewIntArray(env, jsLen);

    //����������
    (*env)->SetIntArrayRegion(env, joArray, 0, jsLen, nSum);
    //�ͷ�
    (*env)->ReleaseIntArrayElements(env, intArray, jpIAry1, 0);

    free(buf_in);
    free(nSum);

    return joArray;
}

jintArray Java_com_broadchance_utils_FilterUtil_getECGDataV5(JNIEnv* env,
		jobject obj, jintArray intArray)
{
    int nIdx;
    unsigned char * buf_in;
    unsigned char * buf_out;
    int rc, outlen  = 0;
    ECG_HND * hnd   = hnd_v5;

    if(hnd == NULL)
    {
        hnd = hnd_v5 = ecg_init(2);
    }

    jint * nSum ;

    jobjectArray joArray = 0;
    //��ȡ���鳤��
    jsize jsLen = (*env)->GetArrayLength(env, intArray);
    //��ȡ����ָ��
    jint *jpIAry1 = (*env)->GetIntArrayElements(env, intArray, 0);

    buf_in = malloc(jsLen *2 *2);
    if(buf_in)
    {
        buf_out = buf_in + jsLen * 2;

        for (nIdx = 0; nIdx < jsLen; nIdx++) {
            //��������
            buf_in[nIdx*2]     =  (jpIAry1[nIdx] >> 8) & 0xff;
            buf_in[nIdx*2 + 1] =  (jpIAry1[nIdx])      & 0xff;
        }

        rc = ecg_input(hnd, buf_in, jsLen *2, buf_out, &outlen);

    }

    jsLen = outlen/ECG_WORD_SIZE;

    nSum = malloc(sizeof(jint) * (jsLen + 1));
    for(nIdx = 0; nIdx < jsLen; nIdx++)
    {
        nSum[nIdx] = (buf_out[nIdx * ECG_WORD_SIZE] << 8) | buf_out[nIdx * ECG_WORD_SIZE + 1];
    }

    //�����������
    joArray = (*env)->NewIntArray(env, jsLen);

    //����������
    (*env)->SetIntArrayRegion(env, joArray, 0, jsLen, nSum);
    //�ͷ�
    (*env)->ReleaseIntArrayElements(env, intArray, jpIAry1, 0);

    free(buf_in);
    free(nSum);

    return joArray;
}

jint Java_com_broadchance_utils_FilterUtil_getHeartRate(JNIEnv* env,
		jobject obj)
{
    ECG_HND * hnd   = hnd_l2;

    if(hnd == NULL)
    {
        hnd = hnd_l2 = ecg_init(0);
    }

    if(hnd->hr_pass)

        return (jint) -99;

    return (jint)(hnd->hrate);

}


ECG_HND * hnd_l2_r = NULL;
ECG_HND * hnd_v1_r = NULL;
ECG_HND * hnd_v5_r = NULL;



void Java_com_broadchance_utils_FilterUtil_resetFilterR(JNIEnv* env, jobject obj)
{
    if(hnd_l2_r)
        ecg_close(hnd_l2_r);
    hnd_l2_r = ecg_init(0);

    if(hnd_v1_r)
        ecg_close(hnd_v1_r);
    hnd_v1_r = ecg_init(1);

    if(hnd_v5_r)
        ecg_close(hnd_v5_r);
    hnd_v5_r = ecg_init(2);
}




jintArray Java_com_broadchance_utils_FilterUtil_getECGDataIIR(JNIEnv* env,
		jobject obj, jintArray intArray)
{
	int nIdx;
	unsigned char * buf_in;
    unsigned char * buf_out;
    int rc, outlen  = 0;
    ECG_HND * hnd   = hnd_l2_r;

    if(hnd == NULL)
    {
        hnd = hnd_l2_r = ecg_init(0);
    }

    jint * nSum ;

	jobjectArray joArray = 0;
	//��ȡ���鳤��
	jsize jsLen = (*env)->GetArrayLength(env, intArray);
	//��ȡ����ָ��
	jint *jpIAry1 = (*env)->GetIntArrayElements(env, intArray, 0);

    buf_in = malloc(jsLen *2 *2);
    if(buf_in)
    {
        short v;
        buf_out = buf_in + jsLen * 2;

        for (nIdx = 0; nIdx < jsLen; nIdx++) {
		    //��������

            v = jpIAry1[nIdx];

		    buf_in[nIdx*2]     =  (v >> 8) & 0xff;
		    buf_in[nIdx*2 + 1] =  (v)      & 0xff;

            ECG_DBG("Input %d  0x%08x == > 0x:%02x %02x\n", nIdx, jpIAry1[nIdx], buf_in[nIdx*2], buf_in[nIdx*2+1]);
	    }

        rc = ecg_input(hnd, buf_in, jsLen *2, buf_out, &outlen);

    }

    jsLen = outlen/ECG_WORD_SIZE;

    nSum = malloc(sizeof(jint) * (jsLen + 1));
    for(nIdx = 0; nIdx < jsLen; nIdx++)
    {
        short v = (buf_out[nIdx * ECG_WORD_SIZE] << 8) | buf_out[nIdx * ECG_WORD_SIZE + 1];

        nSum[nIdx] = v;

        ECG_DBG("Putput %d  0x%08x == > 0x:%02x %02x\n", nIdx,
            buf_out[nIdx * ECG_WORD_SIZE], buf_out[nIdx * ECG_WORD_SIZE+1], nSum[nIdx]);
    }

	//�����������
	joArray = (*env)->NewIntArray(env, jsLen);

	//����������
	(*env)->SetIntArrayRegion(env, joArray, 0, jsLen, nSum);
	//�ͷ�
	(*env)->ReleaseIntArrayElements(env, intArray, jpIAry1, 0);

    free(buf_in);
    free(nSum);

	return joArray;
}
jintArray Java_com_broadchance_utils_FilterUtil_getECGDataV1R(JNIEnv* env,
		jobject obj, jintArray intArray)
{
    int nIdx;
    unsigned char * buf_in;
    unsigned char * buf_out;
    int rc, outlen  = 0;
    ECG_HND * hnd   = hnd_v1_r;

    if(hnd == NULL)
    {
        hnd = hnd_v1_r = ecg_init(1);
    }

    jint * nSum ;

    jobjectArray joArray = 0;
    //��ȡ���鳤��
    jsize jsLen = (*env)->GetArrayLength(env, intArray);
    //��ȡ����ָ��
    jint *jpIAry1 = (*env)->GetIntArrayElements(env, intArray, 0);

    buf_in = malloc(jsLen *2 *2);
    if(buf_in)
    {
        buf_out = buf_in + jsLen * 2;

        for (nIdx = 0; nIdx < jsLen; nIdx++) {
            //��������
            buf_in[nIdx*ECG_WORD_SIZE]     =  (jpIAry1[nIdx] >> 8) & 0xff;
            buf_in[nIdx*ECG_WORD_SIZE + 1] =  (jpIAry1[nIdx])      & 0xff;
        }

        rc = ecg_input(hnd, buf_in, jsLen *ECG_WORD_SIZE, buf_out, &outlen);

    }

    jsLen = outlen/ECG_WORD_SIZE;

    nSum = malloc(sizeof(jint) * (jsLen + 1));
    for(nIdx = 0; nIdx < jsLen; nIdx++)
    {
        nSum[nIdx] = (buf_out[nIdx * ECG_WORD_SIZE] << 8) | buf_out[nIdx * ECG_WORD_SIZE + 1];
    }

    //�����������
    joArray = (*env)->NewIntArray(env, jsLen);

    //����������
    (*env)->SetIntArrayRegion(env, joArray, 0, jsLen, nSum);
    //�ͷ�
    (*env)->ReleaseIntArrayElements(env, intArray, jpIAry1, 0);

    free(buf_in);
    free(nSum);

    return joArray;
}

jintArray Java_com_broadchance_utils_FilterUtil_getECGDataV5R(JNIEnv* env,
		jobject obj, jintArray intArray)
{
    int nIdx;
    unsigned char * buf_in;
    unsigned char * buf_out;
    int rc, outlen  = 0;
    ECG_HND * hnd   = hnd_v5_r;

    if(hnd == NULL)
    {
        hnd = hnd_v5_r = ecg_init(2);
    }

    jint * nSum ;

    jobjectArray joArray = 0;
    //��ȡ���鳤��
    jsize jsLen = (*env)->GetArrayLength(env, intArray);
    //��ȡ����ָ��
    jint *jpIAry1 = (*env)->GetIntArrayElements(env, intArray, 0);

    buf_in = malloc(jsLen *2 *2);
    if(buf_in)
    {
        buf_out = buf_in + jsLen * 2;

        for (nIdx = 0; nIdx < jsLen; nIdx++) {
            //��������
            buf_in[nIdx*2]     =  (jpIAry1[nIdx] >> 8) & 0xff;
            buf_in[nIdx*2 + 1] =  (jpIAry1[nIdx])      & 0xff;
        }

        rc = ecg_input(hnd, buf_in, jsLen *2, buf_out, &outlen);

    }

    jsLen = outlen/ECG_WORD_SIZE;

    nSum = malloc(sizeof(jint) * (jsLen + 1));
    for(nIdx = 0; nIdx < jsLen; nIdx++)
    {
        nSum[nIdx] = (buf_out[nIdx * ECG_WORD_SIZE] << 8) | buf_out[nIdx * ECG_WORD_SIZE + 1];
    }

    //�����������
    joArray = (*env)->NewIntArray(env, jsLen);

    //����������
    (*env)->SetIntArrayRegion(env, joArray, 0, jsLen, nSum);
    //�ͷ�
    (*env)->ReleaseIntArrayElements(env, intArray, jpIAry1, 0);

    free(buf_in);
    free(nSum);

    return joArray;
}

jint Java_com_broadchance_utils_FilterUtil_getHeartRateR(JNIEnv* env,
		jobject obj)
{
    ECG_HND * hnd   = hnd_l2_r;

    if(hnd == NULL)
    {
        hnd = hnd_l2_r = ecg_init(0);
    }

    if(hnd->hr_pass)

        return (jint) -99;

    return (jint)(hnd->hrate);

}

