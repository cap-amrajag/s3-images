package org.cap.s3.batch.service;

import java.util.Map;

import org.cap.s3.batch.exception.S3BatchException;
import org.cap.s3.batch.repository.S3BatchRepository;
import org.cap.s3.batch.utils.S3BatchUtils;
import org.cap.s3.batch.utils.SsmParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3BatchService {
	
	private Logger logger = LoggerFactory.getLogger(S3BatchService.class);
	
	private final SsmParameters ssmParameters;
	
	private final Map<String, String> jobParameters ;
	
	private final S3BatchRepository s3BatchRepository;

	public S3BatchService(Map<String, String> jobParameters) throws Exception {
		this.ssmParameters = S3BatchUtils.getSsmParameters();
		this.jobParameters = jobParameters;
		validateJobParameters();
		s3BatchRepository = new S3BatchRepository(ssmParameters);		
	}

	public void processJob() {
		try {
			jobParameters.forEach((key,value)->logger.info("JobParameter key-{}, value-{}",key,value));
		}catch(Exception e) {
			logger.error("Error occured in S3BatchService::processJob() method. Details: {}",e.toString());
		}
		
	}

	private void validateJobParameters() throws S3BatchException{
		String errorMsg = "";
		if(jobParameters!=null && jobParameters.size()>1) {
			for (Map.Entry<String, String> entry : jobParameters.entrySet()) {
				String value = entry.getValue();
				String key = entry.getKey();
				if(value!=null && !value.trim().isEmpty()) {
					errorMsg = errorMsg.concat("");
				}else {
					errorMsg = errorMsg.concat(String.format(" JobParameter %s is null or empty.",key));
				}
			}
			if(!errorMsg.isEmpty()) {
				throw new S3BatchException("Input job parameters map validation failed. Details: ".concat(errorMsg));
			}
		}else {
			throw new S3BatchException("Input job parameters map is either null or incomplete");
		}
	}

}
