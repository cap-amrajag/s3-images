package org.cap.packet.generation.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.cap.packet.generation.constants.S3BatchConstants;
import org.cap.packet.generation.exception.S3BatchException;
import org.cap.packet.generation.model.AdditionalData;
import org.cap.packet.generation.utils.SsmParameters;
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
			logger.error("Exception in createPostgresDbConnection: {}", e.toString());
			// TODO: comment
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

	public long getImageAuId(long additionalDataId) throws Exception {
		ResultSet rs = null;
		long auId = -1;
		try (PreparedStatement st = getInformixConnection().prepareStatement(S3BatchConstants.QUERY_GET_IMAGE_AU_ID)) {
			st.setLong(1, additionalDataId);
			rs = st.executeQuery();
			if (rs != null && rs.next()) {
				auId = rs.getInt(1);
			}
		} finally {
			if (rs != null)
				rs.close();
		}
		return auId;
	}

	public void getAdditionalDataForDocTypeDIRCV(AdditionalData additionalData, long additionalDataId) throws Exception {
		ResultSet rs = null;
		try (PreparedStatement st = getInformixConnection().prepareStatement(S3BatchConstants.QUERY_GET_ADDITIONAL_DATA_FOR_DOC_TYPE_DIRCV)) {
			st.setLong(1, additionalDataId);
			rs = st.executeQuery();
			if (rs != null && rs.next()) {
				additionalData.setPersonid(rs.getLong(1));
			}
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	public void getAdditionalDataForDocTypeCXINSPPKT(AdditionalData additionalData, long additionalDataId) throws Exception {
		ResultSet rs = null;
		try (PreparedStatement st = getInformixConnection().prepareStatement(S3BatchConstants.QUERY_GET_ADDITIONAL_DATA_FOR_DOC_TYPE_CXINSPPKT)) {
			st.setLong(1, additionalDataId);
			rs = st.executeQuery();
			if (rs != null && rs.next()) {
				additionalData.setCompnbr(additionalDataId);
			}
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	public void getAdditionalDataForDocTypesINSTLISTOrPOCTST(AdditionalData additionalData, long additionalDataId) throws Exception {
		ResultSet rs = null;
		try (PreparedStatement st = getInformixConnection().prepareStatement(S3BatchConstants.QUERY_GET_ADDITIONAL_DATA_FOR_DOC_TYPES_INSTLIST_OR_POCTST)) {
			st.setLong(1, additionalDataId);
			rs = st.executeQuery();
			if (rs != null && rs.next()) {
				additionalData.setSuId(additionalDataId);
			}
		} finally {
			if (rs != null)
				rs.close();
		}
	}
}
