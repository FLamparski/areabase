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

public class Boundary implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String boundaryCode;
	private String envelope;
	private int identifier;
	private int __delivery_getTables_boundaryPointer;
	private String title;

	public Boundary() {
	}

	/**
	 * @return the boundaryCode
	 */
	public String getBoundaryCode() {
		return boundaryCode;
	}

	/**
	 * @param boundaryCode
	 *            the boundaryCode to set
	 */
	public void setBoundaryCode(String boundaryCode) {
		this.boundaryCode = boundaryCode;
	}

	/**
	 * @return the envelope
	 */
	public String getEnvelope() {
		return envelope;
	}

	/**
	 * @param envelope
	 *            the envelope to set
	 */
	public void setEnvelope(String envelope) {
		this.envelope = envelope;
	}

	/**
	 * @return the identifier
	 */
	public int getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier
	 *            the identifier to set
	 */
	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public int get__pointer() {
		return __delivery_getTables_boundaryPointer;
	}

	public void set__pointer(int __delivery_getTables_boundaryPointer) {
		this.__delivery_getTables_boundaryPointer = __delivery_getTables_boundaryPointer;
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
				+ ((boundaryCode == null) ? 0 : boundaryCode.hashCode());
		result = prime * result
				+ ((envelope == null) ? 0 : envelope.hashCode());
		result = prime * result + identifier;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		if (!(obj instanceof Boundary)) {
            return false;
        }
		Boundary other = (Boundary) obj;
		if (boundaryCode == null) {
			if (other.boundaryCode != null) {
                return false;
            }
		} else if (!boundaryCode.equals(other.boundaryCode)) {
            return false;
        }
		if (envelope == null) {
			if (other.envelope != null) {
                return false;
            }
		} else if (!envelope.equals(other.envelope)) {
            return false;
        }
		if (identifier != other.identifier) {
            return false;
        }
		if (title == null) {
			if (other.title != null) {
                return false;
            }
		} else if (!title.equals(other.title)) {
            return false;
        }
		return true;
	}

}
