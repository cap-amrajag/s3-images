package org.cap.packet.generation.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.cap.packet.generation.constants.S3BatchConstants;
import org.cap.packet.generation.constants.S3BatchConstants.QueryType;
import org.cap.packet.generation.exception.S3BatchException;
import org.cap.packet.generation.model.AdditionalData;
import org.cap.packet.generation.model.QueryFields;
import org.cap.packet.generation.model.SearchQuery;
import org.cap.packet.generation.model.SearchQueryBuilder;
import org.cap.packet.generation.repository.S3BatchRepository;
import org.cap.packet.generation.utils.S3BatchUtils;
import org.cap.packet.generation.utils.SsmParameters;
import org.cap.packet.generation.utils.Validator;
import org.json.JSONArray;
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
			final long additionalDataId = (Integer)jobParameters.get(S3BatchConstants.ADDITIONAL_DATA_ID);
			final String docType = (String)jobParameters.get(S3BatchConstants.DOCUMENT_TYPE);
			final String connectionUrl;
			//TODO: create SSM parameter for connection Url
//			connectionUrl = (String)jobParameters.get(SSMParameterConstants.CCS_CONNECTION_URL);
			connectionUrl = "https://u71il86if9.execute-api.us-east-1.amazonaws.com/uat/files/search";
			
			final long auId = getImageAuId(additionalDataId);
			logger.info("AuId fetched from db: {}",auId);
			
			final AdditionalData additionalData = getRequiredAdditionalData(additionalDataId, docType, auId);
			JSONObject json = new JSONObject(additionalData);
			logger.info("Additional data: {}",json);
			
			final String applicationName ;	//	applicationName = getApplicationName(additionalData);
			applicationName = "LAPDocsIdx1";
//			applicationName = "LAPPTESIdx1";
//			applicationName = "LAPCompsIdx1";
//			applicationName = "ScoresTRFIdx1";
			
			final String applicationContext = getApplicationContextFromApplicationName(applicationName);
			logger.info("Application Context: {}",applicationContext);
			
			final String ccsSearchUrl = getCCSSearchUrl(connectionUrl.trim(), applicationContext);
			logger.info("CCS Search Url: {}",ccsSearchUrl);
			
			final JSONObject searchRequestJsonBody = prepareRequestJsonBodyForSearch(additionalData, applicationContext);
			logger.info("Search Request Body: \n{}",searchRequestJsonBody.toString(1));
			
			
		}catch(Exception e) {
			logger.error("Error occured in S3BatchService::processJob() method. Details: {}",e.toString());
			throw new S3BatchException("Error occured in S3BatchService::processJob() method. Details: ".concat(e.toString()));
		}
		
	}

	private JSONObject prepareRequestJsonBodyForSearch(AdditionalData additionalData, String applicationContext) {
		SearchQuery compoundQuery = SearchQueryBuilder.ofQueryType(QueryType.COMPOUND).build();
		JSONObject requestJsonBody = new JSONObject(compoundQuery);		
		JSONArray subQueries = requestJsonBody.getJSONArray(S3BatchConstants.SUB_QUERIES);		
		JSONObject subQuery =  null;
		JSONArray queryFieldsArray = null;
		JSONObject queryField = null;
		
		//	Common sub query Au-Id
		subQuery = new JSONObject(SearchQueryBuilder.ofQueryType(QueryType.SIMPLE).build());
		queryFieldsArray = subQuery.getJSONArray(S3BatchConstants.QUERY_FIELDS);
		queryField = new JSONObject();
		queryField.put(S3BatchConstants.FIELD_NAME,String.format(S3BatchConstants.QUERY_FIELD_AUID, applicationContext));
		queryField.put(S3BatchConstants.FIELD_VALUE, additionalData.getAuId());
		queryFieldsArray.put(queryField);
		subQueries.put(subQuery);
		
		switch(additionalData.getDocType()) {
		case S3BatchConstants.DOC_TYPE_DIRCV:
			//	Sub query for DOC_TYPE_DIRCV
			subQuery =  new JSONObject(SearchQueryBuilder.ofQueryType(QueryType.SIMPLE).build());
			queryFieldsArray = subQuery.getJSONArray(S3BatchConstants.QUERY_FIELDS);
			
			//	Person-Id
			queryField = new JSONObject();
			queryField.put(S3BatchConstants.FIELD_NAME,String.format(S3BatchConstants.QUERY_FIELD_PERSON_ID, applicationContext));
			queryField.put(S3BatchConstants.FIELD_VALUE, Collections.singletonList(additionalData.getPersonid()));
			queryFieldsArray.put(queryField);
						
			//	Doc Type
			queryField = new JSONObject();
			queryField.put(S3BatchConstants.FIELD_NAME,String.format(S3BatchConstants.QUERY_FIELD_DOC_TYPE, applicationContext));
			queryField.put(S3BatchConstants.FIELD_VALUE, additionalData.getDocType());
			queryFieldsArray.put(queryField);
			
			//	Append Sub query
			subQueries.put(subQuery);
			break;
		case S3BatchConstants.DOC_TYPE_CXINSPPKT:
			//	Sub query for .DOC_TYPE_CXINSPPKT
			subQuery =  new JSONObject(SearchQueryBuilder.ofQueryType(QueryType.SIMPLE).build());
			queryFieldsArray = subQuery.getJSONArray(S3BatchConstants.QUERY_FIELDS);
			
			//	Comp-number
			queryField = new JSONObject();
			queryField.put(S3BatchConstants.FIELD_NAME,String.format(S3BatchConstants.QUERY_FIELD_COMPNBR, applicationContext));
			queryField.put(S3BatchConstants.FIELD_VALUE, Collections.singletonList(additionalData.getCompnbr()));
			queryFieldsArray.put(queryField);
			
			//	Doc Type
			queryField = new JSONObject();
			queryField.put(S3BatchConstants.FIELD_NAME,String.format(S3BatchConstants.QUERY_FIELD_DOC_TYPE, applicationContext));
			queryField.put(S3BatchConstants.FIELD_VALUE, additionalData.getDocType());
			queryFieldsArray.put(queryField);
			
			//	Append Sub query
			subQueries.put(subQuery);
			break;
		case S3BatchConstants.DOC_TYPE_INSTLIST:
		case S3BatchConstants.DOC_TYPE_POCTST:
			//	Sub query for .DOC_TYPE_POCTST & DOC_TYPE_INSTLIST
			subQuery =  new JSONObject(SearchQueryBuilder.ofQueryType(QueryType.SIMPLE).build());
			queryFieldsArray = subQuery.getJSONArray(S3BatchConstants.QUERY_FIELDS);
			
			//	Su-Id
			queryField = new JSONObject();
			queryField.put(S3BatchConstants.FIELD_NAME,String.format(S3BatchConstants.QUERY_FIELD_SUID, applicationContext));
			queryField.put(S3BatchConstants.FIELD_VALUE, Collections.singletonList(additionalData.getSuId()));
			queryFieldsArray.put(queryField);
			
			//	Doc Type
			queryField = new JSONObject();
			queryField.put(S3BatchConstants.FIELD_NAME,String.format(S3BatchConstants.QUERY_FIELD_DOC_TYPE, applicationContext));
			queryField.put(S3BatchConstants.FIELD_VALUE, additionalData.getDocType());
			queryFieldsArray.put(queryField);
			
			//	Append Sub query
			subQueries.put(subQuery);
			break;
		default:
			//	Sub query for all other doctypes
			subQuery =  new JSONObject(SearchQueryBuilder.ofQueryType(QueryType.SIMPLE).build());
			queryFieldsArray = subQuery.getJSONArray(S3BatchConstants.QUERY_FIELDS);
			
			//	Person-Id
			queryField = new JSONObject();
			queryField.put(S3BatchConstants.FIELD_NAME, String.format(S3BatchConstants.QUERY_FIELD_PERSON_ID, applicationContext));
			queryField.put(S3BatchConstants.FIELD_VALUE, "");
			queryFieldsArray.put(queryField);
			
			//	Su-Id
			queryField = new JSONObject();
			queryField.put(S3BatchConstants.FIELD_NAME, String.format(S3BatchConstants.QUERY_FIELD_SUID, applicationContext));
			queryField.put(S3BatchConstants.FIELD_VALUE, "");
			queryFieldsArray.put(queryField);
			
			//	Doc Type
			queryField = new JSONObject();
			queryField.put(S3BatchConstants.FIELD_NAME, String.format(S3BatchConstants.QUERY_FIELD_DOC_TYPE, applicationContext));
			queryField.put(S3BatchConstants.FIELD_VALUE, additionalData.getDocType());
			queryFieldsArray.put(queryField);
			
			//	Append Sub query			
			subQueries.put(subQuery);
			break;
		}
		return requestJsonBody;
	}

	private String getCCSSearchUrl(String connectionUrl ,String applicationContext) {
		String limit = S3BatchConstants.SEARCH_LIMIT;
		String sortByScanDate = String.format(S3BatchConstants.SORT_BY_SCAN_DATE, applicationContext);
		connectionUrl = connectionUrl.concat("?limit").concat(limit);
		connectionUrl = connectionUrl.concat("&sort").concat(sortByScanDate);
		return connectionUrl;
	}

	private String getApplicationContextFromApplicationName(String applicationName) throws S3BatchException {
		switch(applicationName) {
		case S3BatchConstants.APPLICATION_NAME_GENERAL:
			return S3BatchConstants.APPLICATION_CONTEXT_GENERAL;
		case S3BatchConstants.APPLICATION_NAME_PTCN:
			return S3BatchConstants.APPLICATION_CONTEXT_PTCN;
		case S3BatchConstants.APPLICATION_NAME_COMPLAINTS:
			return S3BatchConstants.APPLICATION_CONTEXT_COMPLAINTS;
		case S3BatchConstants.APPLICATION_NAME_RESULTS_FORMS:
			return S3BatchConstants.APPLICATION_CONTEXT_RESULTS_FORMS;
		default:
			throw new S3BatchException("No applicationContext mapping found for given applicationName: ".concat(applicationName));
		}
	}

	private String getApplicationName(AdditionalData additionalData) {
		// TODO Auto-generated method stub
		return null;
	}

	private AdditionalData getRequiredAdditionalData(final long additionalDataId, final String docType, final long auId) throws S3BatchException {
		AdditionalData additionalData = new AdditionalData(String.valueOf(auId), docType);
		switch(docType) {
		case S3BatchConstants.DOC_TYPE_DIRCV:
			getAdditionalDataForDocTypeDIRCV(additionalData, additionalDataId);
			break;
		case S3BatchConstants.DOC_TYPE_CXINSPPKT:
			getAdditionalDataForDocTypeCXINSPPKT(additionalData, additionalDataId);
			break;
		case S3BatchConstants.DOC_TYPE_INSTLIST:
		case S3BatchConstants.DOC_TYPE_POCTST:
			getAdditionalDataForDocTypesINSTLISTOrPOCTST(additionalData, additionalDataId);
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
