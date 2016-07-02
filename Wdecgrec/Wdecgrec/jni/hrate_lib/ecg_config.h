#ifndef ECG_CONFIG_H
#define ECG_CONFIG_H

#define ECG_CHs  3
#define ECG_WORD_SIZE 2

#define ECG_LEN (ECG_CHs * ECG_WORD_SIZE)

#define HR_LEN 750


#ifndef ECG_DBG
#define ECG_DBG(format, args...) printf(format, ##args)
#endif


#ifndef ECG_LOG
#define ECG_LOG(format, args...) printf(format, ##args)
#endif



#endif
