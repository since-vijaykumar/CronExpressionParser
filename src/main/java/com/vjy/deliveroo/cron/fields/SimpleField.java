package com.vjy.deliveroo.cron.fields;

public class SimpleField extends BaseField {

	/**
	 * Class representing time field ( minutes/hours/month ).
	 * @param type
	 * @param fldExp
	 */
	public SimpleField(FieldType type, String fldExp) {
		super(type, fldExp);
	}

}
