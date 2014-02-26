package police.types;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;

public class Crime {
	private String category;
	private String persistent_id;
	private String location_type;
	private String location_subtype;
	private long id;
	private Location location;
	private String context;
	private OutcomeStatus outcome_status;

	@SuppressWarnings("unused")
	private Crime() {
	}

	/**
	 * @param category
	 * @param persistent_id
	 * @param location_type
	 * @param location_subtype
	 * @param id
	 * @param location
	 * @param context
	 * @param outcome_status
	 */
	public Crime(String category, String persistent_id, String location_type,
			String location_subtype, long id, Location location,
			String context, OutcomeStatus outcome_status) {
		this.category = category;
		this.persistent_id = persistent_id;
		this.location_type = location_type;
		this.location_subtype = location_subtype;
		this.id = id;
		this.location = location;
		this.context = context;
		this.outcome_status = outcome_status;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @return the persistent_id
	 */
	public String getPersistent_id() {
		return persistent_id;
	}

	/**
	 * @return the location_type
	 */
	public String getLocation_type() {
		return location_type;
	}

	/**
	 * @return the location_subtype
	 */
	public String getLocation_subtype() {
		return location_subtype;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @return the context
	 */
	public String getContext() {
		return context;
	}

	/**
	 * @return the outcome_status
	 */
	public OutcomeStatus getOutcomeStatus() {
		return outcome_status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String locStr = location.toString();
		if (locStr == null) {
			locStr = "(null)";
		}
		String outcomeStr = null;
		try {
			outcomeStr = outcome_status.toString();
		} catch (Exception e) {
			if (outcome_status == null) {
				Log.w("Crime", String.format("Crime %d (%s): %s %s\n", id, persistent_id,
                        category, location.getStreet().getName()));
            }
            Log.w("Crime", " >> Outcome status for Crime@"
					+ Integer.toHexString(this.hashCode()) + " is null!");
		}
		if (outcomeStr == null) {
			locStr = "(null)";
		}
		String category = this.category;
		String persistent_id = this.persistent_id;
		String location_type = this.location_type;
		String location_subtype = this.location_subtype;
		String context = this.context;
		if (this.category == null){
			category = "(null)";
        }
		if (this.persistent_id == null){
			persistent_id = "(null)";
        }
		if (this.location_type == null){
			location_type = "(null)";
        }
		if (this.location_subtype == null){
			location_type = "(null)";
        }
		if (this.context == null) {
			context = "(null)";
		}
		return "Crime [category=" + category + ", persistent_id="
				+ persistent_id + ", location_type=" + location_type
				+ ", location_subtype=" + location_subtype + ", id="
				+ Long.toString(id) + ", location=" + locStr + ", context="
				+ context + ", outcome_status=" + outcomeStr + "]";
	}

	/**
	 * This method is required to convey all the non-exceptional but important
	 * information about this Crime.
	 * 
	 * @return An Entry, where the key is the string representation of the
	 *         object, and the value is a list of remarks about the object's
	 *         retrieval. It will be empty if everything is OK.
	 */
	public KeyValuePair<String, Collection<String>> toStringWithExtraInfo() {
		final ArrayList<String> remarks = new ArrayList<String>();
		String locStr = null;
		try {
			locStr = location.toString();
		} catch (Exception e1) {
			remarks.add("location is null");
		}
		if (locStr == null) {
			locStr = "(null)";
			remarks.add("location string is null");
		}
		String outcomeStr = null;
		try {
			outcomeStr = outcome_status.toString();
		} catch (Exception e) {
			remarks.add("outcome_status is null");
		}
		if (outcomeStr == null) {
			locStr = "(null)";
			remarks.add("outcome_status string is null");
		}
		String category = this.category;
		String persistent_id = this.persistent_id;
		String location_type = this.location_type;
		String location_subtype = this.location_subtype;
		String context = this.context;
		if (this.category == null) {
			category = "(null)";
			remarks.add("category is null");
		}
		if (this.persistent_id == null) {
			persistent_id = "(null)";
			remarks.add("persistent_id is null");
		}
		if (this.location_type == null) {
			location_type = "(null)";
			remarks.add("location_type is null");
		}
		if (this.location_subtype == null) {
			location_type = "(null)";
			remarks.add("location_subtype is null");
		}
		if (this.context == null) {
			context = "(null)";
			remarks.add("context is null");
		}
		final String toString = "Crime [category=" + category
				+ ", persistent_id=" + persistent_id + ", location_type="
				+ location_type + ", location_subtype=" + location_subtype
				+ ", id=" + Long.toString(id) + ", location=" + locStr
				+ ", context=" + context + ", outcome_status=" + outcomeStr
				+ "]";
		return new KeyValuePair<String, Collection<String>>(toString, remarks);
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
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime
				* result
				+ ((location_subtype == null) ? 0 : location_subtype.hashCode());
		result = prime * result
				+ ((location_type == null) ? 0 : location_type.hashCode());
		result = prime * result
				+ ((outcome_status == null) ? 0 : outcome_status.hashCode());
		result = prime * result
				+ ((persistent_id == null) ? 0 : persistent_id.hashCode());
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
		if (!(obj instanceof Crime))
			return false;
		Crime other = (Crime) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (id != other.id)
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (location_subtype == null) {
			if (other.location_subtype != null)
				return false;
		} else if (!location_subtype.equals(other.location_subtype))
			return false;
		if (location_type == null) {
			if (other.location_type != null)
				return false;
		} else if (!location_type.equals(other.location_type))
			return false;
		if (outcome_status == null) {
			if (other.outcome_status != null)
				return false;
		} else if (!outcome_status.equals(other.outcome_status))
			return false;
		if (persistent_id == null) {
			if (other.persistent_id != null)
				return false;
		} else if (!persistent_id.equals(other.persistent_id))
			return false;
		return true;
	}

}
