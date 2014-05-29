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
 * NIO TCP �ͻ���
 * 
 * @date 2010-2-3
 * @time ����03:33:26
 * @version 1.00
 */
public class TCPClient {
	// �ŵ�ѡ����
	private Selector selector;

	// �������ͨ�ŵ��ŵ�
	SocketChannel socketChannel;

	// Ҫ���ӵķ�����Ip��ַ
	private String hostIp;

	// Ҫ���ӵ�Զ�̷������ڼ����Ķ˿�
	private int port;

	/**
	 * ���캯��
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
	 * ��ʼ��
	 * 
	 * @throws IOException
	 */
	private void initialize() throws IOException {
		// �򿪼����ŵ�������Ϊ������ģʽ
		socketChannel = SocketChannel.open();
	//	socketChannel.connect(new InetSocketAddress(hostIp, port)); //�˷��������������ӳ�ʱʱ�䣬��ʱ�����������
		socketChannel.socket().connect(new InetSocketAddress(hostIp, port), 3000); //������������3000���볬ʱ����ʱȡ����������ӷ���

		socketChannel.configureBlocking(false);

		// �򿪲�ע��ѡ�������ŵ�
		selector = Selector.open();
		socketChannel.register(selector, SelectionKey.OP_READ);

		// ��ȡ����
		// new TCPClientReadThread(selector);

	}

	/**
	 * ��ȡ��������Ϣ
	 * 
	 * @param selector
	 */
	private TcpModel readMsg() {

		TcpModel model = null;

		try {
			if (selector.select() > 0) {
				// ����ÿ���п���IO����Channel��Ӧ��SelectionKey
				for (SelectionKey sk : selector.selectedKeys()) {

					// �����SelectionKey��Ӧ��Channel���пɶ�������
					if (sk.isReadable()) {

						model = new TcpModel();

						// ʹ��NIO��ȡChannel�е�����
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
							System.out.println("���յ����Է�����"
									+ sc.socket().getRemoteSocketAddress()
									+ "����Ϣ:" + "");
							System.out.println("�汾��:" + model.getVersion()
									+ "	  XML����:" + xmlLength + "	ָ����:"
									+ model.getDictateNo() + "	ָ�����:"
									+ model.getInstructionNo() + "      ������:"
									+ model.getReturnNo());

							System.out.println("xml����Ϊ:\n"
									+ model.getXmlResult());
							*/
							sk.interestOps(SelectionKey.OP_READ);
							sc.close();

							break;
						}

					}

					// ɾ�����ڴ����SelectionKey
					selector.selectedKeys().remove(sk);
				}
			}
		} catch (IOException ex) {
			System.out.println("��ȡ����.");
			ex.printStackTrace();
		} finally {
			if (selector.isOpen()) {
				try {
					selector.close();
				} catch (IOException e) {
					System.out.println("�رճ���!");
					e.printStackTrace();
				}
			}
		}

		return model;
	}

	/**
	 * �����ַ�����������
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

		return readMsg();// ��ȡ����
	}

	/**
	 * ����TCP����
	 * 
	 * @param version
	 *            �汾��
	 * @param dictateNo
	 *            ���
	 * @param sendMessage
	 *            ����xml����
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
	 * @param version �汾�� 257
	 * @param dictateNo ָ���� 5064
	 * @param dvrId Ŀ��ID(dvr)
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
	 * ��������ͷID��״̬
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
		//DVR�豸����������
		String sendMessage = "<Message Version=\"1.0\"><Device Naming=\"0000000000200000000000000390272:25.30.5.38:010001\"></Device></Message>";
		//DVR�豸���̶�дʧ��/���̹��ϱ���
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
