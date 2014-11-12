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
			 * 分配新的直接字节缓冲区。 新缓冲区的位置将为零，其界限将为其容量，其标记是不确定的。
			 * 无论它是否具有底层实现数组，其标记都是不确定的。 参数：capacity - 新缓冲区的容量，
			 * 以字节为单位
			 * 
			 * allocatepublic static ByteBuffer allocate(int capacity)
			 * 分配一个新的字节缓冲区。 新缓冲区的位置将为零，其界限将为其容量，其标记是不确定的。
			 * 它将具有一个底层实现数组，且其 数组偏移量将为零。 参数：capacity - 新缓冲区的容量，
			 * 以字节为单位 
			 * 
			 * allocate和allocateDirect方法都做了相同的工作，不同的是allocateDirect方法
			 * 直接使用操作系统来分配Buffer。因而它将提供更快的访问速度。不幸的是，并非所有的虚拟机都
			 * 支持这种直接分配的方法。Sun推荐将以字节为单位的直接型缓冲区allocateDirect用于与大型文件
			 * 相关并具有较长生命周期的缓冲区。
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
