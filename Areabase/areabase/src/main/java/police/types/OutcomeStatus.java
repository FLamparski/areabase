package police.types;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OutcomeStatus {
	private String category;
	private String date;

	@SuppressWarnings("unused")
	private OutcomeStatus() {
	}

	/**
	 * @param category outcome status category
	 * @param date date of this outcome status occurring.
	 */
	public OutcomeStatus(String category, String date) {
		this.category = category;
		this.date = date;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @return the date
	 * @throws ParseException
	 */
	public Date getDate() throws ParseException {
		return new SimpleDateFormat("yyyy-MM").parse(date);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OutcomeStatus [category=" + category + ", date=" + date + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		if (!(obj instanceof OutcomeStatus)) {
            return false;
        }
		OutcomeStatus other = (OutcomeStatus) obj;
		if (category == null) {
			if (other.category != null) {
                return false;
            }
		} else if (!category.equals(other.category)) {
            return false;
        }
		if (date == null) {
			if (other.date != null) {
                return false;
            }
		} else if (!date.equals(other.date)) {
            return false;
        }
		return true;
	}

}
