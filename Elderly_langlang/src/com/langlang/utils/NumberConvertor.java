package com.langlang.utils;

public class NumberConvertor {
//	 public static void main(String[] args)  
//	 {  
//	  System.out.println("-----     Test  ----------");  
//	        System.out.println(Integer.MAX_VALUE);  
//	        int ibeforeChange,iafterChange;  
//	        for(int i=Integer.MIN_VALUE;i<Integer.MAX_VALUE;i++)  
//	        {  
//	            ibeforeChange=i;  
//	            iafterChange = convertToInt(convertToShort(ibeforeChange));  
//	            if(ibeforeChange!=iafterChange)  
//	            {  
//	                System.out.println("Num:"+ i+" BeforeChange int=" + ibeforeChange + "  AfterChange int=" + iafterChange + "Change False!");  
//	                continue;  
//	            }  
//	            System.out.println("True");  
//	        }  
//	 }  
//	public static short[] convertToShort(int i)  
//	{  
//		short[] a=new short[2];  
//		a[0] = (short) (i & 0x0000ffff);          //将整型的低位取出,  
//	    a[1] = (short) (i >> 16);                     //将整型的高位取出.  
//	    return a;  
//	}  
//	     
//	public static int convertToInt(short[] a)  
//	{  
//		return ((a[1]<<16))|(a[0]&0x0000ffff);  //做&操作可以保证转化后的数据长度保持16位!  
//	}	
	
	public static short convertSignedIntToSignedShort(int i)  {
		return Short.valueOf(Integer.toString(i));
	}
	
	public static int bytesToInt(byte a, byte b)  {
		int tmp = 0x00000000;	
		int tmp1 = 0x00000000;
		
		if ((a & 0x80) > 0) {
			tmp = (~a);
			tmp = tmp << 4;
			
			tmp1 = 0x0f & ((~b) >> 4);
			tmp = (tmp | tmp1);
			return -tmp;
		} else {
			tmp = a;
			tmp = tmp << 4;
			
			tmp1 = 0x0f & (b >> 4);
			tmp = tmp | tmp1;
			
			return tmp;
		}
	}
}
