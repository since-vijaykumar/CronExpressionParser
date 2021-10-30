package com.vjy.deliveroo.cron.fields;

 class FieldPart implements Comparable<FieldPart>{
	protected int from = -1;
	protected int to = -1;
	protected int increValue = -1;
	protected String metaChar;
	protected String increMetaChar;
	protected boolean all;
	
	@Override
	public int compareTo(FieldPart part) {
		return Integer.compare(from, part.from);
	}
	
}
