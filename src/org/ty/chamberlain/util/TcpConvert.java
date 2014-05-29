/**
 *  2007/01/24
 *
 * Copyright (c) 2000-2002 ����Ƽ����޹�˾, Inc. All Rights Reserved.
 *
 * ��Ŀ˵�� : N/A
 * ��    �� : 1.0
 * �� д �� : ������Ŀ��
 * ��    ַ : http://www.citycomm.cn
 * ��    �� : (008629)87607305
 * �����ʼ� : contact@citycomm.cn
 *          
 */

package org.ty.chamberlain.util;


public class TcpConvert
{

    public TcpConvert()
    {
    }

  
    public static int byte2int(byte b[])
    {
        return b[0] & 0xff | (b[1] & 0xff) << 8 | (b[2] & 0xff) << 16 | (b[3] & 0xff) << 24;
    }


    public static byte[] int2byte(int n)
    {
        byte b[] = new byte[4];
        b[3] = (byte)(n >> 24);
        b[2] = (byte)(n >> 16);
        b[1] = (byte)(n >> 8);
        b[0] = (byte)n;
        return b;
    }

   
    
    
    public static void main(String[] args) {
    	byte[] b = TcpConvert.int2byte(257);
    	
    	for (byte c : b) {
			System.out.print(c);
		}
    	System.out.println();
    	
    	System.out.println(TcpConvert.byte2int(b));
		
    }
    
}