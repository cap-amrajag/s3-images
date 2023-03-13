package org.cap.packet.generation.model;

public class AdditionalData {

	private String auId;
	private String suId;
	private String compnbr;
	private String personid;	//ABE_Id
	
	public AdditionalData(String auId) {
		this.auId = auId;
	}
	
	public String getAuId() {
		return auId;
	}
	public void setAuId(String auId) {
		this.auId = auId;
	}
	public String getSuId() {
		return suId;
	}
	public void setSuId(String suId) {
		this.suId = suId;
	}
	public String getCompnbr() {
		return compnbr;
	}
	public void setCompnbr(String compnbr) {
		this.compnbr = compnbr;
	}
	public String getPersonid() {
		return personid;
	}
	public void setPersonid(String personid) {
		this.personid = personid;
	}	
	
}
