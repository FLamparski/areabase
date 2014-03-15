package police.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

public class Outcome {
	private OutcomeCategory category;
	private String date;
	private Crime crime;

	@SuppressWarnings("unused")
	private Outcome() {
	}

	/**
	 * @param category outcome category
	 * @param date unparsed date at which the crime occurred
	 * @param crime the crime itself
	 */
	public Outcome(OutcomeCategory category, String date, Crime crime) {
		this.category = category;
		this.date = date;
		this.crime = crime;
	}

	/**
	 * @return the category
	 */
	public OutcomeCategory getCategory() {
		return category;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @return the crime
	 */
	public Crime getCrime() {
		return crime;
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
		result = prime * result + ((crime == null) ? 0 : crime.hashCode());
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
		if (!(obj instanceof Outcome)) {
            return false;
        }
		Outcome other = (Outcome) obj;
		if (category == null) {
			if (other.category != null) {
                return false;
            }
		} else if (!category.equals(other.category)) {
            return false;
        }
		if (crime == null) {
			if (other.crime != null) {
                return false;
            }
		} else if (!crime.equals(other.crime)) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Outcome [category=" + category + ", date=" + date + ", crime="
				+ crime + "]";
	}

	public Entry<String, Collection<String>> toStringWithExtraInfo() {
		Entry<String, Collection<String>> swei_crime = crime
				.toStringWithExtraInfo();
		final ArrayList<String> remarks = new ArrayList<String>();

		String category;
		try {
			category = this.category.toString();
		} catch (Exception e) {
			remarks.add("category is null");
			category = "(null)";
		}

		String date = this.date;

		String crime = swei_crime.getKey();
		remarks.addAll(swei_crime.getValue());

		final String mStr = "Outcome [category=" + category + ", date=" + date
				+ ", crime=" + crime + "]";

		return new KeyValuePair<String, Collection<String>>(mStr, remarks);
	}
}
