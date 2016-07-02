
//

//#include <Windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "fft.h"


#define LEN 750
#define FS 125
#define len 16384


double r[LEN];


double filterplot1[LEN]; //通道1原始数据,申请一个含有len个unsigned short类型大小内存空间, 并把这块空间的首地址赋值
double filterplot2[LEN];
double filterplot3[LEN];
//分别指定三通道指针
double *p1 = &filterplot1[0];
double *p2 = &filterplot2[0];
double *p3 = &filterplot3[0];

//double *ecg1;
double ECG[LEN];
//s double *dstplot1 = (double *)malloc(LEN * sizeof(double));
//double *dstplot1 = malloc(LEN * sizeof(double));//滤波后可用于波形显示
//double *dstplot2 = malloc(LEN * sizeof(double));
//double *dstplot3 = malloc(LEN * sizeof(double));
double *ECGp ; //= malloc(LEN * sizeof(double));//待滤波的ECG指针
double* ECGfilt1 ; // = malloc(LEN * sizeof(double));
double* ECGfilt2 ; // = malloc(LEN * sizeof(double));
double* ECGf = malloc(LEN * sizeof(double));
double* ECGband = malloc(LEN * sizeof(double));
double* yp = malloc(LEN * sizeof(double));
double* ypp = malloc(LEN * sizeof(double));
void bandfilt(double *, double *, int);//带通滤波
double* filtfilt(double*, double*, int, double*, double*, int);//零相位，需修改为线性相位
int count = 4500;
double nfilt1 = 3.0;//floor(FS / 50);
double nfilt2 = 4.0;//floor(FS / 35);
double nfilt3 = 5.0;
//doulbe idx1 = 1/nfilt1, idx2 = 1/nfilt2;
double a1[3] = {1, 0, 0}, a2[4] = {1, 0, 0, 0}, a3[5] = {1, 0, 0, 0, 0};
double b1[3] = { 1 / nfilt1, 1 / nfilt1, 1 / nfilt1}, b2[4] = {0.25, 0.25, 0.25, 0.25}, b3[5] = {0.2, 0.2, 0.2, 0.2, 0.2};
double *pa1 = a1, *pa2 = a2, *pb1 = b1, *pb2 = b2, *pa3 = a3, *pb3 = b3;
int n1 = 3, n2 = 4, n3 = 5;
int i, j, mloc, m;
double mval;
void fft(double *, int);
int find_maxv_maxloc( double const *, int );//返回数组最值位置
//阈值
double newm5, meanM5, Rm;
int timer = 0;
int detectflag = 1, firstdetect = 1;
int Rint;
int beat[LEN], idx = 1, rloc, rpos[LEN];
double mdec, rdec, val;

//  带通系数
//double bband[101],aband[101];
double bband[101] = { -0.000578988631173346,   -0.000600128896610291,  -0.00268008789841635,   0.000727366648024813,   0.00171861197247558,
                      -0.000841938627203477,  0.00330131709976230,    0.00443196407355798,    -0.000109639871021124,  0.00341789812926311,
                      0.00383278140269855,    -0.00350616784946745,   -0.000596212183660091,  0.000209063288005223,   -0.00860380660983887,
                      -0.00434429111478046,   -0.000506017301617287,  -0.00888051213067269,   -0.00186843010879688,   0.00603641265950376,
                      -0.00269093848928181,   0.00506499037119991,    0.0146674813699607, 0.00175017518924094,    0.00619816313491274,
                      0.0148264649553533, -0.00476680978373397,   -0.00460205627097691,   0.00520421929824151,    -0.0181174527965783,
                      -0.0178450415723517,    0.000171275676843005,   -0.0212535631758228,    -0.0164157831532998,    0.0147664463775232,
                      -0.00473184971251931,   0.00104189491231777,    0.0421996564073418, 0.0149531597528527, 0.00914277199263776,
                      0.0535335039381519, 0.00582288469850889,    -0.0239653172886342,    0.0286459513407936, -0.0449572066884816,
                      -0.105352608452920, -0.00585817790695608,   -0.109440651043356, -0.246614809027510, 0.155906159846135,
                      0.527707396312409,  0.155906159846135,  -0.246614809027510, -0.109440651043356, -0.00585817790695608,
                      -0.105352608452920, -0.0449572066884816,    0.0286459513407936, -0.0239653172886342,    0.00582288469850889,
                      0.0535335039381519, 0.00914277199263776,    0.0149531597528527, 0.0421996564073418, 0.00104189491231777,
                      -0.00473184971251931,   0.0147664463775232, -0.0164157831532998,    -0.0212535631758228,    0.000171275676843005,
                      -0.0178450415723517,    -0.0181174527965783,    0.00520421929824151,    -0.00460205627097691,   -0.00476680978373397,
                      0.0148264649553533, 0.00619816313491274,    0.00175017518924094,    0.0146674813699607, 0.00506499037119991,
                      -0.00269093848928181,   0.00603641265950376,    -0.00186843010879688,   -0.00888051213067269,   -0.000506017301617287,
                      -0.00434429111478046,   -0.00860380660983887,   0.000209063288005223,   -0.000596212183660091,  -0.00350616784946745,
                      0.00383278140269855,    0.00341789812926311,    -0.000109639871021124,  0.00443196407355798,    0.00330131709976230,
                      -0.000841938627203477,  0.00171861197247558,    0.000727366648024813,   -0.00268008789841635,   -0.000600128896610291,
                      -0.000578988631173346
                    };
double aband[101];
double *pb = bband, *pa = aband;
//初始化阈值参数
int mswait = (int)floor(0.55 * FS);
double ms1200 = floor(1.2 * FS);
double ms350 = floor(0.35 * FS);
double ms300 = floor(0.3 * FS);
double ms50 = floor(0.05 * FS);
int s3 = 3 * FS;
double mc = 0.45;
double M[LEN];
double R[LEN];
double MR[LEN];
double Y[LEN];
double M5[5] = {1.0, 1.0, 1.0, 1.0, 1.0};
double R5[5] = {1.0, 1.0, 1.0, 1.0, 1.0};
int k, beat;
int step = floor(R5[4] * 0.5);
double *ECG_r;
double meanRR;
int hrate;


int hrate_detect(double *pdata, int dlen)
{
    ECGfilt1 = malloc(LEN * sizeof(double));
    ECGfilt2 = malloc(LEN * sizeof(double));

    //两次低通滤波50/35
    filtfilt(*pdata, ECGfilt1, dlen, pa1, pb1, n1);
    free(ECGp);

    filtfilt(ECGfilt1, ECGfilt2, dlen, pa2, pb2, n2);

    free(ECGfilt1);

    //FFT
    fft(ECGfilt2, s3);
    for (m = len; m >= s3; m >>= 1)
        ;
    m <<= 1;
    for (i = 0; i < m; i++)
    {
        r[i] *= *(ECGfilt2 + i) / m;
    }
    ECGf = &r[0];
    mloc = find_maxv_maxloc( ECGfilt2,  m );
    for (i = 0; i < 5; i++) R5[i] = R5[i] * floor(m / mloc);
    k = (int)floor(FS * FS * PI / (2 * 2 * PI * 10 * (R5[0])));

    aband[0] = 1;
    for(i = 1; i < 101; i++)
        aband[i] = 0;
    filtfilt(ECGfilt2, ECGband, dlen, pa, pb, 101);
    free(ECGfilt2);
    //
    for(i = 0; i < dlen; i++)
        Y[i] = 0;
    for(i = k; i < (dlen - k); i++)
        Y[i] = (*(ECGband + i)) * (*(ECGband + i)) - (*(ECGband + i - k)) * (*(ECGband + i + k));
    Y[LEN - 1] = 0;
    yp = &Y[0];
    filtfilt(yp, ypp, len, pa3, pb3, n3);
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    for( count = 0; count < dlen; ++count ) {
        ypp[count] = (ypp[count] < 0) ? 0 : ypp[count];
    }
    //initial M
    //找最大值
    mval = *ypp;
    for ( count = 0; count < s3; ++count ) {
        if ( *ypp++ > mval ) mval = *(ypp - 1);
    }
    for (i = 0; i < 5; i++)
        M5[i] = mc * mval * M5[i];
    //double mean_double_array(double *dp, int dlen);
    meanM5 = mean_double_array(M5, 5);
    for(i = 0; i < s3; i++)
        M[i] = meanM5;
    //

    for(i = 0; i < len; i++)
    {
        timer++;
        if (idx >= 2)
        {
            if (detectflag)
            {
                detectflag = 0;
                meanM5 = mean_double_array(M5, 5);
                M[i] = meanM5;
                mdec = (M[i] - M[i] * mc) / (ms1200 - mswait);
                rdec = mdec / 1.4;
            }
            else if (!detectflag)
            {
                if (timer <= mswait || timer > ms1200)
                    M[i] = M[i - 1];
                else if(timer == mswait + 1)
                {
                    M[i] = M[i - 1] - mdec;
                    val = Y[i - mswait - 1];
                    for(j = i - mswait; j < i; j++)
                        if (val < Y[j])val = Y[j];
                    newm5 = mc * val;
                    if (newm5 > 1.5 * M5[5])newm5 = 1.5 * M5[4];
                    for(j = 1; j < 5; j++)
                        M5[j - 1] = M5[j];
                    M5[4] = newm5;
                }
                else if(timer > (mswait + 1) && timer <= ms1200)
                    M[i] = M[i - 1] - mdec;
            }
        }



        Rm = mean_double_array(R5, 5);
        Rint = (int)floor(2 * Rm / 3);
        if (timer <= Rint)
            R[i] = 0;
        MR[i] = M[i] + R[i];

        while (Y[i] >= MR[i])
        {
            if (firstdetect || timer > mswait)
            {
                if(firstdetect) firstdetect = 0;
                beat[idx] = i;
                if(idx > 1)
                {
                    for(j = 1; j < 5; j++)
                        R5[j - 1] = R5[j];
                    R5[4] = beat[idx - 1];
                }

                ECG_r = ECG[i - step];
                rloc = find_maxv_maxloc( ECG_r,  2 * step );
                rpos[idx] = rloc + i - step - 1; //存放rpos
                idx++;
                detectflag = 1;
                timer = -1;
            }
        }
    }
//计算输出心率hrate
    meanRR = mean_double_array(rpos, idx--);
    hrate = floor（meanRR / FS）；
}

#if 0
//处理流程示例
int main()
{
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //模拟 输入数据，6s，采样频率125Hz
    double input_data_6s1[125] =
    {
        585,
        585,
        584,
        584,
        578,
        578,
        577,
        577,
        573,
        573,
        569,
        569,
        570,
        570,
        566,
        566,
        566,
        566,
        562,
        562,
        557,
        557,
        557,
        557,
        552,
        552,
        552,
        552,
        548,
        548,
        544,
        544,
        545,
        545,
        541,
        541,
        545,
        545,
        544,
        544,
        544,
        544,
        549,
        549,
        548,
        548,
        550,
        550,
        549,
        549,
        548,
        548,
        550,
        550,
        548,
        548,
        550,
        550,
        549,
        549,
        548,
        548,
        550,
        550,
        549,
        549,
        552,
        552,
        550,
        550,
        548,
        548,
        552,
        552,
        550,
        550,
        553,
        553,
        550,
        550,
        550,
        550,
        552,
        552,
        549,
        549,
        552,
        552,
        549,
        549,
        550,
        550,
        556,
        556,
        554,
        554,
        560,
        560,
        558,
        558,
        558,
        558,
        561,
        561,
        561,
        561,
        565,
        565,
        562,
        562,
        564,
        564,
        565,
        565,
        553,
        553,
        548,
        548,
        566,
        566,
        632,
        632,
        716,
        716,
        713
    };
    for (i = 0; i < 6; i++)
    {
        for (j = 0; j < 125; j++)
        {
            filterplot1[i * 125 + j] = input_data_6s1[j];
            filterplot2[i * 125 + j] = input_data_6s1[j];
            filterplot3[i * 125 + j] = input_data_6s1[j];
        }
    }
    printf("读取数据成功！\n");

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------
    //1.滤波供波形显示
    //带通滤波：void my_filter(double const *src,double *dst,int dlen)
    //src为原始数据，dst为滤波后结果，dlen为输入数据长度（数据点个数）,返回值outlen为输出滤波后数据长度，本例设计延迟1秒
    //滤波后得到的dstplot1、dstplot2、dstplot3用于滤波波形显示
    bandfilt(p1, dstplot1, LEN);
    bandfilt(p2, dstplot2, LEN);
    bandfilt(p3, dstplot3, LEN);
    printf("滤波完成！\n");

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------
    //2.检测心率
    //单通道检测

    for(j = 0; j < LEN; j++)
    {
        ECG[j] = filterplot1[j];
    }
    ECGp = &ECG[0];

    hrate = hrate_detect(ECGp, LEN);
    printf("心率：%d\n"，hrate);
}
#endif