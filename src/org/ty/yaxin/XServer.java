package org.ty.yaxin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class XServer implements Runnable{

	public static void main(String[] args){
		new Thread(new XServer()).start();
	}
	
	private ServerSocketChannel ssc;
	private Selector selector;
	private SelectionKey skey;
	
	public void init(){
		try {
			ssc = ServerSocketChannel.open();
			ssc.socket().bind(new InetSocketAddress(9999));
			ssc.configureBlocking(false);
			selector = Selector.open();
			skey = ssc.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		init();
		while(true){
			try {
				if(selector.select()>0){
					Iterator<SelectionKey> it = selector.selectedKeys().iterator();
					while(it.hasNext()){
						SelectionKey key = it.next();
						it.remove();
						if(!key.isValid()){
							continue;
						}
						else if(key.isAcceptable()){
							System.out.println("#conn");
							conn(key);
							
						}else if(key.isReadable()){
							System.out.println("#read");
							read(key);
							
						}else if(key.isWritable()){
							System.out.println("#write");
							write(key);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void conn(SelectionKey key){
		
		try {
			ServerSocketChannel channel = (ServerSocketChannel) key.channel();
			SocketChannel client = channel.accept();
			client.configureBlocking(false);
			client.register(selector, SelectionKey.OP_READ);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void read(SelectionKey key){
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buf = ByteBuffer.allocate(48);
		buf.clear();
		StringBuffer sb = new StringBuffer();
		int len=0;
		try {
			while((len = channel.read(buf))>0){
				buf.flip();
				sb.append(new String(buf.array(), 0, len));  
			}
			if(sb.length()>0){
				System.out.println(sb.toString());
			}
			if("quit".equals(sb.toString().trim())){
				channel.write(ByteBuffer.wrap("quit".getBytes()));
				key.cancel();
				channel.close();
				channel.socket().close();
			}else{
				key.interestOps(SelectionKey.OP_WRITE | key.interestOps());
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void write(SelectionKey key){
		Scanner cin=new Scanner(System.in);
		String input=cin.nextLine();
		SocketChannel channel = (SocketChannel) key.channel();
		try {
			channel.write(ByteBuffer.wrap(input.getBytes()));
			key.interestOps(SelectionKey.OP_READ);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
