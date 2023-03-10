package org.cap.packet.generation.constants;

public class S3BatchConstants {
	
	private S3BatchConstants() {}

	//	Input map parameters
	public static final String TASK_ID = "taskId";
	public static final String ITEM_SEQUENCE_NUMBER = "itemSeqNum";
	public static final String IMAGE_CODE = "imageCode";
	public static final String IMAGE_CREATED_DATE_TIME = "imageCreatedDateTime";
	public static final String DOCUMENT_TYPE = "docType";
	public static final String ADDITIONAL_DATA_ID = "additionalDataU";
	public static final String REQUEST_ID = "requestId";
	
	//	Document types
	public static final String DOC_TYPE_DIRCV = "DIRCV";
	public static final String DOC_TYPE_CXINSPPKT = "CXINSPPKT";
	public static final String DOC_TYPE_INSTLIST = "INSTLIST";
	public static final String DOC_TYPE_POCTST = "POCTST";
	
	//	Sql queries
	public static final String QUERY_GET_IMAGE_AU_ID = "SELECT addtnl_data_t FROM lpt_addtnl_d_value WHERE addtnl_data_u = ? AND addtnl_data_fld_c='AUID'";
	public static final String QUERY_GET_ADDITIONAL_DATA_FOR_DOC_TYPE_DIRCV = "SELECT a.addtnl_data_t FROM lpt_addtnl_d_value a INNER JOIN lpt_addtnl_d_value b ON b.addtnl_data_u = a.addtnl_data_u WHERE a.addtnl_data_u = ? AND a.addtnl_data_fld_c = 'ABE' AND b.addtnl_data_fld_c = 'ABERELTYPE' AND b.addtnl_data_t= 'I' ORDER BY a.last_update_dt DESC LIMIT 1";
	public static final String QUERY_GET_ADDITIONAL_DATA_FOR_DOC_TYPE_CXINSPPKT = "SELECT a.addtnl_data_t FROM lpt_addtnl_d_value a WHERE a.addtnl_data_u = ? AND a.addtnl_data_fld_c='COMPNBR' ORDER BY a.addtnl_data_u DESC LIMIT 1";
	public static final String QUERY_GET_ADDITIONAL_DATA_FOR_DOC_TYPES_INSTLIST_OR_POCTST = "SELECT a.addtnl_data_t FROM lpt_addtnl_d_value a WHERE a.addtnl_data_u = ? AND a.addtnl_data_fld_c='SUABE' ORDER BY a.addtnl_data_u DESC LIMIT 1";
	
	

}
