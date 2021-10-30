package com.vjy.deliveroo.cron.fields;

import java.util.Set;

public class DayOfWeekField extends SimpleField {

	public DayOfWeekField(FieldType type, String fldExp) {
		super(type, fldExp);
	}

	@Override
	protected int mapToValue(String value) {
		// 0 and 7 are represented as sunday
		return "0".equals(value) ? 7 : super.mapToValue(value);
	}

	protected Set<Integer> getDaysOfWeek() {
		return build();
	}

	public Set<Integer> get() {

		Set<Integer> result = null;
		switch (type) {
		case DAY_OF_WEEK:
			Set<Integer> dayOfWeek = getDaysOfWeek();
			result = dayOfWeek;
			break;
		default:
			break;
		}

		return result;
	}

}
