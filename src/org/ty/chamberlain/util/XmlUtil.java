package org.ty.chamberlain.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.ty.chamberlain.model.Camera;

/**
 * ������xml��ʽ���ַ���,��ȡ����Ӧ��Ϣ
 * 
 * @author heyang
 * 
 */
public class XmlUtil {
	/** TCP IP��ַ */
	public static String TCP_IPADREES;

	/** TCP �˿� */
	public static Integer TCP_PORT;

	static private Properties props = new Properties();

	static {

		InputStream propStream = XmlUtil.class
				.getResourceAsStream("global.properties");
		if (propStream == null) {
			throw new ExceptionInInitializerError(
					"Can not find global.properties");
		}
		try {
			props.load(propStream);
		} catch (IOException ex) {
			throw new ExceptionInInitializerError(
					"Load global.properties data error.");
		}

		String tcp_ipadrees = props.getProperty("tcp.ipAdrees");
		if (tcp_ipadrees == null) {
			throw new IllegalArgumentException("tcp.ipAdrees ");
		} else {
			TCP_IPADREES = tcp_ipadrees;
		}

		String tcp_port = props.getProperty("tcp.port");
		System.out.println("TCP�˿�(tcp.port):" + tcp_port);
		if (tcp_port == null) {
			throw new IllegalArgumentException("tcp.port");
		} else {
			TCP_PORT = Integer.parseInt(tcp_port);
		}

	}

	/**
	 * ת��xml����
	 * 
	 * @param xmlStr
	 * @return
	 */
	public static List<Camera> parserXml(String xmlStr) {

		List<Camera> listEyes = null;
		try {

			listEyes = new ArrayList<Camera>();

			if (xmlStr == null || xmlStr.equals("")) {
				System.out.println("����xml����Ϊ��.");
				return listEyes;
			}
			//����һ���ַ���,��Buffer��ȡ��������ʧ�ܵ�ʱ��			
			/*int lastIndex = xmlStr.lastIndexOf("</Message>");
			 System.out.println("��ֵΪ -1 ����Buffer��ȡʧ�ܣ�lastIndex = " + lastIndex);
			 if(lastIndex == -1){
			 int lastIndex2 = xmlStr.lastIndexOf("/>");				
			 String xmlStrLast = xmlStr.substring(0, lastIndex2 + 2);
			 xmlStrLast = xmlStrLast + "\n</CameraList>\n</Message>";
			 //System.out.println("xmlStrLast = " + xmlStrLast);
			 xmlStr = xmlStrLast;
			 }*/

			Document document = DocumentHelper.parseText(xmlStr);

			Element root = document.getRootElement();// ָ����ڵ�
			Element cameraList = root.element("CameraList");
			List camrea = cameraList.elements("Camera");
			System.out.println("camrea = " + camrea.size());
			for (Object object : camrea) {

				if (object instanceof Element) {

					Element element = (Element) object;

					String id = element.attribute("ID").getValue();
					String recordStatus = null;
					try {
						recordStatus = element.attribute("RecordStatus")
								.getValue();
					} catch (Exception e) {
						// TODO: handle exception
					}

					Camera eyes = new Camera();
					eyes.setId(id);
					eyes.setRecordStatus(recordStatus);

					listEyes.add(eyes);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("xml�������� error is " + e.getMessage());
		}

		return listEyes;

	}

	public static void main(String[] args) {

		List<Camera> listEyes = XmlUtil.parserXml("<Message>" + "<CameraList>"
				+ "<Camrea   ID= '1'  RecordStatus='1'  />"
				+ "<Camrea   ID= '2'  RecordStatus='0'  />"
				+ " <Camrea   ID= '3'  RecordStatus='1'  />"
				+ "<Camrea   ID= '4'  RecordStatus='0'  />"
				+ "<Camrea   ID= '5'  RecordStatus='1'  />"
				+ " <Camrea   ID= '6'  RecordStatus='1'  />"
				+ " <Camrea   ID= '7'  RecordStatus='0'  />"
				+ " <Camrea   ID= '8'  RecordStatus='1'  />" + "</CameraList>"
				+ "</Message >");

		for (Camera eyes : listEyes) {
			System.out.print("����ͷId:" + eyes.getId());
			System.out.println("����ͷ״̬:" + eyes.getRecordStatus());
		}
	}

}
