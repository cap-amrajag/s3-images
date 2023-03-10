package org.cap.s3.batch.utils;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;

public class CommonUtils {

	private static final Properties PROPERTIES = new Properties();

	private static String strUUID = null;
	
	protected static Logger log = LoggerFactory.getLogger(CommonUtils.class);

	private static boolean canReadFromProperties = false;

	static {
		try {
			PROPERTIES.load(CommonUtils.class.getResourceAsStream("/application.properties"));
			if(PROPERTIES.getProperty("canReadFromProperties")!=null) {
				canReadFromProperties = PROPERTIES.getProperty("canReadFromProperties").equalsIgnoreCase("yes");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private CommonUtils() {}
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
		if (strUUID == null) {
			strUUID = UUID.randomUUID().toString();
		}

		return strUUID;
	}
	
	public static String getParameterFromSSMByName(String parameterKey) {
		
		try {
			GetParameterRequest parameterRequest = new GetParameterRequest();
			parameterRequest.withName(parameterKey).setWithDecryption(Boolean.valueOf(true));
			AWSSimpleSystemsManagement simpleSystemClient = AWSSimpleSystemsManagementClientBuilder.defaultClient();
			GetParameterResult parameterResult = simpleSystemClient.getParameter(parameterRequest);
			return parameterResult.getParameter().getValue();			
		} catch (Exception e) {
			log.error("Exception occured while fetching SSM Parameter for Key: {} - Details: {}",parameterKey, e.toString());
			return null;
		}
		

	}

}
