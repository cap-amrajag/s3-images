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
//			applicationName = "LAPDocsIdx1";
			applicationName = "LAPPTESIdx1";
//			applicationName = "LAPCompsIdx1";
//			applicationName = "ScoresTRFIdx1";
			
			final String applicationContext = getApplicationContextFromApplicationName(applicationName);
			logger.info("Application Context: {}",applicationContext);
			
			final String ccsSearchUrl = getCCSSearchUrl(connectionUrl.trim(), applicationContext);
			logger.info("CCS Search Url: {}",ccsSearchUrl);
			
			final String searchRequestBody = prepareRequestBodyForSearch(additionalData, applicationContext);
			logger.info("Search Request Body: \n{}",new JSONObject(searchRequestBody).toString(1));
			
			
		}catch(Exception e) {
			logger.error("Error occured in S3BatchService::processJob() method. Details: {}",e.toString());
			throw new S3BatchException("Error occured in S3BatchService::processJob() method. Details: ".concat(e.toString()));
		}
		
	}

	private String prepareRequestBodyForSearch(AdditionalData additionalData, String applicationContext) {
		SearchQuery compoundQuery = SearchQueryBuilder.ofQueryType(QueryType.COMPOUND).build();
		JSONObject jsonBody = new JSONObject(compoundQuery);
		
		JSONArray subQueries = jsonBody.getJSONArray(S3BatchConstants.SUB_QUERIES);
		
		SearchQuery subQuery = SearchQueryBuilder.ofQueryType(QueryType.SIMPLE).build();
		//	Au-Id
		QueryFields queryField = new QueryFields(String.format(S3BatchConstants.QUERY_FIELD_AUID, applicationContext), additionalData.getAuId());
		List<QueryFields> queryFields = Collections.singletonList(queryField);
		subQuery.setQueryFields(queryFields);
		JSONObject auIdJson = new JSONObject(subQuery);
		subQueries.put(auIdJson);
		JSONObject additionalJson = null;
		
		switch(additionalData.getDocType()) {
		case S3BatchConstants.DOC_TYPE_DIRCV:
			subQuery = SearchQueryBuilder.ofQueryType(QueryType.SIMPLE).build();
			queryFields = new ArrayList<>();
			
			//	Person-Id
			queryField = new QueryFields(String.format(S3BatchConstants.QUERY_FIELD_PERSON_ID, applicationContext), Collections.singletonList(additionalData.getPersonid()));
			queryFields.add(queryField);
			
			//	Doc Type
			queryField = new QueryFields(String.format(S3BatchConstants.QUERY_FIELD_DOC_TYPE, applicationContext), additionalData.getDocType());
			queryFields.add(queryField);
			
			//Final Query Fields list
			subQuery.setQueryFields(queryFields);
			additionalJson = new JSONObject(subQuery);
			subQueries.put(additionalJson);
			break;
		case S3BatchConstants.DOC_TYPE_CXINSPPKT:
			subQuery = SearchQueryBuilder.ofQueryType(QueryType.SIMPLE).build();
			queryFields = new ArrayList<>();
			
			//	Comp-number
			queryField = new QueryFields(String.format(S3BatchConstants.QUERY_FIELD_COMPNBR, applicationContext), Collections.singletonList(additionalData.getCompnbr()));
			queryFields.add(queryField);
			
			//	Doc Type
			queryField = new QueryFields(String.format(S3BatchConstants.QUERY_FIELD_DOC_TYPE, applicationContext), additionalData.getDocType());
			queryFields.add(queryField);
			
			//Final Query Fields list
			subQuery.setQueryFields(queryFields);
			additionalJson = new JSONObject(subQuery);
			subQueries.put(additionalJson);
			break;
		case S3BatchConstants.DOC_TYPE_INSTLIST:
		case S3BatchConstants.DOC_TYPE_POCTST:
			subQuery = SearchQueryBuilder.ofQueryType(QueryType.SIMPLE).build();
			queryFields = new ArrayList<>();
			
			//	Su-Id
			queryField = new QueryFields(String.format(S3BatchConstants.QUERY_FIELD_SUID, applicationContext), Collections.singletonList(additionalData.getSuId()));
			queryFields.add(queryField);
			
			//	Doc Type
			queryField = new QueryFields(String.format(S3BatchConstants.QUERY_FIELD_DOC_TYPE, applicationContext), additionalData.getDocType());
			queryFields.add(queryField);
			
			//Final Query Fields list
			subQuery.setQueryFields(queryFields);
			additionalJson = new JSONObject(subQuery);
			subQueries.put(additionalJson);
			break;
		default:
			subQuery = SearchQueryBuilder.ofQueryType(QueryType.SIMPLE).build();
			additionalJson = new JSONObject(subQuery);
			JSONArray defaultFieldsArray = additionalJson.getJSONArray(S3BatchConstants.QUERY_FIELDS);
			
			//	Person-Id
			JSONObject defaultFields = new JSONObject();
			defaultFields.put(S3BatchConstants.FIELD_NAME, String.format(S3BatchConstants.QUERY_FIELD_PERSON_ID, applicationContext));
			defaultFields.put(S3BatchConstants.FIELD_VALUE, "");
			defaultFieldsArray.put(defaultFields);
			
			//	Su-Id
			defaultFields = new JSONObject();
			defaultFields.put(S3BatchConstants.FIELD_NAME, String.format(S3BatchConstants.QUERY_FIELD_SUID, applicationContext));
			defaultFields.put(S3BatchConstants.FIELD_VALUE, "");
			defaultFieldsArray.put(defaultFields);
			
			//	Doc Type
			defaultFields = new JSONObject();
			defaultFields.put(S3BatchConstants.FIELD_NAME, String.format(S3BatchConstants.QUERY_FIELD_DOC_TYPE, applicationContext));
			defaultFields.put(S3BatchConstants.FIELD_VALUE, additionalData.getDocType());
			defaultFieldsArray.put(defaultFields);
			
			//Final Default Query Fields list
			
			subQueries.put(additionalJson);
			break;
		}
		return jsonBody.toString();
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
