package org.ty.chamberlain.tcp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.ty.chamberlain.model.Camera;
import org.ty.chamberlain.model.TcpModel;
import org.ty.chamberlain.util.TcpHead;
import org.ty.chamberlain.util.XmlUtil;

/**
 * NIO TCP 客户端
 * 
 * @date 2010-2-3
 * @time 下午03:33:26
 * @version 1.00
 */
public class TCPClient {
	// 信道选择器
	private Selector selector;

	// 与服务器通信的信道
	SocketChannel socketChannel;

	// 要连接的服务器Ip地址
	private String hostIp;

	// 要连接的远程服务器在监听的端口
	private int port;

	/**
	 * 构造函数
	 * 
	 * @param HostIp
	 * @param HostListenningPort
	 * @throws IOException
	 */
	public TCPClient(String hostIp, int port) throws IOException {

		this.hostIp = hostIp;
		this.port = port;

		initialize();
	}

	public TCPClient(String hostIp) throws IOException {

		// if ( this.port == 0) {
		this.hostIp = hostIp;
		this.port = XmlUtil.TCP_PORT;
		// }

		initialize();
	}

	/**
	 * 初始化
	 * 
	 * @throws IOException
	 */
	private void initialize() throws IOException {
		// 打开监听信道并设置为非阻塞模式
		socketChannel = SocketChannel.open();
	//	socketChannel.connect(new InetSocketAddress(hostIp, port)); //此方法不能设置连接超时时间，暂时不用这个方法
		socketChannel.socket().connect(new InetSocketAddress(hostIp, port), 3000); //方法可以设置3000毫秒超时，暂时取代上面的连接方法

		socketChannel.configureBlocking(false);

		// 打开并注册选择器到信道
		selector = Selector.open();
		socketChannel.register(selector, SelectionKey.OP_READ);

		// 读取数据
		// new TCPClientReadThread(selector);

	}

	/**
	 * 读取服务器信息
	 * 
	 * @param selector
	 */
	private TcpModel readMsg() {

		TcpModel model = null;

		try {
			if (selector.select() > 0) {
				// 遍历每个有可用IO操作Channel对应的SelectionKey
				for (SelectionKey sk : selector.selectedKeys()) {

					// 如果该SelectionKey对应的Channel中有可读的数据
					if (sk.isReadable()) {

						model = new TcpModel();

						// 使用NIO读取Channel中的数据
						SocketChannel sc = (SocketChannel) sk.channel();
						ByteBuffer buffer = ByteBuffer.allocate(100000);

						sc.read(buffer);
						buffer.flip();
//						System.out.println(buffer);
//						for (byte by : buffer.array()) {
//							System.out.print(by);
//						}
//						System.out.println();
						if (buffer.limit() > 0) {

							byte[] tcpHead = TcpHead.getTcpHead(20, buffer);
							int xmlLength = TcpHead.getResult(tcpHead, 4);

							model.setVersion(TcpHead.getResult(tcpHead, 0));
							model.setXmlLength(xmlLength);
							model.setDictateNo(TcpHead.getResult(tcpHead, 8));
							model.setInstructionNo(TcpHead.getResult(tcpHead,
									12));
							model.setReturnNo(TcpHead.getResult(tcpHead, 16));
							model.setXmlResult(TcpHead
									.getXml(xmlLength, buffer));
							/*
							System.out.println("接收到来自服务器"
									+ sc.socket().getRemoteSocketAddress()
									+ "的信息:" + "");
							System.out.println("版本号:" + model.getVersion()
									+ "	  XML长度:" + xmlLength + "	指令编号:"
									+ model.getDictateNo() + "	指令序号:"
									+ model.getInstructionNo() + "      返回码:"
									+ model.getReturnNo());

							System.out.println("xml内容为:\n"
									+ model.getXmlResult());
							*/
							sk.interestOps(SelectionKey.OP_READ);
							sc.close();

							break;
						}

					}

					// 删除正在处理的SelectionKey
					selector.selectedKeys().remove(sk);
				}
			}
		} catch (IOException ex) {
			System.out.println("读取出错.");
			ex.printStackTrace();
		} finally {
			if (selector.isOpen()) {
				try {
					selector.close();
				} catch (IOException e) {
					System.out.println("关闭出错!");
					e.printStackTrace();
				}
			}
		}

		return model;
	}

	/**
	 * 发送字符串到服务器
	 * 
	 * @param message
	 * @throws IOException
	 */
	private TcpModel sendMsg(String message, byte[] tcphead) throws IOException {

		ByteBuffer sumBuffer = ByteBuffer
				.allocate(message.getBytes("gb2312").length + tcphead.length);
		sumBuffer.order(ByteOrder.BIG_ENDIAN);
		// sumBuffer.put(message.getBytes("gb2312"));

		sumBuffer.put(tcphead);
		sumBuffer.put(message.getBytes("gb2312"));

		ByteBuffer buffer = ByteBuffer.wrap(sumBuffer.array());

		socketChannel.write(buffer);

		return readMsg();// 读取数据
	}

	/**
	 * 发送TCP请求
	 * 
	 * @param version
	 *            版本号
	 * @param dictateNo
	 *            编号
	 * @param sendMessage
	 *            发送xml内容
	 * @return
	 * @throws IOException
	 */
	public TcpModel sendClient(int version, int dictateNo, String sendMessage)
			throws IOException {

		return sendMsg(sendMessage, TcpHead.getTcpHead(80, version, sendMessage
				.getBytes("gb2312").length, dictateNo, 0, 0, 0));
	}
	
	
	/**
	 * 
	 * @param version 版本号 257
	 * @param dictateNo 指令编号 5064
	 * @param dvrId 目地ID(dvr)
	 * @param sendMessage
	 * @return
	 * @throws IOException
	 */
	public TcpModel sendLightSockt(int version, int dictateNo,String dvrId, String sendMessage)
			throws IOException {

		return sendMsg(sendMessage, TcpHead.getTcpHead(80, version, sendMessage
				.getBytes("gb2312").length, dictateNo, 1, "0000000000000000000000000000000", dvrId));
	}

	/**
	 * 返回摄像头ID和状态
	 * 
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public List<Camera> getCamera(String xml) {
		return XmlUtil.parserXml(xml);
	}

	public void getChucu(TCPClient client) {

		// TCPClient client = new TCPClient("222.211.76.4");
		//		String sendMessage = "<?xml version=\"1.0\"encoding=\"gb2312\"?>"
		//				+ "<Message>" + "<CameraList>" + "<CameraID></CameraID>"
		//				// + "<CameraID>0000000000200000000000000161443</CameraID>"
		//				// + "<CameraID>0000000000200000000000000160667</CameraID>"
		//				// + "<CameraID>0000000000200000000000000161442</CameraID>"
		//				// + "<CameraID>0000000000200000000000000162449</CameraID>"
		//				+ "</CameraList>" + "</Message>";

		//
		//String sendMessage1 = "<?xml version=\"1.0\"encoding=\"gb2312\"?><Message><CameraList><CameraID>0000000000200000000000000250397</CameraID><CameraID>0000000000200000000000000250403</CameraID><CameraID>0000000000200000000000000250344</CameraID><CameraID>0000000000200000000000000250384</CameraID><CameraID>0000000000200000000000000250380</CameraID><CameraID>0000000000200000000000000250339</CameraID><CameraID>0000000000200000000000000250342</CameraID><CameraID>0000000000200000000000000250524</CameraID><CameraID>0000000000200000000000000250336</CameraID><CameraID>0000000000200000000000000250412</CameraID><CameraID>0000000000200000000000000250391</CameraID></CameraList></Message>";
		//DVR设备无心跳报警
		String sendMessage = "<Message Version=\"1.0\"><Device Naming=\"0000000000200000000000000390272:25.30.5.38:010001\"></Device></Message>";
		//DVR设备磁盘读写失败/磁盘故障报警
		//String sendMessage = "<Message Version=\"1.0\"><Header SequenceNumber=\"0\" SessionID=\"012345678901234\"/><Label HostType=\"TERMINAL\" IP=\"25.30.5.212\" ItemType=\"FAULT\" ItemTable=\"DEVICE\"/><Body><GatherTime>2013-04-09 16:00:00</GatherTime><DeviceID>0000000000200000000000000400429</DeviceID><DeviceType>2</DeviceType><FaultType>1</FaultType><FaultID>008</FaultID><FaultTime>2013-04-09 16:00:00</FaultTime><IsClear>0</IsClear></Body></Message>";
		TcpModel tcp = null;
		try {
			tcp = client.sendClient(257, 9208, sendMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) throws UnsupportedEncodingException, IOException {

		TCPClient client = null;
		try {
			String msg="<Message><Nmaing>0000000000200000000000002760000:25.30.5.103:010001</Naming>"
			        + "<DeviceID>0000000000200000000000001950001</DeviceID ><Op>0</Op></Message>";
			 client = new TCPClient("25.30.5.103",6005);
			 TcpModel res = client.sendLightSockt(257,5064,"0000000000200000000000001950001",msg);
			 System.out.println(res.getReturnNo());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
