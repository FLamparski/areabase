package police.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

public class CaseHistory {
	private Collection<Outcome> outcomes;
	private Crime crime;

	@SuppressWarnings("unused")
	private CaseHistory() {
	}

	/**
	 * @param outcomes
	 * @param crime
	 */
	public CaseHistory(Collection<Outcome> outcomes, Crime crime) {
		this.outcomes = outcomes;
		this.crime = crime;
	}

	/**
	 * @return the outcomes
	 */
	public Collection<Outcome> getOutcomes() {
		return outcomes;
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
		result = prime * result + ((crime == null) ? 0 : crime.hashCode());
		result = prime * result
				+ ((outcomes == null) ? 0 : outcomes.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CaseHistory))
			return false;
		CaseHistory other = (CaseHistory) obj;
		if (crime == null) {
			if (other.crime != null)
				return false;
		} else if (!crime.equals(other.crime))
			return false;
		if (outcomes == null) {
			if (other.outcomes != null)
				return false;
		} else if (!outcomes.equals(other.outcomes))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String outcomes;
		try {
			outcomes = this.outcomes.toString();
		} catch (Exception e) {
			outcomes = "(null)";
		}

		String crime;
		try {
			crime = this.crime.toString();
		} catch (Exception e) {
			crime = "(null)";
		}

		return "CaseHistory [outcomes=" + outcomes + ", crime=" + crime + "]";
	}

	public Entry<String, Collection<String>> toStringWithExtraInfo() {
		Entry<String, Collection<String>> swei_crime = crime
				.toStringWithExtraInfo();
		final ArrayList<String> remarks = new ArrayList<String>();

		String crime = swei_crime.getKey();
		remarks.addAll(swei_crime.getValue());

		String outcomes;
		try {
			outcomes = this.outcomes.toString();
		} catch (Exception e) {
			outcomes = "(null)";
			remarks.add("outcomes are null");
		}

		final String mStr = "CaseHistory [outcomes=" + outcomes + ", crime="
				+ crime + "]";

		return new KeyValuePair<String, Collection<String>>(mStr, remarks);
	}
}
