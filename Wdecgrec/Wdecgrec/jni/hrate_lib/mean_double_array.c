/////////////////////////////////////////////////////////////////////////
//对double数组求平均
/////////////////////////////////////////////////////////////////////////
#include <stdio.h>
#include <stdlib.h>
double mean_double_array(double *dp, int len)
{
	if ( dp == NULL ){
		//puts("pointer invalid.");
		//exit(0);
		return 0;
	}
	int count = 0;
	double sum = 0;
	for(count = 0; count < len; ++count){
		sum += dp[count];
	}
	double mean = sum/len;
	return mean;
}