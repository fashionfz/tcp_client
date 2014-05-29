/**
 *  2007/01/24
 *
 * Copyright (c) 2000-2002 中润科技有限公司, Inc. All Rights Reserved.
 *
 * 项目说明 : N/A
 * 版    本 : 1.0
 * 编 写 者 : 短信项目组
 * 网    址 : http://www.citycomm.cn
 * 电    话 : (008629)87607305
 * 电子邮件 : contact@citycomm.cn
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