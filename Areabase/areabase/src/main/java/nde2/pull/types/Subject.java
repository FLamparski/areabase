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
import java.io.Serializable;

import nde2.errors.NDE2Exception;
import nde2.pull.methodcalls.discovery.GetSubjectDetail;

/**
 * Represents a subject in the NDE database.
 * 
 * @author filip
 * @see {@link DetailedSubject}
 * 
 */
public class Subject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private int id;

	public Subject(String name, int id) {
		this.name = name;
		this.id = id;
	}

	protected Subject(Subject copy) {
		this.name = copy.name;
		this.id = copy.id;
	}

	public Subject() {
	}

	/**
	 * 
	 * @return The subject's proper name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return The subject's ID, used for NDE querying.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 
	 * @return A detailed representation of this Subject, containing description
	 *         and explanation of the subject.
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws NDE2Exception
	 */
	public DetailedSubject getDetailed() throws IOException,
			XmlPullParserException, NDE2Exception {
		return new GetSubjectDetail(this).execute();
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
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (!(obj instanceof Subject)) {
            return false;
        }
		Subject other = (Subject) obj;
		if (id != other.id) {
            return false;
        }
		if (name == null) {
			if (other.name != null) {
                return false;
            }
		} else if (!name.equals(other.name)) {
            return false;
        }
		return true;
	}

}
