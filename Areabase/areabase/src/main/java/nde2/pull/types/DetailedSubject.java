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
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import nde2.errors.NDE2Exception;

/**
 * A more detailed representation of a subject. Has *two* descriptions!
 * 
 * @author filip
 * @see {@link Subject}
 */
public class DetailedSubject extends Subject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String description;
	private String moreDescription;

	/**
	 * @param name subject name
	 * @param id subject id
	 * @param description the description to set
	 * @param moreDescription additional information
	 */
	public DetailedSubject(String name, int id, String description,
			String moreDescription) {
		super(name, id);
		this.description = description;
		this.moreDescription = moreDescription;
	}

	public DetailedSubject(Subject basic, String description,
			String moreDescription) {
		super(basic);
		this.description = description;
		this.moreDescription = moreDescription;
	}

	public DetailedSubject() {
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
	 * @return the moreDescription
	 */
	public String getMoreDescription() {
		return moreDescription;
	}

	/**
	 * @param moreDescription
	 *            the moreDescription to set
	 */
	public void setMoreDescription(String moreDescription) {
		this.moreDescription = moreDescription;
	}

	/**
	 * @return Itself. It's already a DetailedSubject after all.
	 */
	@Override
	public DetailedSubject getDetailed() throws IOException,
			XmlPullParserException, NDE2Exception {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((moreDescription == null) ? 0 : moreDescription.hashCode());
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
		if (!super.equals(obj)) {
            return false;
        }
		if (!(obj instanceof DetailedSubject)) {
            return false;
        }
		DetailedSubject other = (DetailedSubject) obj;
		if (description == null) {
			if (other.description != null) {
                return false;
            }
		} else if (!description.equals(other.description)) {
            return false;
        }
		if (moreDescription == null) {
			if (other.moreDescription != null) {
                return false;
            }
		} else if (!moreDescription.equals(other.moreDescription)) {
            return false;
        }
		return true;
	}
}
