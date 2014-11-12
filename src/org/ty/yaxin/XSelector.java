package org.ty.yaxin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class XSelector {

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
					if (key.isAcceptable()) {
						System.out.println("siAccept");
					} else if (key.isConnectable()) {
						System.out.println("isConnection");
					} else if (key.isReadable()) {
						System.out.println("isRead");
						channel = (SocketChannel) key.channel();
						buf.clear();
						channel.read(buf);
						buf.flip();
						while(buf.hasRemaining()){
							System.out.println(buf.get());
						}
					} else if (key.isWritable()) {
						System.out.println("isWrite");
					}
					keyIterator.remove();
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
