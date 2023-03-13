package org.cap.packet.generation.model;

public class AdditionalData {
	//Ids
	private String auId;
	private String suId;
	private String compnbr;
	private String personid;	//ABE_Id
	
	private String applicationName;
	private String className;
	private String viewerType;
	private String docType;
	
	public AdditionalData(String auId, String docType) {
		this.auId = auId;
		this.docType = docType;
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

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getViewerType() {
		return viewerType;
	}

	public void setViewerType(String viewerType) {
		this.viewerType = viewerType;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}	
	
	
}
