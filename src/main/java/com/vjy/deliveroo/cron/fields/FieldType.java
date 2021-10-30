package com.vjy.deliveroo.cron.fields;

import java.util.Arrays;
import java.util.List;

public enum FieldType {

	MINUTE(0, 59, "minute", null), 
	HOUR(0, 23, "hour", null),
	DAY_OF_MONTH(1, 31, "day of month", null),
	MONTH(1, 12, "month", Arrays.asList("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")),
	DAY_OF_WEEK(1, 7, "day of week", Arrays.asList("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"));

	private final int from;
	private final int to;
	private final String identifier;
	private final List<String> names;

	FieldType(int from, int to, String identifier, List<String> names) {
		this.from = from;
		this.to = to;
		this.identifier = identifier;
		this.names = names;
	}

	public int getTo() {
		return to;
	}

	public int getFrom() {
		return from;
	}

	public String getIdentifier() {
		return identifier;
	}

	public List<String> getNames() {
		return names;
	}

}
