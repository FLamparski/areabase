package nde2.pull.types;
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
import java.io.Serializable;
import java.util.Date;

import nde2.helpers.DateFormat;

/**
 * Represents a date range
 * 
 * @author filip
 * 
 */
public class DateRange implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date startDate;
	private Date endDate;

	/**
	 * @param startDate
	 *            Starting date of this date range
	 * @param endDate
	 *            ending date of this date range
	 */
	public DateRange(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public DateRange(String startDateString, String endDateString) {
		startDate = DateFormat.fromNDEDateTime(startDateString);
		endDate = DateFormat.fromNDEDateTime(endDateString);
	}

	public DateRange() {
	}

	/**
	 * @return the Starting date of this date range
	 */
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the ending date of this date range
	 */
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return The duration of this time range
	 */
	public Date getDuration() {
		return new Date(endDate.getTime() - startDate.getTime());
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }
		if (obj == null) {
            return false;
        }
		if (!(obj instanceof DateRange)) {
            return false;
        }
		DateRange other = (DateRange) obj;
		if (endDate == null) {
			if (other.endDate != null) {
                return false;
            }
		} else if (!endDate.equals(other.endDate)) {
            return false;
        }
		if (startDate == null) {
			if (other.startDate != null) {
                return false;
            }
		} else if (!startDate.equals(other.startDate)) {
            return false;
        }
		return true;
	}

    /**
     * Find the most recent date range in the array
     * @param dateRanges the array to search
     * @return the date range whose end date is closest to now
     */
    public static DateRange mostRecent(DateRange[] dateRanges){
        DateRange ret = null;
        long now = System.currentTimeMillis();
        long smallestDifference = Long.MAX_VALUE;
        for(DateRange r : dateRanges){
            long diff = now - r.endDate.getTime();
            if(diff < smallestDifference){
                smallestDifference = diff;
                ret = r;
            }
        }
        return ret;
    }

}
