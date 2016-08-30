#include "stdlib.h"

void pinghua(double *src, double *dst, int len, int winlen)
{
    int deunit = winlen / 2;
    int i;
    double sum;

    double *data = (double *)calloc(len + 2 * deunit, sizeof(double));
    //数据补偿
    if (deunit <= 1)
    {
        data[0] = src[0];
        data[len + 1] = src[len - 1];
    }
    else
    {
        for (i = 0; i < deunit; i++)
        {

            data[i] = src[deunit - 1 - i];
            data[len + deunit + i] = src[len - i - 1];
        }
    }
    for(i = 0; i < len; i++)
    {
        data[i + deunit] = src[i];
    }
    //平滑处理
    sum = 0;
    for(i = 0; i < len; i++)
    {
        int count;
        for(count = 0; count < winlen; count++)
        {
            sum += data[i + count];
        }
        sum = sum / winlen;
        dst[i] = sum;
        sum = 0;
    }
}