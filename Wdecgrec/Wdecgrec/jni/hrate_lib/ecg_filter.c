#include <stdio.h>
#include <stdlib.h>
#include <malloc.h>
#include <stdarg.h>




#include "ecg_filter.h"







#define IDX(I)  ((I) & RING_MASK)
#define IDX250(I)  ((I) & (RING_LEN*2-1))


extern double band_125[125];
extern double band_1800[];
extern double *ECGp;

extern int hrate_detect(double * pdata, int dlen);

static void ring_put(ECG_HND * hnd, int ch, int d)
{
    hnd->ecg_chx_input[ch][IDX(hnd->idx_w[ch])] = d;


    /*��ֵ��250Hz*/
    hnd->ecg250_chx_input[ch][IDX250(hnd->idx250_w[ch])]
        = (hnd->ecg_chx_input[ch][IDX(hnd->idx_w[ch]-1)] + hnd->ecg_chx_input[ch][IDX(hnd->idx_w[ch])])/2 ;

    hnd->ecg250_chx_input[ch][IDX250(hnd->idx250_w[ch] + 1)] = hnd->ecg_chx_input[ch][IDX(hnd->idx_w[ch])] ;

#if 0
    ECG_DBG("Input :CH %d:0x%08x: %6d(%lf)\n", hnd->channel_id, hnd->idx_w[ch], d, hnd->ecg_chx_input[ch][IDX(hnd->idx_w[ch])]);
    ECG_DBG("Input :CH %d:0x%08x: (%lf), (%lf)\n", hnd->channel_id, hnd->idx250_w[ch],
        hnd->ecg250_chx_input[ch][IDX250(hnd->idx250_w[ch])], hnd->ecg250_chx_input[ch][IDX250(hnd->idx250_w[ch]+1)]);
#endif

    hnd->idx_w[ch]++;

    hnd->idx250_w[ch] += 2;
}

#if 0
static inline int ring_counter(ECG_HND * hnd)
{
    int numbers = (int)(hnd->idx_w - hnd->idx_r);

    return numbers;
}
#endif


ECG_HND *  ecg_init(int channel_id)
{
    ECG_HND * hnd;

    int i;

    hnd = malloc(sizeof(ECG_HND));
    //memset(hnd, 0, sizeof(ECG_HND));

    for(i=0;i<3;i++)
        hnd->idx_w[i] = 0;

    hnd->fl_pass = 1;
    hnd->hr_pass = 1;
    hnd->channel_id = channel_id;

    ECG_LOG("Init CH %d Success\n", hnd->channel_id);

    return hnd;
}

ECG_E ecg_input(ECG_HND *hnd, void * input, int len, void *output, int * outlen)
{
    int i, j;
    unsigned idx;
	double hr_buffer[HR_LEN];

    unsigned char * data = input;

    if(len % ECG_WORD_SIZE) {
        ECG_LOG("data length error %d\n", len);
        return ECG_ERR_UNALIG;
    }

    *outlen = 0;  // Clean outlen

    len = len / ECG_WORD_SIZE;

    for(i = 0; i < len; i++)
    {
        short v = data[i* ECG_WORD_SIZE + 0] << 8 | data[i * ECG_WORD_SIZE + 1];

        ring_put(hnd, 0, v);
    }
    ECG_DBG("CH %d After enqueue IDX 0x%08x  0x%08x, Samples %d\n",hnd->channel_id, hnd->idx_w[0], hnd->idx250_w[0], len);


    /* �˲����� */
    idx = hnd->idx250_w[0];
    idx -= 2*len;

    ECG_DBG("First IDX250 0x%08x\n", idx);

    for(i=0; i < 2*len; i++, idx++)
    {
        unsigned char * data = (unsigned char *) output + ( i >> 1) * ECG_WORD_SIZE;
        int v;

        if(hnd->fl_pass && idx <= 1800)
        {
            /* �ʼ�� 1800 ����, ԭ����� */
            v = (int)hnd->ecg250_chx_input[0][IDX250(idx)];
        }
        else
        {
            /* �������������� */
            double sum = 0;

            hnd->fl_pass = 0;

            for(j = 0; j < 1801; j++)
            {
                //ECG_DBG("    sum = %lf", sum);
                sum += band_1800[j] * hnd->ecg250_chx_input[0][IDX250(idx - j)];
                //ECG_DBG("          %lf += %lf * %lf\n", sum, band_125[j], hnd->ecg_chx_input[0][IDX(idx - j)] );
            }
            v = (int) sum;
        }

        if((idx & 0x1) == 0x1)
        {
            data[0] = (v & 0xff00) >> 8;
            data[1] = (v & 0xff);
        }

        //ECG_DBG("Output:CH %d:0x%08x:%6d[0x%02x %02x]\n", hnd->channel_id ,idx,  v, data[0], data[1]);
    }

    *outlen = len * ECG_WORD_SIZE;

    /*
     * ���ʼ���: ���ʼ���������㹻�����ݲſ��Կ�ʼ����
     * Ϊ�˼��ټ�������������channel 1�ϼ�����������
     *
     */
    if(hnd->channel_id != 0)
        return ECG_OK;

    /* ����Ƿ����㹻���������� */
    if(hnd->hr_pass && hnd->idx_w[0] <= HR_LEN)
        return ECG_OK;

    hnd->hr_pass = 0;

    /* ���ݹ��������£�������� */
    

    idx = hnd->idx_w[0]; 
    idx -= HR_LEN;


    /* ����ͨ�����ݵ�ƽ̹���棬Դ�����ڻ��ζ����У� ���ܻ��� */
    for(i=0; i<HR_LEN; i++,idx++)
    {
        hr_buffer[i] = hnd->ecg_chx_input[0][IDX(idx)];
    }

    hnd->hrate = hrate_detect(hr_buffer, HR_LEN);

    return hnd->hrate;
}

void   ecg_close(ECG_HND *hnd)
{
    free(hnd);
    return ;
}















