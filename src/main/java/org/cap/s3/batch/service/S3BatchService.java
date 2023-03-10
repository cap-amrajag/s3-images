package org.cap.s3.batch.service;

import java.util.Map;

import org.cap.s3.batch.constants.S3BatchConstants;
import org.cap.s3.batch.exception.S3BatchException;
import org.cap.s3.batch.repository.S3BatchRepository;
import org.cap.s3.batch.utils.S3BatchUtils;
import org.cap.s3.batch.utils.SsmParameters;
import org.cap.s3.batch.utils.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3BatchService {
	
	private Logger logger = LoggerFactory.getLogger(S3BatchService.class);
	
	private final SsmParameters ssmParameters;
	
	private final Map<String, Object> jobParameters ;
	
	private final S3BatchRepository s3BatchRepository;

	public S3BatchService(Map<String, Object> jobParameters) throws Exception {
		this.ssmParameters = S3BatchUtils.getSsmParameters();
		this.jobParameters = jobParameters;
		validateJobParameters();
		s3BatchRepository = new S3BatchRepository(ssmParameters);		
	}

	public void processJob() throws S3BatchException {
		try {
//			jobParameters.forEach((key,value)->logger.info("JobParameter key-{}, value-{}",key,value));
			final int auId = getImageAuId((Integer)jobParameters.get(S3BatchConstants.ADDITIONAL_DATA_ID));
			logger.info("AuId fetched from db: {}",auId);
		}catch(Exception e) {
			logger.error("Error occured in S3BatchService::processJob() method. Details: {}",e.toString());
			throw new S3BatchException("Error occured in S3BatchService::processJob() method. Details: ".concat(e.toString()));
		}
		
	}

	private int getImageAuId(int additionalDataId) throws S3BatchException {
		int auId = -1;
		try {
			auId = s3BatchRepository.getImageAuId(additionalDataId);
			if(auId<=0)
				throw new S3BatchException("AuId is incorrect "+auId);
			return auId;
		} catch (Exception e) {
			throw new S3BatchException("Error in S3BatchService::getImageAuId() method. Details: ".concat(e.toString()));
		}
	}

	private void validateJobParameters() throws S3BatchException{
		
		if(jobParameters!=null && jobParameters.size()>1) {
			String errorMsg = Validator.validateJobParameters(jobParameters);
			if(!errorMsg.isEmpty()) {
				throw new S3BatchException("Input job parameters map validation failed. Details: ".concat(errorMsg));
			}
		}else {
			throw new S3BatchException("Input job parameters map is either null or incomplete");
		}
	}

}
