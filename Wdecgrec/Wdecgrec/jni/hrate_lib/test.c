#include <stdio.h>
#include <unistd.h>



#include "ecg_filter.h"


short test_1s[125] =
{
    4352, 34, 4369, 21845, 578, 578, 577, 577, 573, 573, 569, 569, 570, 570, 566, 566,
    566, 566, 562, 562, 557, 557, 557, 557, 552, 552, 552, 552, 548, 548, 544, 544,
    545, 545, 541, 545, 545, 544, 544, 544, 544, 549, 549, 548, 548, 550, 550, 549,
    549, 548, 548, 550, 550, 548, 548, 550, 550, 549, 549, 548, 548, 550, 550, 549,
    549, 552, 552, 550, 550, 548, 548, 552, 552, 550, 550, 553, 553, 550, 550, 550,
    550, 552, 552, 549, 549, 552, 552, 549, 549, 550, 550, 556, 556, 554, 554, 560,
    560, 558, 558, 558, 558, 561, 561, 561, 561, 565, 562, 562, 564, 564, 565, 565,
    553, 553, 548, 548, 566, 566, 632, 632, 716, 716, 713, 800, 812
};


#define TEST_LEN 4
int main()
{
    ECG_HND * hnd[ECG_CHs];
    ECG_E     rc;


    short input[TEST_LEN*3];
    short output[TEST_LEN*3];
    int i,j,k;
    unsigned int cn;

    for(i=0;i<ECG_CHs;i++)
    {
        hnd[i] = ecg_init(i);

        if(hnd[i] == NULL)
        {
            ECG_LOG("ECG CH %d init failed\n",i);
            return -1;
        }
    }

    for(i=0; i<ECG_CHs; i++)
    {
        for(j=0;j<TEST_LEN;j++)
        {
            input[i*TEST_LEN +j] = htons(test_1s[j]);
        }

        //ECG_LOG("%hd %hd %hd\n", ntohs(input[i*3 +0]), ntohs(input[i*3 +1]), ntohs(input[i*3 +2]));
    }

    cn = 0;

    while(1)
    {
        int outlen;

        for(i =0 ;i<3; i++)
        {
            rc = ecg_input(hnd[i], input + i*TEST_LEN , TEST_LEN * 2, output + i*TEST_LEN, &outlen);
            if(rc < ECG_OK)
            {
                ECG_LOG("ecg_input return error %d\n", rc);
                goto out;
            } else if(rc > 0)
                ECG_LOG("HR %d\n", rc);

            if( outlen > 0) {
                for(j=0;j<outlen/2;j++)
                {
                    ECG_LOG("Seq %d CH %d %hd -> %hd\n", cn,
                        hnd[i]->channel_id, ntohs(input[j]), ntohs(output[j]));
                }
            }
        }

        cn ++;

        sleep(1);
    }

out:
    for(i=0;i<ECG_CHs;i++)
        ecg_close(hnd[i]);

    ECG_LOG("test program exit\n");

    return 0;
}
