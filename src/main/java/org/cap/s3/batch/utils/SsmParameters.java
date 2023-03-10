package org.cap.s3.batch.utils;

import org.cap.s3.batch.constants.SSMParameterConstants;

public class SsmParameters {
	

	private String informixUrl;
	private String informixPassword;
	private String informixUsername;
	private static final String INFORMIX_DRIVER_CLASS = "com.informix.jdbc.IfxDriver";

	private String postgresUrl;
	private String postgresUsername;
	private String postgresPassword;
	private static final String POSTGRES_DRIVER_CLASS = "org.postgresql.Driver";

	private String oracleUrl;
	private String oracleUsername;
	private String oraclePassword;
	private String oracleDriverClass;

	private String thunderheadFilepath;
	private String capDomain;
	private String webServiceUrl;
	private Integer pollingInterval;
	private Integer jobIterations;
	

	public SsmParameters() throws Exception {
		try {
			validateSSMParameters();
			informixPassword = CommonUtils.getProperty(SSMParameterConstants.INFORMIX_PASSWORD);
			informixUrl = CommonUtils.getProperty(SSMParameterConstants.INFORMIX_URL);
			informixUsername = CommonUtils.getProperty(SSMParameterConstants.INFORMIX_USERNAME);
			postgresPassword = CommonUtils.getProperty(SSMParameterConstants.POSTGRES_PASSWORD);
			postgresUrl = CommonUtils.getProperty(SSMParameterConstants.POSTGRES_URL);
			postgresUsername = CommonUtils.getProperty(SSMParameterConstants.POSTGRES_USERNAME);
			thunderheadFilepath = CommonUtils.getProperty(SSMParameterConstants.THUNDERHEAD_FILEPATH);
			capDomain =  CommonUtils.getProperty(SSMParameterConstants.CAP_DOMAIN);
			webServiceUrl = CommonUtils.getProperty(SSMParameterConstants.WEB_SERVICE_URL);
			pollingInterval = Integer.parseInt(CommonUtils.getProperty(SSMParameterConstants.POLLING_INTERVAL));
			jobIterations = Integer.parseInt(CommonUtils.getProperty(SSMParameterConstants.JOB_ITERATIONS));
		} catch (Exception e) {
			throw new Exception("Exception in SsmParameters: ".concat(e.toString()));
			
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
		errorMessage += validateProperty(SSMParameterConstants.THUNDERHEAD_FILEPATH);
		errorMessage += validateProperty(SSMParameterConstants.CAP_DOMAIN);
		errorMessage += validateProperty(SSMParameterConstants.WEB_SERVICE_URL);
		errorMessage += validateProperty(SSMParameterConstants.POLLING_INTERVAL);
		errorMessage += validateProperty(SSMParameterConstants.JOB_ITERATIONS);
		if (!errorMessage.isEmpty()) {
			throw new Exception("Validation Failed: \n ".concat(errorMessage));
		}
	}

	public String validateProperty(String key) {
		String value = CommonUtils.getProperty(key);
		if (value != null && !value.isEmpty()) {
			return "";
		} else {
			return String.format("\nSSM Paramater %s is not fetched.", key);
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

	public String getOracleUrl() {
		return oracleUrl;
	}

	public String getOracleUsername() {
		return oracleUsername;
	}

	public String getOraclePassword() {
		return oraclePassword;
	}

	public String getOracleDriverClass() {
		return oracleDriverClass;
	}

	public String getThunderheadFilepath() {
		return thunderheadFilepath;
	}

	public String getCapDomain() {
		return capDomain;
	}

	public String getWebServiceUrl() {
		return webServiceUrl;
	}

	public Integer getPollingInterval() {
		return pollingInterval;
	}

	public Integer getJobIterations() {
		return jobIterations;
	}
}
