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
	 * @param outcomes the crime outcomes to set for the crime
	 * @param crime the crime to set
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
		if (this == obj) {
            return true;
        }
		if (obj == null) {
            return false;
        }
		if (!(obj instanceof CaseHistory)) {
            return false;
        }
		CaseHistory other = (CaseHistory) obj;
		if (crime == null) {
			if (other.crime != null) {
                return false;
            }
		} else if (!crime.equals(other.crime)) {
            return false;
        }
		if (outcomes == null) {
			if (other.outcomes != null) {
                return false;
            }
		} else if (!outcomes.equals(other.outcomes)) {
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
