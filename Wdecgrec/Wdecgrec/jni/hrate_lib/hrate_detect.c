
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include "ecg_config.h"



#define LEN 1250
#define FS 125
int find_maxv_maxloc(double *, int);//返回数组最值位置
double mean_double_array(double *, int);//返回数组均值
void pinghua(double *, double *, int, int);
extern double bband[101];



double M[LEN];
double yadd = 0.0;
double M5[5] = { 1.0, 1.0, 1.0, 1.0, 1.0 };
int hr[3] = { 1, 1, 1 };
int timer = 0, rpos0 = 0;
int detectflag = 1, firstdetect = 1, s3flag = 1;
int outhrate = 0;
#if 0
double test_1s[125] =
{
    585, 585, 584, 584, 578, 578, 577, 577, 573, 573, 569, 569, 570, 570, 566, 566,
    566, 566, 562, 562, 557, 557, 557, 557, 552, 552, 552, 552, 548, 548, 544, 544,
    545, 545, 541, 545, 545, 544, 544, 544, 544, 549, 549, 548, 548, 550, 550, 549,
    549, 548, 548, 550, 550, 548, 548, 550, 550, 549, 549, 548, 548, 550, 550, 549,
    549, 552, 552, 550, 550, 548, 548, 552, 552, 550, 550, 553, 553, 550, 550, 550,
    550, 552, 552, 549, 549, 552, 552, 549, 549, 550, 550, 556, 556, 554, 554, 560,
    560, 558, 558, 558, 558, 561, 561, 561, 561, 565, 562, 562, 564, 564, 565, 565,
    553, 553, 548, 548, 566, 566, 632, 632, 716, 716, 713, 800, 812
};
#endif

//int main(double * pdata, int dlen)

#ifdef _WIN32
int round(double f)
{
    if ((int)f + 0.5 > f)
        return (int)f;
    else
        return (int)f + 1;
}
#endif

int hrate_detect(double * pdata, int dlen)
{

    //double input[125 * 6];
    double input[125 * 10];
    double data[LEN];
    double datab[LEN + 100];
    double Y[LEN];
    int nfilt1 = 2, nfilt2 = 3, nfilt3 = 5, step = 60;
    int mswait = 43, ms1200 = 150, ms350 = 43, ms300 = 37, ms50 = 6, s3 = 375;
    double mc = 0.45;
    int i, j, count, k;
    double newm5, meanM5, mean;
    int  rloc0, rloc, nr, NRR, meanRR;
    double mdec, val, mval, sum = 0.0;
    int idx = 0;
    int hrate = 0;
    int beat[50], rpos[50], RR[50];
    double * pfilt50 = malloc(dlen * sizeof(double));
    double * pfilt35 = malloc(dlen * sizeof(double));
    double * pband = malloc(dlen * sizeof(double));
    //double * pfilt25 = malloc(dlen * sizeof(double));
    double * yp = malloc(dlen * sizeof(double));
    double * yp_hold;
    double *ECG_r0, *ECG_r;
    int Rdx = 0;
    yp_hold = yp;

    if(dlen != LEN)
    {
        ECG_LOG("Must input 6s data\n");
        return -1;
    }

#if 0
    for (j = 0; j < 6; j++)
    {
        for (i = 0; i < 125; i++)
        {
            input[i + j * 125] = test_1s[i];
        }
    }
#else
    memcpy(input, pdata, dlen * sizeof(double));
#endif

    //printf("%s: %d\n",__FUNCTION__, dlen);

    pdata = &input[0];

    //检查是否有效心电数据

    for (count = 0; count < 250; ++count) {
        sum += input[count];
    }
    mean = sum / LEN;
    if (!mean)
    {
        yadd = 0.0;
        for (i = 0; i < 5; i++)
            M5[i] = 1.0;
        for (i = 0; i < 3; i++)
            hr[i] = 1;
        timer = 0, rpos0 = 0;
        detectflag = 1, firstdetect = 1, s3flag = 1;
        outhrate = 0;
    }
    //正常
    else
    {
        //平滑
        pinghua(pdata, pfilt50, LEN, nfilt1);
        //debug
        /*
        for (i = 0; i < LEN; i++)
            data[i] = pfilt50[i];*/
        pinghua(pfilt50, pfilt35, LEN, nfilt2);
        free(pfilt50);
        //debug
        /*
        for (i = 0; i < LEN; i++)
            data[i] = pfilt35[i];*/

        //带通
        for (i = 0; i < 50; i++)
        {
            datab[i] = pfilt35[50 - 1 - i];
            datab[LEN + 50 + i] = pfilt35[LEN - i - 1];
        }
        for (i = 0; i < LEN; i++)
        {
            datab[i + 50] = pfilt35[i];
        }
        free(pfilt35);

        pfilt35 = &datab[0];

        for (i = 0; i < LEN; i++) {
            for (j = 0; j < 101; j++)
            {
                sum += bband[j] * pfilt35[i + j];
            }
            pband[i] = sum;
            sum = 0;
        }
        //debug1
        /*
        for (i = 0; i < LEN; i++)
            data[i] = pband[i];*/

        //square func
        k = 1;
        Y[0] = yadd;
        for (i = 1; i < (LEN - k); i++)
            Y[i] = pband[i] * pband[i] - pband[i - k] * pband[i + k];
        yadd = Y[125];
        Y[LEN - 1] = 0;
        //再次平滑
        pinghua(Y, yp, LEN, nfilt3);
        //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        for (count = 0; count < LEN; ++count) {
            yp[count] = (yp[count] < 0) ? 0 : yp[count];
        }
        for (i = 0; i < LEN; i++) data[i] = yp[i];

        //初始化 M5,M,R
        if (s3flag)
        {
            mval = *yp;
            for (count = 0; count < s3 - 1; ++count) {
                if (*(++yp) > mval) mval = *(yp - 1);
            }
            for (i = 0; i < 5; i++)
                M5[i] = mc * mval * M5[i];

            meanM5 = mean_double_array(M5, 5);
            for (i = 0; i < s3; i++)
                M[i] = meanM5;
            //s3flag = 0;
        }
        else
        {
            for (i = 0; i < LEN; i++)
            {
                if (i < LEN - 125)
                    M[i] = M[125 + i];
                else
                    M[i] = 0;
            }
        }
        for (i = 0; i < 50; i++)
        {
            rpos[i] = 0;
            RR[i] = 0;
        }
        //阈值比较
        for (i = 0; i < LEN; i++)
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
                    //rdec=mdec/1.4;
                }
                else if (!detectflag)
                {
                    if (timer <= mswait || timer > ms1200)
                        M[i] = M[i - 1];
                    else if (timer == mswait + 1)
                    {
                        M[i] = M[i - 1] - mdec;
                        val = data[i - mswait - 1];
                        for (j = i - mswait; j < i; j++)
                        {
                            if (val < data[j]) val = data[j];
                        }
                        newm5 = mc * val;
                        if (newm5 > 1.5 * M5[4]) newm5 = 1.5 * M5[4];
                        for (j = 1; j < 5; j++)
                            M5[j - 1] = M5[j];
                        M5[4] = newm5;
                    }
                    else if (timer > (mswait + 1) && timer <= ms1200)
                        M[i] = M[i - 1] - mdec;
                }
            }



            //MR[i] = M[i];
            if (data[i] >= M[i])
            {
                if (firstdetect || timer > mswait)
                {
                    if (firstdetect) firstdetect = 0;
                    beat[idx] = i;

                    //-200ms开始的400ms区间定位Ymax（rloc0），然后在Ymax（rloc0）前后三分之一×200ms范围内搜寻ECGmax（rloc）
                    ECG_r0 = &data[i - 25];
                    rloc0 = find_maxv_maxloc(ECG_r0, 50) + i - 26;
                    ECG_r = &input[rloc0 - 8];
                    rloc = find_maxv_maxloc(ECG_r, 16);
                    rpos[idx] = rloc + rloc0 - 9;
                    idx++;
                    detectflag = 1;
                    timer = -1;
                }
            }
        }
        /*
            if (data[i] >= M[i])
            {
                if (firstdetect || timer > mswait)
                {
                    if (firstdetect) firstdetect = 0;
                    beat[idx] = i;

                ECG_r = &input[i - step];
                rloc = find_maxv_maxloc(ECG_r, 2 * step);
                rpos[idx] = rloc + i - step;
                idx++; detectflag = 1; timer = -1;
            }
        }
        }
        */
        if (s3flag)
        {
            RR[0] = 0;
            //s3flag = 0;
        }
        else
            RR[0] = rpos[0] + LEN - rpos0;

        rpos0 = rpos[idx - 1];

        nr = idx;

        for (i = 0; i < nr - 1; i++)
            RR[i + 1] = rpos[i + 1] - rpos[i];
        Rdx = 0;
        sum = 0;
        for (i = 0; i < nr - 1; i++)
        {
            if (((sqrt(3)*RR[i] / 3) <= RR[i + 1]) && ((sqrt(3)*RR[i]) >= RR[i + 1]))
            {
                sum += RR[i];
                Rdx++;
            }
        }
        //计算输出心率
        NRR = Rdx;
        if (NRR)
        {
            meanRR = (int) sum / NRR;
            hrate = (int)round(60 * FS / meanRR);
        }
        else
            hrate = outhrate;
        if (s3flag)
        {
            for (i = 0; i < 3; i++)
                hr[i] = hrate;
            s3flag = 0;
        }
        else
        {
            for (i = 0; i < 2; i++)
                hr[i] = hr[i + 1];
            hr[2] = hrate;
        }
        outhrate = (outhrate) ? (outhrate + hrate) / 2 : hrate;

    }

    free(yp_hold);
    free(pband);

    return outhrate;
}