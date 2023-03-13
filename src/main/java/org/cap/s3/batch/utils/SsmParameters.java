package org.cap.s3.batch.utils;

import org.cap.s3.batch.constants.SSMParameterConstants;
import org.cap.s3.batch.exception.S3BatchException;

public class SsmParameters {
	

	private String informixUrl;
	private String informixPassword;
	private String informixUsername;
	private static final String INFORMIX_DRIVER_CLASS = "com.informix.jdbc.IfxDriver";

	private String postgresUrl;
	private String postgresUsername;
	private String postgresPassword;
	private static final String POSTGRES_DRIVER_CLASS = "org.postgresql.Driver";	

	public SsmParameters() throws Exception {
		try {
			validateSSMParameters();
			informixPassword = S3BatchUtils.getProperty(SSMParameterConstants.INFORMIX_PASSWORD);
			informixUrl = S3BatchUtils.getProperty(SSMParameterConstants.INFORMIX_URL);
			informixUsername = S3BatchUtils.getProperty(SSMParameterConstants.INFORMIX_USERNAME);
			postgresPassword = S3BatchUtils.getProperty(SSMParameterConstants.POSTGRES_PASSWORD);
			postgresUrl = S3BatchUtils.getProperty(SSMParameterConstants.POSTGRES_URL);
			postgresUsername = S3BatchUtils.getProperty(SSMParameterConstants.POSTGRES_USERNAME);
		} catch (Exception e) {
			throw new S3BatchException("Exception in SsmParameters: ".concat(e.toString()));
		}
	}

	private void validateSSMParameters() throws Exception {
		String errorMessage = "";
		errorMessage += validateProperty(SSMParameterConstants.INFORMIX_PASSWORD);
		errorMessage += validateProperty(SSMParameterConstants.INFORMIX_URL);
		errorMessage += validateProperty(SSMParameterConstants.INFORMIX_USERNAME);
		errorMessage += validateProperty(SSMParameterConstants.POSTGRES_PASSWORD);
		errorMessage += validateProperty(SSMParameterConstants.POSTGRES_URL);
		errorMessage += validateProperty(SSMParameterConstants.POSTGRES_USERNAME);
		if (!errorMessage.isEmpty()) {
			throw new S3BatchException("Validation Failed: \n ".concat(errorMessage));
		}
	}

	public String validateProperty(String key) {
		String value = S3BatchUtils.getProperty(key);
		if (value != null && !value.isEmpty()) {
			return "";
		} else {
			return String.format("%nnSSM Paramater %s is not fetched.", key);
		}
	}

	public String getInformixUrl() {
		return informixUrl;
	}

	public String getInformixPassword() {
		return informixPassword;
	}

	public String getInformixUsername() {
		return informixUsername;
	}

	public String getInformixDriverClass() {
		return INFORMIX_DRIVER_CLASS;
	}

	public String getPostgresUrl() {
		return postgresUrl;
	}

	public String getPostgresUsername() {
		return postgresUsername;
	}

	public String getPostgresPassword() {
		return postgresPassword;
	}

	public String getPostgresDriverClass() {
		return POSTGRES_DRIVER_CLASS;
	}

}
