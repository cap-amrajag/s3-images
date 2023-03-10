package org.cap.s3.batch;

import java.sql.Timestamp;
import java.time.Instant;
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
		int taskId = 123;
		int itemSeqNo = 2;
		String imageCode = "abc";
		Timestamp timestamp = Timestamp.from(Instant.now());
		String docType;
		int additionalDataId;
		int requestId = 1234;
		additionalDataId = 36768902; docType = "DIRCV";
//		additionalDataId = 36765816; docType = "CXINSPPKT";
		Map<String,Object> jobParameters = new HashMap<>();
		jobParameters.put(S3BatchConstants.TASK_ID, taskId);
		jobParameters.put(S3BatchConstants.ITEM_SEQUENCE_NUMBER, itemSeqNo);
		jobParameters.put(S3BatchConstants.IMAGE_CODE, imageCode);
		jobParameters.put(S3BatchConstants.IMAGE_CREATED_DATE_TIME, timestamp);
		jobParameters.put(S3BatchConstants.DOCUMENT_TYPE, docType);
		jobParameters.put(S3BatchConstants.ADDITIONAL_DATA_ID, additionalDataId);
		jobParameters.put(S3BatchConstants.REQUEST_ID, requestId);
		
		try {
			S3BatchService s3Batch = new S3BatchService(jobParameters);
			s3Batch.processJob();
		}catch(Exception e) {
			logger.error("Fatal error occured in S3BatchApplication. Details: {}",e.toString());
			System.exit(0);
		}
	}

}
