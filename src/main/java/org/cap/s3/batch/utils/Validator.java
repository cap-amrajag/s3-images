package org.cap.s3.batch.utils;

import java.util.Map;

import org.cap.s3.batch.constants.S3BatchConstants;

public class Validator {
	
	private Validator() {}

	public static String validateJobParameters(Map<String, Object> jobParameters) {
		StringBuilder errorMsg = new StringBuilder();
		errorMsg.append( validateIntParameter(S3BatchConstants.TASK_ID, jobParameters.get(S3BatchConstants.TASK_ID)));
		errorMsg.append( validateIntParameter(S3BatchConstants.ITEM_SEQUENCE_NUMBER, jobParameters.get(S3BatchConstants.ITEM_SEQUENCE_NUMBER)));
		errorMsg.append( validateStringParameter(S3BatchConstants.IMAGE_CODE, jobParameters.get(S3BatchConstants.IMAGE_CODE)));
		errorMsg.append( validateTimestampParameter(S3BatchConstants.IMAGE_CREATED_DATE_TIME, jobParameters.get(S3BatchConstants.IMAGE_CREATED_DATE_TIME)));
		errorMsg.append( validateStringParameter(S3BatchConstants.DOCUMENT_TYPE, jobParameters.get(S3BatchConstants.DOCUMENT_TYPE)));
		errorMsg.append( validateIntParameter(S3BatchConstants.ADDITIONAL_DATA_ID, jobParameters.get(S3BatchConstants.ADDITIONAL_DATA_ID)));
		errorMsg.append( validateIntParameter(S3BatchConstants.REQUEST_ID, jobParameters.get(S3BatchConstants.REQUEST_ID)));
		return errorMsg.toString();
	}

	private static String validateStringParameter(String parameter, Object value) {
		if(value!=null && !((String) value).trim().isEmpty()) {
			return "";
		}else {
			return String.format(" Job-Parameter %s is null or empty.",parameter);
		}
	}
	
	private static String validateIntParameter(String parameter, Object value) {
		if(value!=null && (Integer)value>0) {
			return "";
		}else {
			return String.format(" JobParameter %s is incorrect.",parameter);
		}
	}
	
	private static String validateTimestampParameter(String parameter, Object value) {
		if(value!=null) {
			return "";
		}else {
			return String.format(" JobParameter %s is null or incorrect.",parameter);
		}
	}

}
