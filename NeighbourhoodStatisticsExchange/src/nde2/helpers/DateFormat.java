package nde2.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateFormat {

	private static final Calendar CACHED_CALENDAR = new GregorianCalendar();

	static {
		CACHED_CALENDAR.setTimeZone(TimeZone.getTimeZone("GMT"));
		CACHED_CALENDAR.clear();
	}

	/**
	 * 
	 * @param dateString
	 *            Expected format: yyyy-MM-dd
	 * @return
	 */
	public static Date fromNDEDateOnly(String dateString) {
		dateString = dateString.trim(); // If anything gets messed up.
		int y = Integer.parseInt(dateString.substring(0, 4));
		int m = Integer.parseInt(dateString.substring(5, 7));
		--m;
		int d = Integer.parseInt(dateString.substring(8, 10));

		CACHED_CALENDAR.set(y, m, d);
		return CACHED_CALENDAR.getTime();
	}

	/**
	 * 
	 * @param datetimeString
	 *            Expected format: yyyy-MM-ddT~~~~~~
	 * @return
	 */
	public static Date fromNDEDateTime(String datetimeString) {
		return fromNDEDateOnly(datetimeString.split("T")[0]);
	}

	/**
	 * A faster? variant of new SimpleDateFormat("yyyy-MM-dd").format(date).
	 * 
	 * @param date
	 *            The date to be formatted
	 * @return A string formatted using the year-month-day format.
	 */
	public static String toNDEDate(Date date) {
		CACHED_CALENDAR.setTime(date);
		StringBuilder b = new StringBuilder();
		b.append(CACHED_CALENDAR.get(GregorianCalendar.YEAR)).append("-");
		b.append(CACHED_CALENDAR.get(GregorianCalendar.MONTH)).append("-");
		b.append(CACHED_CALENDAR.get(GregorianCalendar.DAY_OF_MONTH));
		return b.toString();
	}
}
