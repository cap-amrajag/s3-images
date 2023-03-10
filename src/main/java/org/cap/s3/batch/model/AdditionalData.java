package org.cap.s3.batch.model;

public class AdditionalData {

	private int auId;
	private int suId;
	private int compnbr;
	private int personid;
	private String docType;
	
	public AdditionalData(int auId) {
		this.auId = auId;
	}
	
	public int getAuId() {
		return auId;
	}
	public void setAuId(int auId) {
		this.auId = auId;
	}
	public int getSuId() {
		return suId;
	}
	public void setSuId(int suId) {
		this.suId = suId;
	}
	public int getCompnbr() {
		return compnbr;
	}
	public void setCompnbr(int compnbr) {
		this.compnbr = compnbr;
	}
	public int getPersonid() {
		return personid;
	}
	public void setPersonid(int personid) {
		this.personid = personid;
	}
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	
	
}
