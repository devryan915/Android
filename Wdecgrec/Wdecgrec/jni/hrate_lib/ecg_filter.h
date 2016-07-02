#ifndef ECG_FILTER_H
#define ECG_FILTER_H

#include "ecg_config.h"


#define RING_LEN   1024
#define RING_MASK  0x3ff


typedef struct {
    double ecg_chx_input[ECG_CHs][RING_LEN];

    unsigned int idx_w[ECG_CHs];

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
