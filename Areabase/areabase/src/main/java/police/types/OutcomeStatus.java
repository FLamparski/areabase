package police.types;

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
