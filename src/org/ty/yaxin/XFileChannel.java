package org.ty.yaxin;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class XFileChannel {

	public static void main(String[] args){
		Path path =Paths.get("e:\\xx.txt");
		try {
			FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);
			/**
			 * allocateDirectpublic static ByteBuffer allocateDirect(int capacity)
			 * �����µ�ֱ���ֽڻ������� �»�������λ�ý�Ϊ�㣬����޽�Ϊ�������������ǲ�ȷ���ġ�
			 * �������Ƿ���еײ�ʵ�����飬���Ƕ��ǲ�ȷ���ġ� ������capacity - �»�������������
			 * ���ֽ�Ϊ��λ
			 * 
			 * allocatepublic static ByteBuffer allocate(int capacity)
			 * ����һ���µ��ֽڻ������� �»�������λ�ý�Ϊ�㣬����޽�Ϊ�������������ǲ�ȷ���ġ�
			 * ��������һ���ײ�ʵ�����飬���� ����ƫ������Ϊ�㡣 ������capacity - �»�������������
			 * ���ֽ�Ϊ��λ 
			 * 
			 * allocate��allocateDirect������������ͬ�Ĺ�������ͬ����allocateDirect����
			 * ֱ��ʹ�ò���ϵͳ������Buffer����������ṩ����ķ����ٶȡ����ҵ��ǣ��������е��������
			 * ֧������ֱ�ӷ���ķ�����Sun�Ƽ������ֽ�Ϊ��λ��ֱ���ͻ�����allocateDirect����������ļ�
			 * ��ز����нϳ��������ڵĻ�������
			 */
			ByteBuffer buf = ByteBuffer.allocate(48);
			int length =channel.read(buf);
			buf.flip();
			while(length!=-1){
				System.out.println("Read " + length);
				
				while(buf.hasRemaining()){
					System.out.print((char) buf.get());
				}
				buf.clear();
				length = channel.read(buf);
			}
			channel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
