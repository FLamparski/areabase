package nde2.helpers;
/** !license-block 
    This file is part of Areabase.

    Areabase is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Areabase is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Areabase.  If not, see <http://www.gnu.org/licenses/>.

    Areabase (C) 2013-2014 Filip Wieland <filiplamparski@gmail.com>
*/
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
	 * @return a date object from an NDE date string
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
	 * @return a date object from an NDE date/time string
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
		return String.format("%1$tF", CACHED_CALENDAR);
	}

	/**
	 * 
	 * @param date the date to get the year out of
	 * @return The year of given date
	 */
	public static int getYear(Date date) {
		CACHED_CALENDAR.setTime(date);
		return CACHED_CALENDAR.get(GregorianCalendar.YEAR);
	}
}
