package nde2.pull.types;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Dataset implements Serializable {

	private static final long serialVersionUID = 1L;

	private int familyId;
	private String creator;
	private String description;
	private String subjectCategory;
	private String title;
	private Map<Integer, Topic> topics;
	private Map<Integer, Period> periods;
	private Map<Integer, Boundary> boundaries;
	private Set<DataSetItem> items;

	public Dataset() {
	}

	/**
	 * @return the familyId
	 */
	public int getFamilyId() {
		return familyId;
	}

	/**
	 * @param familyId
	 *            the familyId to set
	 */
	public void setFamilyId(int familyId) {
		this.familyId = familyId;
	}

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @param creator
	 *            the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the subjectCategory
	 */
	public String getSubjectCategory() {
		return subjectCategory;
	}

	/**
	 * @param subjectCategory
	 *            the subjectCategory to set
	 */
	public void setSubjectCategory(String subjectCategory) {
		this.subjectCategory = subjectCategory;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the topics
	 */
	public Map<Integer, Topic> getTopics() {
		return topics;
	}

	/**
	 * @param dstopics
	 *            the topics to set
	 */
	public void setTopics(Map<Integer, Topic> dstopics) {
		this.topics = dstopics;
	}

	/**
	 * @return the period
	 */
	public Map<Integer, Period> getPeriods() {
		return periods;
	}

	/**
	 * @param dsperiods
	 *            the period to set
	 */
	public void setPeriods(Map<Integer, Period> dsperiods) {
		this.periods = dsperiods;
	}

	/**
	 * @return the boundaries
	 */
	public Map<Integer, Boundary> getBoundaries() {
		return boundaries;
	}

	/**
	 * @param dsboundaries
	 *            the boundaries to set
	 */
	public void setBoundaries(Map<Integer, Boundary> dsboundaries) {
		this.boundaries = dsboundaries;
	}

	/**
	 * @return the items as a list
	 */
	public Set<DataSetItem> getItems() {
		return items;
	}

	/**
	 * 
	 * @param t topic to filter the dataset by
	 * @return the items with topic t
	 */
	public Set<DataSetItem> getItems(Topic t) {
		Set<DataSetItem> filtered = new HashSet<DataSetItem>();
		for (DataSetItem e : items) {
			if (e.getTopic().equals(t)) {
                filtered.add(e);
            }
		}
		return filtered;
	}

	/**
	 * 
	 * @param p period to filter the dataset by
	 * @return the items with period p
	 */
	public Set<DataSetItem> getItems(Period p) {
		Set<DataSetItem> filtered = new HashSet<DataSetItem>();
		for (DataSetItem e : items) {
			if (e.getTopic().equals(p)) {
                filtered.add(e);
            }
		}
		return filtered;
	}

	/**
	 * 
	 * @param b boundary to filter the dataset by
	 * @return the items with boundary b
	 */
	public Set<DataSetItem> getItems(Boundary b) {
		Set<DataSetItem> filtered = new HashSet<DataSetItem>();
		for (DataSetItem e : items) {
			if (e.getTopic().equals(b)) {
                filtered.add(e);
            }
		}
		return filtered;
	}

	/**
	 * @param items
	 *            the items to set
	 */
	public void setItems(Set<DataSetItem> items) {
		this.items = items;
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
				+ ((boundaries == null) ? 0 : boundaries.hashCode());
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + familyId;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result + ((periods == null) ? 0 : periods.hashCode());
		result = prime * result
				+ ((subjectCategory == null) ? 0 : subjectCategory.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((topics == null) ? 0 : topics.hashCode());
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
		if (!(obj instanceof Dataset)) {
            return false;
        }
		Dataset other = (Dataset) obj;
		if (boundaries == null) {
			if (other.boundaries != null) {
                return false;
            }
		} else if (!boundaries.equals(other.boundaries)) {
            return false;
        }
		if (creator == null) {
			if (other.creator != null) {
                return false;
            }
		} else if (!creator.equals(other.creator)) {
            return false;
        }
		if (description == null) {
			if (other.description != null) {
                return false;
            }
		} else if (!description.equals(other.description)) {
            return false;
        }
		if (familyId != other.familyId) {
            return false;
        }
		if (items == null) {
			if (other.items != null) {
                return false;
            }
		} else if (!items.equals(other.items)) {
            return false;
        }
		if (periods == null) {
			if (other.periods != null) {
                return false;
            }
		} else if (!periods.equals(other.periods)) {
            return false;
        }
		if (subjectCategory == null) {
			if (other.subjectCategory != null) {
                return false;
            }
		} else if (!subjectCategory.equals(other.subjectCategory)) {
            return false;
        }
		if (title == null) {
			if (other.title != null) {
                return false;
            }
		} else if (!title.equals(other.title)) {
            return false;
        }
		if (topics == null) {
			if (other.topics != null) {
                return false;
            }
		} else if (!topics.equals(other.topics)) {
            return false;
        }
		return true;
	}
}
