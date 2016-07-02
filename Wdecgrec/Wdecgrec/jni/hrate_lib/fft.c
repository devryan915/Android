

#include <stdio.h>
#include <math.h>

#include <stdlib.h>


#ifndef BSD
# include <string.h>
#else           /* for Berkeley UNIX only */
# include <strings.h>
# define strchr index
#endif

#define	len	16384		/* maximum points in FFT */
#ifdef i386
#define strcasecmp strcmp
#endif
#define PI	3.1415926	/* pi to machine precision, defined in math.h */
#define TWOPI	(2.0*PI)

void four1();
void realft();

int m, n;
static double *c;
//double  (*window)();

/* See Oppenheim & Schafer, Digital Signal Processing, p. 242 (1st ed.)
   The second edition of Numerical Recipes calls this the "Hann" window. */
double win_hanning(j, n)
int j, n;
{
    double a = 2.0*PI/(n-1), w;

    w = 0.5 - 0.5*cos(a*j);
   // wsum += w;
    return (w);
}

/* This function detrends (subtracts a least-squares fitted line from) a
   a sequence of n uniformily spaced ordinates supplied in c. */
void detrend(double *c, int n)
{
    int i;
    double a, b = 0.0, tsqsum = 0.0, ysum = 0.0, t;
	double r[375];
    for (i = 0; i < n; i++)
	{  r[i] = *(c+i);
	ysum += *(c + i);
	}
    for (i = 0; i < n; i++) {
	t = i - n/2 + 0.5;
	tsqsum += t*t;
	b += t * (* (c + i));
    }
    b /= tsqsum;
    a = ysum/n - b*(n-1)/2.0;
    for (i = 0; i < n; i++)
	{
	  *c -= a + b*i;
	  c++;
	}
}

void fft(double *x, int n)		/* calculate forward FFT */
{
	int i;
	double *xx;
    detrend(x, n);
    for (m = len; m >= n; m >>= 1)
	;
    m <<= 1;		/* m is now the smallest power of 2 >= n; this is the
			   length of the input series (including padding) */
	//window = win_hanning;
	xx = x;
	 for (i = 0; i < n; i++)
	 {
		// *x *= (*window)(i, n);
		 *xx *= win_hanning(i,n);
		 xx++;
	 }
    // free(xx);
  /*  if (fflag) fstep = freq/(2.*m); /* note that fstep is actually half of
				       the frequency interval;  it is
				       multiplied by the doubled index i
				       to obtain the center frequency for
				       bin (i/2) */
    realft(x-1, m/2, 1);	/* perform the FFT;  see Numerical Recipes */
}




void realft(data,n,isign)
double data[];
int n,isign;
{
    int i, i1, i2, i3, i4, n2p3;
    double c1 = 0.5, c2, h1r, h1i, h2r, h2i;
    double wr, wi, wpr, wpi, wtemp, theta;
    void four1();

    theta = PI/(double) n;
    if (isign == 1) {
	c2 = -0.5;
	four1(data, n, 1);
    } 
    else {
	c2 = 0.5;
	theta = -theta;
    }
    wtemp = sin(0.5*theta);
    wpr = -2.0*wtemp*wtemp;
    wpi = sin(theta);
    wr = 1.0+wpr;
    wi = wpi;
    n2p3 = 2*n+3;
    for (i = 2; i <= n/2; i++) {
	i4 = 1 + (i3 = n2p3 - (i2 = 1 + ( i1 = i + i - 1)));
	h1r =  c1*(data[i1] + data[i3]);
	h1i =  c1*(data[i2] - data[i4]);
	h2r = -c2*(data[i2] + data[i4]);
	h2i =  c2*(data[i1] - data[i3]);
	data[i1] =  h1r + wr*h2r - wi*h2i;
	data[i2] =  h1i + wr*h2i + wi*h2r;
	data[i3] =  h1r - wr*h2r + wi*h2i;
	data[i4] = -h1i + wr*h2i + wi*h2r;
	wr = (wtemp = wr)*wpr - wi*wpi+wr;
	wi = wi*wpr + wtemp*wpi + wi;
    }
    if (isign == 1) {
	data[1] = (h1r = data[1]) + data[2];
	data[2] = h1r - data[2];
    } else {
	data[1] = c1*((h1r = data[1]) + data[2]);
	data[2] = c1*(h1r - data[2]);
	four1(data, n, -1);
    }
}

void four1(data, nn, isign)
double data[];
int nn, isign;
{
    int n, mmax, m, j, istep, i;
    double wtemp, wr, wpr, wpi, wi, theta;
    double tempr, tempi;
    
    n = nn << 1;
    j = 1;
    for (i = 1; i < n; i += 2) {
	if (j > i) {
	    tempr = data[j];     data[j] = data[i];     data[i] = tempr;
	    tempr = data[j+1]; data[j+1] = data[i+1]; data[i+1] = tempr;
	}
	m = n >> 1;
	while (m >= 2 && j > m) {
	    j -= m;
	    m >>= 1;
	}
	j += m;
    }
    mmax = 2;
    while (n > mmax) {
	istep = 2*mmax;
	theta = TWOPI/(isign*mmax);
	wtemp = sin(0.5*theta);
	wpr = -2.0*wtemp*wtemp;
	wpi = sin(theta);
	wr = 1.0;
	wi = 0.0;
	for (m = 1; m < mmax; m += 2) {
	    for (i = m; i <= n; i += istep) {
		j =i + mmax;
		tempr = wr*data[j]   - wi*data[j+1];
		tempi = wr*data[j+1] + wi*data[j];
		data[j]   = data[i]   - tempr;
		data[j+1] = data[i+1] - tempi;
		data[i] += tempr;
		data[i+1] += tempi;
	    }
	    wr = (wtemp = wr)*wpr - wi*wpi + wr;
	    wi = wi*wpr + wtemp*wpi + wi;
	}
	mmax = istep;
    }
}