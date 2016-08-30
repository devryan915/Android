
#include <stdio.h>
#include <stdlib.h>
double mean_double_array(double *dp, int len)
{
    int count = 0;
    double sum = 0;
    double mean;

    if ( dp == NULL ) {
        //puts("pointer invalid.");
        //exit(0);
        return 0;
    }
   
    for(count = 0; count < len; ++count) {
        sum += dp[count];
    }
    mean = sum / len;
    return mean;
}