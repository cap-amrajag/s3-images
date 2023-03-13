package org.cap.packet.generation.model;

public class QueryFields {
	private String field_name ;
	private Object field_value ;
	
	public QueryFields(String fieldName, Object fieldValue) {
		this.field_name = fieldName;
		this.field_value = fieldValue;
	}
	
	public String getField_name() {
		return field_name;
	}

	public void setField_name(String field_name) {
		this.field_name = field_name;
	}

	public Object getField_value() {
		return field_value;
	}

	public void setField_value(Object field_value) {
		this.field_value = field_value;
	}
	
	
}
