package org.cap.s3.batch.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.cap.s3.batch.exception.S3BatchException;
import org.cap.s3.batch.utils.SsmParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3BatchRepository implements AutoCloseable {
	
	private Logger logger = LoggerFactory.getLogger(S3BatchRepository.class);

	private Connection informixConnection;

	private Connection postgresConnection;

	private SsmParameters ssmParameters;

	public S3BatchRepository(SsmParameters ssmParameters) throws S3BatchException {
		this.ssmParameters = ssmParameters;

		// Create Database Connections
		createInformixDbConnection();
		createPostgresDbConnection();
	}

	private void createInformixDbConnection() throws S3BatchException {
		try {
			this.informixConnection = DriverManager.getConnection(ssmParameters.getInformixUrl(),
					ssmParameters.getInformixUsername(), ssmParameters.getInformixPassword());
		} catch (Exception e) {
			throw new S3BatchException("Error occured creating connection to Informix Server".concat(e.toString()));
		}
	}

	public Connection getInformixConnection() {
		return this.informixConnection;
	}

	private void createPostgresDbConnection() throws S3BatchException {
		try {
			this.postgresConnection = DriverManager.getConnection(ssmParameters.getPostgresUrl(),
					ssmParameters.getPostgresUsername(), ssmParameters.getPostgresPassword());
		} catch (Exception e) {
			logger.error("Exception in createPostgresDbConnection: {}",e.toString());
//			TODO: comment
			throw new S3BatchException("Error occured creating connection to Postgres Server".concat(e.toString()));
		}
	}

	public Connection getPostgresConnection() {
		return this.postgresConnection;
	}

	public void removeConnections() {

		try {
			if (null != getInformixConnection())
				this.informixConnection.close();
			if (null != getPostgresConnection())
				this.postgresConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void close() throws Exception {
		// Release Database Connections
		removeConnections();
	}
}
