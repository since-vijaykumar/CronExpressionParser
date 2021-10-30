package com.vjy.deliveroo.cron;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vjy.deliveroo.cron.fields.DayOfMonthField;
import com.vjy.deliveroo.cron.fields.DayOfWeekField;
import com.vjy.deliveroo.cron.fields.FieldType;
import com.vjy.deliveroo.cron.fields.SimpleField;

public class CronExpressionTest {

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void parse_number() throws Exception {
		SimpleField field = new SimpleField(FieldType.MINUTE, "5");
		assertEquals(new HashSet<>(Arrays.asList(5)), field.get());
	}

	@Test
	public void parse_number_with_increment() throws Exception {
		SimpleField field = new SimpleField(FieldType.MINUTE, "0/15");
		assertEquals(new HashSet<>(Arrays.asList(0, 15, 30, 45)), field.get());
	}

	@Test
	public void parse_range() throws Exception {
		SimpleField field = new SimpleField(FieldType.MINUTE, "5-10");
		assertEquals(new HashSet<>(Arrays.asList(5, 6, 7, 8, 9, 10)), field.get());
	}

	@Test
	public void parse_range_with_increment() throws Exception {
		SimpleField field = new SimpleField(FieldType.MINUTE, "20-30/2");
		assertEquals(new HashSet<>(Arrays.asList(20, 22, 24, 26, 28, 30)), field.get());
	}

	@Test
	public void parse_asterix() throws Exception {
		SimpleField field = new SimpleField(FieldType.HOUR, "*");
		assertEquals(new HashSet<>(IntStream.range(0, 24).boxed().collect(Collectors.toList())), field.get());
	}

	@Test
	public void parse_asterix_with_increment() throws Exception {
		DayOfWeekField field = new DayOfWeekField(FieldType.DAY_OF_WEEK, "*/2");
		assertEquals(new HashSet<>(Arrays.asList(1, 3, 5, 7)), field.get());
	}

	@Test(expected = IllegalArgumentException.class)
	public void ignore_field_in_day_of_week() throws Exception {
		DayOfWeekField field = new DayOfWeekField(FieldType.DAY_OF_WEEK, "?");
		assertFalse(field.get().isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void ignore_field_in_day_of_month() throws Exception {
		DayOfMonthField field = new DayOfMonthField(FieldType.DAY_OF_MONTH, "?");
		assertFalse(field.get().isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void give_error_if_invalid_count_field() throws Exception {
		new CronExpression("* 3 *");
	}

	@Test(expected = IllegalArgumentException.class)
	public void give_error_if_minute_field_ignored() throws Exception {
		new SimpleField(FieldType.MINUTE, "?");
	}

	@Test(expected = IllegalArgumentException.class)
	public void give_error_if_hour_field_ignored() throws Exception {
		new SimpleField(FieldType.HOUR, "?");
	}

	@Test(expected = IllegalArgumentException.class)
	public void give_error_if_month_field_ignored() throws Exception {
		new SimpleField(FieldType.MONTH, "?");
	}

	@Test(expected = IllegalArgumentException.class)
	public void check_invalid_input() throws Exception {
		new CronExpression(null);
	}

	public String buildDescription(CronExpression exp, Set<Integer> min, Set<Integer> hour, Set<Integer> dayOfMonth,
			Set<Integer> month, Set<Integer> dayOfWeek, String cmd) {
		StringBuffer result = new StringBuffer("");
		result.append(exp.getString(FieldType.MINUTE.getIdentifier(), min));
		result.append("\n");

		result.append(exp.getString(FieldType.HOUR.getIdentifier(), hour));
		result.append("\n");

		result.append(exp.getString(FieldType.DAY_OF_MONTH.getIdentifier(), dayOfMonth));
		result.append("\n");

		result.append(exp.getString(FieldType.MONTH.getIdentifier(), month));
		result.append("\n");

		result.append(exp.getString(FieldType.DAY_OF_WEEK.getIdentifier(), dayOfWeek));
		result.append("\n");

		result.append(exp.getString("command", Collections.<Integer>emptySet(), cmd));
		return result.toString();
	}

	@Test
	public void check_all() throws Exception {
		CronExpression cronExpr = new CronExpression("* * * * * /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(IntStream.range(0, 60).boxed().collect(Collectors.toList()));
		Set<Integer> hour = new TreeSet<>(IntStream.range(0, 24).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfMonth = new TreeSet<>(IntStream.range(1, 32).boxed().collect(Collectors.toList()));
		Set<Integer> month = new TreeSet<>(IntStream.range(1, 13).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfWeek = new TreeSet<>(IntStream.range(1, 8).boxed().collect(Collectors.toList()));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);
	}

	@Test
	public void check_minute_number() throws Exception {
		CronExpression cronExpr = new CronExpression("3 * * * * /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(IntStream.range(3, 4).boxed().collect(Collectors.toList()));
		Set<Integer> hour = new TreeSet<>(IntStream.range(0, 24).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfMonth = new TreeSet<>(IntStream.range(1, 32).boxed().collect(Collectors.toList()));
		Set<Integer> month = new TreeSet<>(IntStream.range(1, 13).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfWeek = new TreeSet<>(IntStream.range(1, 8).boxed().collect(Collectors.toList()));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);
	}

	@Test
	public void check_minute_increment() throws Exception {
		CronExpression cronExpr = new CronExpression("0/15 * * * * /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(Arrays.asList(0, 15, 30, 45));
		Set<Integer> hour = new TreeSet<>(IntStream.range(0, 24).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfMonth = new TreeSet<>(IntStream.range(1, 32).boxed().collect(Collectors.toList()));
		Set<Integer> month = new TreeSet<>(IntStream.range(1, 13).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfWeek = new TreeSet<>(IntStream.range(1, 8).boxed().collect(Collectors.toList()));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);

	}

	@Test
	public void check_minute_list() throws Exception {
		CronExpression cronExpr = new CronExpression("7,19 * * * * /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(Arrays.asList(7, 19));
		Set<Integer> hour = new TreeSet<>(IntStream.range(0, 24).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfMonth = new TreeSet<>(IntStream.range(1, 32).boxed().collect(Collectors.toList()));
		Set<Integer> month = new TreeSet<>(IntStream.range(1, 13).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfWeek = new TreeSet<>(IntStream.range(1, 8).boxed().collect(Collectors.toList()));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);
	}

	@Test
	public void check_hour_number() throws Exception {
		CronExpression cronExpr = new CronExpression("* 3 * * * /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(IntStream.range(0, 60).boxed().collect(Collectors.toList()));
		Set<Integer> hour = new TreeSet<>(Arrays.asList(3));
		Set<Integer> dayOfMonth = new TreeSet<>(IntStream.range(1, 32).boxed().collect(Collectors.toList()));
		Set<Integer> month = new TreeSet<>(IntStream.range(1, 13).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfWeek = new TreeSet<>(IntStream.range(1, 8).boxed().collect(Collectors.toList()));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);
	}

	@Test
	public void check_hour_increment() throws Exception {
		CronExpression cronExpr = new CronExpression("* 0/15 * * * /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(IntStream.range(0, 60).boxed().collect(Collectors.toList()));
		Set<Integer> hour = new TreeSet<>(Arrays.asList(0, 15));
		Set<Integer> dayOfMonth = new TreeSet<>(IntStream.range(1, 32).boxed().collect(Collectors.toList()));
		Set<Integer> month = new TreeSet<>(IntStream.range(1, 13).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfWeek = new TreeSet<>(IntStream.range(1, 8).boxed().collect(Collectors.toList()));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);
	}

	@Test
	public void check_hour_list() throws Exception {
		CronExpression cronExpr = new CronExpression("* 7,19 * * * /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(IntStream.range(0, 60).boxed().collect(Collectors.toList()));
		Set<Integer> hour = new TreeSet<>(Arrays.asList(7, 19));
		Set<Integer> dayOfMonth = new TreeSet<>(IntStream.range(1, 32).boxed().collect(Collectors.toList()));
		Set<Integer> month = new TreeSet<>(IntStream.range(1, 13).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfWeek = new TreeSet<>(IntStream.range(1, 8).boxed().collect(Collectors.toList()));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);

	}

	@Test
	public void check_dayOfMonth_number() throws Exception {
		CronExpression cronExpr = new CronExpression("* * 3 * * /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(IntStream.range(0, 60).boxed().collect(Collectors.toList()));
		Set<Integer> hour = new TreeSet<>(IntStream.range(0, 24).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfMonth = new TreeSet<>(Arrays.asList(3));
		Set<Integer> month = new TreeSet<>(IntStream.range(1, 13).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfWeek = new TreeSet<>(IntStream.range(1, 8).boxed().collect(Collectors.toList()));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);
	}

	@Test
	public void check_dayOfMonth_increment() throws Exception {
		CronExpression cronExpr = new CronExpression("0 0 1/15 * * /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(Arrays.asList(0));
		Set<Integer> hour = new TreeSet<>(Arrays.asList(0));
		Set<Integer> dayOfMonth = new TreeSet<>(Arrays.asList(1, 16, 31));
		Set<Integer> month = new TreeSet<>(IntStream.range(1, 13).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfWeek = new TreeSet<>(IntStream.range(1, 8).boxed().collect(Collectors.toList()));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);

	}

	@Test
	public void check_dayOfMonth_list() throws Exception {
		CronExpression cronExpr = new CronExpression("0 0 7,19 * * /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(Arrays.asList(0));
		Set<Integer> hour = new TreeSet<>(Arrays.asList(0));
		Set<Integer> dayOfMonth = new TreeSet<>(Arrays.asList(7, 19));
		Set<Integer> month = new TreeSet<>(IntStream.range(1, 13).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfWeek = new TreeSet<>(IntStream.range(1, 8).boxed().collect(Collectors.toList()));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);

	}

	@Test(expected = IllegalArgumentException.class)
	public void check_dayOfMonth_invalid_modifier() throws Exception {
		new CronExpression("0 0 9X * * /usr/bin/find");
	}

	@Test(expected = IllegalArgumentException.class)
	public void check_dayOfMonth_invalid_increment_modifier() throws Exception {
		new CronExpression("0 0 9#2 * * /usr/bin/find");
	}

	@Test
	public void check_month_number() throws Exception {
		CronExpression cronExpr = new CronExpression("0 0 1 5 * /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(Arrays.asList(0));
		Set<Integer> hour = new TreeSet<>(Arrays.asList(0));
		Set<Integer> dayOfMonth = new TreeSet<>(Arrays.asList(1));
		Set<Integer> month = new TreeSet<>(Arrays.asList(5));
		Set<Integer> dayOfWeek = new TreeSet<>(IntStream.range(1, 8).boxed().collect(Collectors.toList()));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);
	}

	@Test
	public void check_month_increment() throws Exception {
		CronExpression cronExpr = new CronExpression("0 0 1 5/2 * /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(Arrays.asList(0));
		Set<Integer> hour = new TreeSet<>(Arrays.asList(0));
		Set<Integer> dayOfMonth = new TreeSet<>(Arrays.asList(1));
		Set<Integer> month = new TreeSet<>(Arrays.asList(5, 7, 9, 11));
		Set<Integer> dayOfWeek = new TreeSet<>(IntStream.range(1, 8).boxed().collect(Collectors.toList()));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);
	}

	@Test
	public void check_month_list() throws Exception {
		CronExpression cronExpr = new CronExpression("0 0 1 3,7,12 * /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(Arrays.asList(0));
		Set<Integer> hour = new TreeSet<>(Arrays.asList(0));
		Set<Integer> dayOfMonth = new TreeSet<>(Arrays.asList(1));
		Set<Integer> month = new TreeSet<>(Arrays.asList(3, 7, 12));
		Set<Integer> dayOfWeek = new TreeSet<>(IntStream.range(1, 8).boxed().collect(Collectors.toList()));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);
	}

	@Test
	public void check_month_list_by_name() throws Exception {
		CronExpression cronExpr = new CronExpression("0 0 1 MAR,JUL,DEC * /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(Arrays.asList(0));
		Set<Integer> hour = new TreeSet<>(Arrays.asList(0));
		Set<Integer> dayOfMonth = new TreeSet<>(Arrays.asList(1));
		Set<Integer> month = new TreeSet<>(Arrays.asList(3, 7, 12));
		Set<Integer> dayOfWeek = new TreeSet<>(IntStream.range(1, 8).boxed().collect(Collectors.toList()));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);
	}

	@Test(expected = IllegalArgumentException.class)
	public void check_month_invalid_modifier() throws Exception {
		new CronExpression("0 0 1 ? * /usr/bin/find");
	}

	@Test
	public void check_dayOfWeek_number() throws Exception {
		CronExpression cronExpr = new CronExpression("0 0 * * 3 /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(Arrays.asList(0));
		Set<Integer> hour = new TreeSet<>(Arrays.asList(0));
		Set<Integer> dayOfMonth = new TreeSet<>(IntStream.range(1, 32).boxed().collect(Collectors.toList()));
		Set<Integer> month = new TreeSet<>(IntStream.range(1, 13).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfWeek = new TreeSet<>(Arrays.asList(3));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);

	}

	@Test
	public void check_dayOfWeek_increment() throws Exception {
		CronExpression cronExpr = new CronExpression("0 0 * * 3/2 /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(Arrays.asList(0));
		Set<Integer> hour = new TreeSet<>(Arrays.asList(0));
		Set<Integer> dayOfMonth = new TreeSet<>(IntStream.range(1, 32).boxed().collect(Collectors.toList()));
		Set<Integer> month = new TreeSet<>(IntStream.range(1, 13).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfWeek = new TreeSet<>(Arrays.asList(3, 5, 7));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);
	}

	@Test
	public void check_dayOfWeek_list() throws Exception {
		CronExpression cronExpr = new CronExpression("0 0 * * 1,5,7 /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(Arrays.asList(0));
		Set<Integer> hour = new TreeSet<>(Arrays.asList(0));
		Set<Integer> dayOfMonth = new TreeSet<>(IntStream.range(1, 32).boxed().collect(Collectors.toList()));
		Set<Integer> month = new TreeSet<>(IntStream.range(1, 13).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfWeek = new TreeSet<>(Arrays.asList(1, 5, 7));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);
	}

	@Test
	public void check_dayOfWeek_list_by_name() throws Exception {
		CronExpression cronExpr = new CronExpression("0 0 * * MON,FRI,SUN /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(Arrays.asList(0));
		Set<Integer> hour = new TreeSet<>(Arrays.asList(0));
		Set<Integer> dayOfMonth = new TreeSet<>(IntStream.range(1, 32).boxed().collect(Collectors.toList()));
		Set<Integer> month = new TreeSet<>(IntStream.range(1, 13).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfWeek = new TreeSet<>(Arrays.asList(1, 5, 7));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);
	}

	@Test(expected = IllegalArgumentException.class)
	public void check_dayOfWeek_invalid_modifier() throws Exception {
		new CronExpression("0 0 * * 5W /usr/bin/find");
	}

	@Test(expected = IllegalArgumentException.class)
	public void check_dayOfWeek_invalid_increment_modifier() throws Exception {
		new CronExpression("0 0 * * 5?3 /usr/bin/find");
	}

	@Test
	public void check_dayOfWeek_shall_interpret_0_as_sunday() throws Exception {
		CronExpression cronExpr = new CronExpression("0 0 * * 0 /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(Arrays.asList(0));
		Set<Integer> hour = new TreeSet<>(Arrays.asList(0));
		Set<Integer> dayOfMonth = new TreeSet<>(IntStream.range(1, 32).boxed().collect(Collectors.toList()));
		Set<Integer> month = new TreeSet<>(IntStream.range(1, 13).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfWeek = new TreeSet<>(Arrays.asList(7));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);

	}

	@Test
	public void check_dayOfWeek_shall_interpret_7_as_sunday() throws Exception {
		CronExpression cronExpr = new CronExpression("0 0 * * 7 /usr/bin/find");
		String des = cronExpr.describe();

		Set<Integer> min = new TreeSet<>(Arrays.asList(0));
		Set<Integer> hour = new TreeSet<>(Arrays.asList(0));
		Set<Integer> dayOfMonth = new TreeSet<>(IntStream.range(1, 32).boxed().collect(Collectors.toList()));
		Set<Integer> month = new TreeSet<>(IntStream.range(1, 13).boxed().collect(Collectors.toList()));
		Set<Integer> dayOfWeek = new TreeSet<>(Arrays.asList(7));

		String buildDescription = buildDescription(cronExpr, min, hour, dayOfMonth, month, dayOfWeek, "/usr/bin/find");

		assertEquals(buildDescription, des);

	}
	
	@Test
	public void expression_tostring() throws Exception {
		CronExpression cronExpr = new CronExpression("0 0 * * 7 /usr/bin/find");
		assertEquals("0 0 * * 7 /usr/bin/find", cronExpr.toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shall_not_not_support_rolling_period() throws Exception {
		new CronExpression("* 5-1 * * * /usr/bin/find");
	}

}
