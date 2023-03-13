package org.cap.packet.generation.model;

import java.util.Collections;
import java.util.List;

public class SearchQuery {

	private String queryType;
	private List<QueryFields> queryFields = Collections.emptyList();
	private List<SearchQuery> subQueries = Collections.emptyList();
	private String queryOperation;

	SearchQuery(String queryType, String queryOperation) {
		this.queryType = queryType;
		this.queryOperation = queryOperation;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public List<QueryFields> getQueryFields() {
		return queryFields;
	}

	public void setQueryFields(List<QueryFields> queryFields) {
		this.queryFields = queryFields;
	}

	public List<SearchQuery> getSubQueries() {
		return subQueries;
	}

	public void setSubQueries(List<SearchQuery> subQueries) {
		this.subQueries = subQueries;
	}

	public String getQueryOperation() {
		return queryOperation;
	}

	public void setQueryOperation(String queryOperation) {
		this.queryOperation = queryOperation;
	}
	
	
}
