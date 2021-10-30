package com.vjy.deliveroo.cron.fields;

import java.util.Set;

public class DayOfMonthField extends SimpleField {

	public DayOfMonthField(FieldType type, String fldExp) {
		super(type, fldExp);
	}

	protected Set<Integer> getDaysOfMonth() {
		return build();
	}

	public Set<Integer> get() {

		Set<Integer> result = null;
		switch (type) {
		case DAY_OF_MONTH:
			Set<Integer> dayOfMonth = getDaysOfMonth();
			result = dayOfMonth;
			break;
		default:
			break;
		}

		return result;
	}

}
