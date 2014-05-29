package org.ty.chamberlain.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TcpHead {



	/**
	 * 
	 * @param tcpHead
	 *            整个TCP字节头数组
	 * @param index
	 *            从哪个下标开始截取
	 * @param returnByte
	 *            返回的字节数组
	 * @return
	 */
	private static void getByte(byte[] tcpHead, int index, byte[] returnByte) {
		System.arraycopy(tcpHead, index, returnByte, 0, returnByte.length);
	}

	/**
	 * 返回TCP头的字节数组
	 * 
	 * @param tcpHealLength
	 *            tcp头长度
	 * @param version
	 *            版本号
	 * @param xmlLength
	 *            xml长度
	 * @param dictateNo
	 *            指令编号
	 * @param instructionNo
	 *            指令序号
	 * @param sourceId
	 *            源ID
	 * @param purposeId
	 *            目的
	 * @return
	 */
	public static byte[] getTcpHead(int tcpHealLength, int version,
			int xmlLength, int dictateNo, int instructionNo, int sourceId,
			int purposeId) {

		byte[] versionByte = TcpConvert.int2byte(version);
		byte[] xmlLengthByte = TcpConvert.int2byte(xmlLength);
		byte[] dictateNoByte = TcpConvert.int2byte(dictateNo);
		byte[] instructionNoByte = TcpConvert.int2byte(instructionNo);
		byte[] sourceIdByte = TcpConvert.int2byte(sourceId);
		byte[] purposeIdByte = TcpConvert.int2byte(purposeId);

		ByteBuffer buffer = ByteBuffer.allocate(tcpHealLength);
		buffer.order(ByteOrder.BIG_ENDIAN);

		buffer.put(versionByte);
		buffer.put(xmlLengthByte);
		buffer.put(dictateNoByte);
		buffer.put(instructionNoByte);
		buffer.put(sourceIdByte);
		buffer.put(purposeIdByte);

		return buffer.array();
	}
	
	
	
	public static byte[] getTcpHead(int tcpHealLength, int version,
			int xmlLength, int dictateNo, int instructionNo, String sourceId,
			String purposeId) {

		byte[] versionByte = TcpConvert.int2byte(version);
		byte[] xmlLengthByte = TcpConvert.int2byte(xmlLength);
		byte[] dictateNoByte = TcpConvert.int2byte(dictateNo);
		byte[] instructionNoByte = TcpConvert.int2byte(instructionNo);
		
		ByteBuffer sourceBuffer = ByteBuffer.allocate(32);
		sourceBuffer.put(sourceId.getBytes());
		
		
//		byte[] sourceIdByte = sourceId.getBytes();
		
        ByteBuffer purposeBuffer = ByteBuffer.allocate(32);
        purposeBuffer.put(purposeId.getBytes());
//		byte[] purposeIdByte = purposeId.getBytes();

		ByteBuffer buffer = ByteBuffer.allocate(tcpHealLength);
		buffer.order(ByteOrder.BIG_ENDIAN);

		buffer.put(versionByte);
		buffer.put(xmlLengthByte);
		buffer.put(dictateNoByte);
		buffer.put(instructionNoByte);
		buffer.put(sourceBuffer.array());
		buffer.put(purposeBuffer.array());

		return buffer.array();
	}


	/**
	 * 返回字节头的数组节
	 */
	public static byte[] getTcpHead(int tcpLength,ByteBuffer buffer) {
		
		byte[] tcpHead = new byte[tcpLength];
		buffer.get(tcpHead);

		return tcpHead;
	}
	
	/**
	 * 返回字节结果
	 */
	public static int getResult(byte[] tcpHead,int index){
		
		byte[] result = new byte[4];
		getByte(tcpHead, index, result);
		
		return TcpConvert.byte2int(result);
	}
	
	
	/**
	 * 返回XML内容信息
	 */
	public static String getXml(int xmlLength,ByteBuffer buffer){
		

		byte[] xmlByte = new byte[xmlLength];
		buffer.get(xmlByte, 0, xmlByte.length);
		
		String xmlStr = "";
		try {
			xmlStr = new String(xmlByte,"gb2312");
			xmlStr = xmlStr.replaceAll("“","\"");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return xmlStr;
	}

}
