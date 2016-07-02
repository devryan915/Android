/////////////////////////////////////////////////////////////////
//在double数组中找出最大值，返回其中一个的位置："first" or "last"
/////////////////////////////////////////////////////////////////
#include <stdio.h>
#include <stdlib.h>
int find_maxv_maxloc( double *p, 
						  int len )
{
	double max = *p;
	int count = 0;
	int max_pos = 0;
	if ( !p){
		//puts(" Warning: pointer invalid! ");
		//exit(0);
		return 0;
	}


			for ( count = 0; count < len; ++count ){
				if ( *p++ > max ) {
					max = *(p-1);
					max_pos = count;
				}
			}

			//puts("invalid num input!");
			//exit(0);


	return max_pos;
}