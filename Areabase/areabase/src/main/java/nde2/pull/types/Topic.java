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

/**
 * A column in a {@link Dataset}. To get values that belong to this column from
 * a data set, use {@link Dataset#getItems(Topic)}
 * 
 * @author filip
 * 
 */
public class Topic implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int __delivery_getTables_topicPointer;
	private int topicId;
	private int datasetFamilyId;
	private String creator;
	private String description;
	private String title;
	private String coinageUnit;

	public Topic() {
	}

	/**
	 * <b>Do not use</b>: If you need a primary-key ID of this Topic, use
	 * {@link Topic#getTopicCode()} instead. This is only useful when ordering
	 * topics during the creation of a {@link Dataset}.
	 * 
	 * @return the ID element of the Topic that is used in the response
	 *         document. Actually an internal pointer that links topics and
	 *         items together when SOAP serialized.
	 */
	public int get__pointer() {
		return __delivery_getTables_topicPointer;
	}

	/**
	 * @return the ID of this topic
	 */
	public int getTopicId() {
		return topicId;
	}

	/**
	 * @return the identifier -- which is also the id of the
	 *         {@link DataSetFamily} this Topic is in.
	 */
	public int getDatasetFamilyId() {
		return datasetFamilyId;
	}

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the the unit in which this value is presented. Count, Percentage,
	 *         etc.
	 */
	public String getCoinageUnit() {
		return coinageUnit;
	}

	/**
	 * @param the
	 *            __pointer to set. explanation in get__pointer().
	 */
	public void set__pointer(int __delivery_getTables_topicPointer) {
		this.__delivery_getTables_topicPointer = __delivery_getTables_topicPointer;
	}

	/**
	 * @param topicId
	 *            the topicCode to set
	 */
	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	/**
	 * @param identifier
	 *            the identifier to set
	 */
	public void setDatasetFamilyId(int identifier) {
		this.datasetFamilyId = identifier;
	}

	/**
	 * @param creator
	 *            the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param coinageUnit
	 *            the coinageUnit to set
	 */
	public void setCoinageUnit(String coinageUnit) {
		this.coinageUnit = coinageUnit;
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
				+ ((coinageUnit == null) ? 0 : coinageUnit.hashCode());
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + datasetFamilyId;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + topicId;
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
		if (!(obj instanceof Topic)) {
            return false;
        }
		Topic other = (Topic) obj;
		if (coinageUnit == null) {
			if (other.coinageUnit != null) {
                return false;
            }
		} else if (!coinageUnit.equals(other.coinageUnit)) {
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
		if (datasetFamilyId != other.datasetFamilyId) {
            return false;
        }
		if (title == null) {
			if (other.title != null) {
                return false;
            }
		} else if (!title.equals(other.title)) {
            return false;
        }
		if (topicId != other.topicId) {
            return false;
        }
		return true;
	}

}
