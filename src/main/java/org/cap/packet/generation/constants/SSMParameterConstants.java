package org.cap.packet.generation.constants;

public class SSMParameterConstants {
	
	private SSMParameterConstants() {}
	
	public static final String INFORMIX_URL  = "/cap/systems/scores/ha/jdbc-url";
	public static final String INFORMIX_USERNAME  = "/cap/apps/mcc/datasource/informix/update/username"; 
	public static final String INFORMIX_PASSWORD  = "/cap/apps/mcc/datasource/informix/update/password";
	
	public static final String POSTGRES_URL = "/cap/systems/postgresql/mcc/url";
	public static final String POSTGRES_USERNAME = "/cap/apps/mcc/postgresql/update/username"; 
	public static final String POSTGRES_PASSWORD = "/cap/apps/mcc/postgresql/update/password";	
	
	public static final String THUNDERHEAD_FILEPATH = "THUNDERHEAD_FILEPATH";
	public static final String CAP_DOMAIN = "CAP_DOMAIN";
	public static final String WEB_SERVICE_URL = "WEB_SERVICE_URL";
	public static final String POLLING_INTERVAL = "POLLING_INTERVAL";
	public static final String JOB_ITERATIONS = "JOB_ITERATIONS";
}
