package org.cap.s3.batch.model;

public class AdditionalData {

	private long auId;
	private long suId;
	private long compnbr;
	private long personid;	//ABE_Id
	
	public AdditionalData(long auId) {
		this.auId = auId;
	}
	
	public long getAuId() {
		return auId;
	}
	public void setAuId(long auId) {
		this.auId = auId;
	}
	public long getSuId() {
		return suId;
	}
	public void setSuId(long suId) {
		this.suId = suId;
	}
	public long getCompnbr() {
		return compnbr;
	}
	public void setCompnbr(long compnbr) {
		this.compnbr = compnbr;
	}
	public long getPersonid() {
		return personid;
	}
	public void setPersonid(long personid) {
		this.personid = personid;
	}	
	
}
