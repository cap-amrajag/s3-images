package org.cap.s3.batch;

import java.util.HashMap;
import java.util.Map;

import org.cap.s3.batch.constants.S3BatchConstants;
import org.cap.s3.batch.service.S3BatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3BatchApplication {
	
	private static Logger logger = LoggerFactory.getLogger(S3BatchApplication.class);
	
	public static void main(String[] args) {
		logger.info("Starting S3BatchApplication .");
		startS3Batch();
	}

	private static void startS3Batch() {
		Map<String,String> jobParameters = new HashMap<>();
		jobParameters.put(S3BatchConstants.TASK_ID, null);
		jobParameters.put(S3BatchConstants.ITEM_SEQUENCE_NUMBER, null);
		jobParameters.put(S3BatchConstants.IMAGE_CODE, null);
		jobParameters.put(S3BatchConstants.IMAGE_CREATED_DATE_TIME, null);
		jobParameters.put(S3BatchConstants.DOCUMENT_TYPE, null);
		jobParameters.put(S3BatchConstants.ADDITIONAL_DATA_ID, null);
		jobParameters.put(S3BatchConstants.REQUEST_ID, null);
		
		try {
			S3BatchService s3Batch = new S3BatchService(jobParameters);
			s3Batch.processJob();
		}catch(Exception e) {
			logger.error("Fatal error occured in S3BatchApplication. Details: {}",e.toString());
		}
	}

}
