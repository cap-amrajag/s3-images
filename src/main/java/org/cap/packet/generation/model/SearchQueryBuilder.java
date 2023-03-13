package org.cap.packet.generation.model;

import org.cap.packet.generation.constants.S3BatchConstants;
import org.cap.packet.generation.constants.S3BatchConstants.QueryType;

public class SearchQueryBuilder {

	private QueryType queryType;

	public SearchQueryBuilder(QueryType queryType) {
		this.queryType = queryType;
	}

	public static SearchQueryBuilder ofQueryType(QueryType queryType) {
		return new SearchQueryBuilder(queryType);
	}

	public SearchQuery build() {
		switch (queryType) {
		case COMPOUND:
			return new SearchQuery(S3BatchConstants.COMPOUND, S3BatchConstants.OR);
		case SIMPLE:
			return new SearchQuery(S3BatchConstants.SIMPLE, S3BatchConstants.AND);
		default:
			break;
		}
		return null;
	}
}
