package org.cap.packet.generation.utils;

import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;

public class S3BatchUtils {

	private static Logger logger = LoggerFactory.getLogger(S3BatchUtils.class);
	
	private static final Properties PROPERTIES = new Properties();

	private static boolean canReadFromProperties = false;
	

	static {
		
		try {
			PROPERTIES.load(S3BatchUtils.class.getResourceAsStream("/application.properties"));
			if(PROPERTIES.getProperty("canReadFromProperties")!=null) {
				canReadFromProperties = PROPERTIES.getProperty("canReadFromProperties").equalsIgnoreCase("yes");
			}
		} catch (Exception e) {
			logger.error("Error while initializing S3Batch. Details: {}",e.toString());
		}
	}

	private S3BatchUtils() {}
	
	public static String getProperty(String key) {
		
		String value = getParameterFromSSMByName(key);
		if(value!=null && !value.trim().isEmpty()) {
			return value.trim();
		}else if(canReadFromProperties){
			return PROPERTIES.getProperty(key);
		}else {
			return null;
		}
	}

	public static String getUUID() {
		return UUID.randomUUID().toString();
	}
	
	public static String getParameterFromSSMByName(String parameterKey) {		
		try {
			GetParameterRequest parameterRequest = new GetParameterRequest();
			parameterRequest.withName(parameterKey).setWithDecryption(Boolean.valueOf(true));
			AWSSimpleSystemsManagement simpleSystemClient = AWSSimpleSystemsManagementClientBuilder.defaultClient();
			GetParameterResult parameterResult = simpleSystemClient.getParameter(parameterRequest);
			return parameterResult.getParameter().getValue();			
		} catch (Exception e) {
			logger.error("Exception occured while fetching SSM Parameter for Key: {} - Details: {}",parameterKey, e.toString());
			return null;
		}	
	}
	
	public static SsmParameters getSsmParameters() throws Exception {
		return new SsmParameters();
	}

}
