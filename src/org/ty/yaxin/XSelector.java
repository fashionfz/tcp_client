package org.ty.yaxin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import com.sun.xml.internal.messaging.saaj.util.CharReader;

public class XSelector{

	public static void main(String[] args) {
		try {
			SocketChannel channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.connect(new InetSocketAddress("localhost", 9999));
			while(! channel.finishConnect() ){

			}
			ByteBuffer buf = ByteBuffer.allocate(48);
			buf.clear();
			buf.put("client".getBytes());
			buf.flip();
			channel.write(buf);

			Selector selector = Selector.open();
			
			channel.register(selector, SelectionKey.OP_READ);
			while (true) {
				int readyChannels = selector.select();
				if (readyChannels == 0)
					continue;
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
				while (keyIterator.hasNext()) {
					SelectionKey key = keyIterator.next();
					keyIterator.remove();
					if (key.isAcceptable()) {
						System.out.println("#siAccept");
					} else if (key.isConnectable()) {
						System.out.println("#isConnection");
					} else if (key.isReadable()) {
						System.out.println("#isRead");
						channel = (SocketChannel) key.channel();
						int len = 0;
						buf.clear();
						StringBuffer sb = new StringBuffer();
						while((len = channel.read(buf))>0){
							buf.flip();
							sb.append(new String(buf.array(),0,len));
						}
						System.out.println(sb.toString());
						key.interestOps(SelectionKey.OP_WRITE);

						
					} else if (key.isWritable()) {
						System.out.println("#isWrite");
						
						Scanner cin=new Scanner(System.in);
						String input=cin.nextLine();
						buf.clear();
						buf.put(input.getBytes());
						buf.flip();
						channel.write(buf);
						key.interestOps(SelectionKey.OP_READ);
						System.out.println("client write over");
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
