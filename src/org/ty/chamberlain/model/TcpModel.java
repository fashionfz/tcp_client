package org.ty.chamberlain.model;

public class TcpModel {

	private int version;

	private int xmlLength;

	private int dictateNo;

	private int instructionNo;

	private int returnNo;

	private String xmlResult;

	public int getDictateNo() {
		return dictateNo;
	}

	public void setDictateNo(int dictateNo) {
		this.dictateNo = dictateNo;
	}

	public int getInstructionNo() {
		return instructionNo;
	}

	public void setInstructionNo(int instructionNo) {
		this.instructionNo = instructionNo;
	}

	public int getReturnNo() {
		return returnNo;
	}

	public void setReturnNo(int returnNo) {
		this.returnNo = returnNo;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getXmlLength() {
		return xmlLength;
	}

	public void setXmlLength(int xmlLength) {
		this.xmlLength = xmlLength;
	}

	public String getXmlResult() {
		return xmlResult;
	}

	public void setXmlResult(String xmlResult) {
		this.xmlResult = xmlResult;
	}
}
