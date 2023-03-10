package org.cap.s3.batch.constants;

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
	
	//	Sql queries
	public static final String QUERY_GET_IMAGE_AU_ID = "SELECT addtnl_data_t FROM lpt_addtnl_d_value WHERE addtnl_data_u = ? AND addtnl_data_fld_c='AUID'";
	
	//	Document types
	public static final String DOC_TYPE_DIRCV = "DIRCV";

}
