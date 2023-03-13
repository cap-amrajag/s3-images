package org.cap.s3.batch.service;

import java.util.Collections;
import java.util.Map;

import org.cap.s3.batch.constants.S3BatchConstants;
import org.cap.s3.batch.exception.S3BatchException;
import org.cap.s3.batch.model.AdditionalData;
import org.cap.s3.batch.repository.S3BatchRepository;
import org.cap.s3.batch.utils.S3BatchUtils;
import org.cap.s3.batch.utils.SsmParameters;
import org.cap.s3.batch.utils.Validator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3BatchService {
	
	private Logger logger = LoggerFactory.getLogger(S3BatchService.class);
	
	private final SsmParameters ssmParameters;
	
	private final Map<String, Object> jobParameters ;
	
	private final S3BatchRepository s3BatchRepository;

	public S3BatchService(Map<String, Object> jobParameters) throws Exception {
		this.ssmParameters = S3BatchUtils.getSsmParameters();
		this.jobParameters = Collections.unmodifiableMap(jobParameters);
		validateJobParameters();
		s3BatchRepository = new S3BatchRepository(ssmParameters);		
	}

	public void processJob() throws S3BatchException {
		try {
//			jobParameters.forEach((key,value)->logger.info("JobParameter key-{}, value-{}",key,value));
			
			final long auId = getImageAuId((Integer)jobParameters.get(S3BatchConstants.ADDITIONAL_DATA_ID));
			logger.info("AuId fetched from db: {}",auId);
			
			final AdditionalData additionalData = getRequiredAdditionalData(auId);
			JSONObject json = new JSONObject(additionalData);
			logger.info("Additional data: {}",json);
			
		}catch(Exception e) {
			logger.error("Error occured in S3BatchService::processJob() method. Details: {}",e.toString());
			throw new S3BatchException("Error occured in S3BatchService::processJob() method. Details: ".concat(e.toString()));
		}
		
	}

	private AdditionalData getRequiredAdditionalData(final long auId) throws S3BatchException {
		AdditionalData additionalData = new AdditionalData(auId);
		switch((String)jobParameters.get(S3BatchConstants.DOCUMENT_TYPE)) {
		case S3BatchConstants.DOC_TYPE_DIRCV:
			getAdditionalDataForDocTypeDIRCV(additionalData, (Integer)jobParameters.get(S3BatchConstants.ADDITIONAL_DATA_ID));
			break;
		case S3BatchConstants.DOC_TYPE_CXINSPPKT:
			getAdditionalDataForDocTypeCXINSPPKT(additionalData, (Integer)jobParameters.get(S3BatchConstants.ADDITIONAL_DATA_ID));
			break;
		case S3BatchConstants.DOC_TYPE_INSTLIST:
		case S3BatchConstants.DOC_TYPE_POCTST:
			getAdditionalDataForDocTypesINSTLISTOrPOCTST(additionalData, (Integer)jobParameters.get(S3BatchConstants.ADDITIONAL_DATA_ID));
			break;
		default:
		}
		return additionalData;
	}

	private void getAdditionalDataForDocTypesINSTLISTOrPOCTST(AdditionalData additionalData, long additionalDataId) throws S3BatchException {
		try {
			s3BatchRepository.getAdditionalDataForDocTypesINSTLISTOrPOCTST(additionalData, additionalDataId);
		}catch (Exception e) {
			throw new S3BatchException("Error in S3BatchService::getAdditionalDataForDocTypesINSTLISTOrPOCTST() method. Details: ".concat(e.toString()));
		}
	}

	private void getAdditionalDataForDocTypeCXINSPPKT(AdditionalData additionalData, long additionalDataId) throws S3BatchException {
		try {
			s3BatchRepository.getAdditionalDataForDocTypeCXINSPPKT(additionalData, additionalDataId);
		}catch (Exception e) {
			throw new S3BatchException("Error in S3BatchService::getAdditionalDataForDocTypeCXINSPPKT() method. Details: ".concat(e.toString()));
		}
	}

	private void getAdditionalDataForDocTypeDIRCV(AdditionalData additionalData, long additionalDataId) throws S3BatchException {
		try {
			s3BatchRepository.getAdditionalDataForDocTypeDIRCV(additionalData, additionalDataId);
		}catch (Exception e) {
			throw new S3BatchException("Error in S3BatchService::getAdditionalDataForDocTypeDIRCV() method. Details: ".concat(e.toString()));
		}
	}

	private long getImageAuId(long additionalDataId) throws S3BatchException {
		long auId = -1;
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
