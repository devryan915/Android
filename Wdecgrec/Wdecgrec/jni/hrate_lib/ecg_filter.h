#ifndef ECG_FILTER_H
#define ECG_FILTER_H

#include "ecg_config.h"


#define RING_LEN   4096
#define RING_MASK  0xfff


typedef struct {
    double ecg_chx_input[1][RING_LEN];
    double ecg250_chx_input[1][ RING_LEN * 2 ];

    unsigned int idx_w[1];
    unsigned int idx250_w[1];

    int fl_pass;
    int hr_pass;
    int hrate;

    int channel_id;

} ECG_HND;

typedef enum
{
    ECG_ERR_UNALIG,
    ECG_OK = 0,
} ECG_E;

extern ECG_HND * ecg_init(int channel_id);
extern ECG_E  ecg_input(ECG_HND *hnd, void * input, int len, void *output, int * outlen);
extern void   ecg_close(ECG_HND *hnd);

#endif
