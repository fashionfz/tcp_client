package org.ty.yaxin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class XServer {

	public static void main(String[] args){
		try {
			ServerSocketChannel server = ServerSocketChannel.open();
			server.socket().bind(new InetSocketAddress(9999));
			while(true){
				SocketChannel channel = server.accept();
				ByteBuffer buf = ByteBuffer.allocate(48);
				int length = channel.read(buf);
				buf.flip();
				while(length!=-1){
					System.out.println("length"+length);
					while(buf.hasRemaining()){
						System.out.println("server:"+(char)buf.get());
					}
					break;
				}
				System.out.println("server read over");
				buf.clear();
				buf.put("server answer".getBytes());
				channel.write(buf);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
