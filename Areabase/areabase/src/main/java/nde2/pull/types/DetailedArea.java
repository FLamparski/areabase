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

/**
 * This class of objects represents UK's administrative and geographical areas
 * in more detail. You want this in order to get the OS-grid encoded envelope
 * for this area if you want to use maps.
 * 
 * @author filip
 * @see {@link Area}
 */
@SuppressWarnings("SameParameterValue")
public class DetailedArea extends Area {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String extCode;
	private String envelope;
	private String mandatoryMetadata;
	private String optionalMetadata;

	public DetailedArea(String name, int areaId, int levelTypeId,
			int hierarchyId, String extCode, String envelope,
			String mandatoryMetadata, String optionalMetadata) {
		super(name, areaId, levelTypeId, hierarchyId);
		this.envelope = envelope;
		this.extCode = extCode;
		this.mandatoryMetadata = mandatoryMetadata;
		this.optionalMetadata = optionalMetadata;
	}

	public DetailedArea(Area area, String extCode, String envelope,
			String mandatoryMetadata, String optionalMetadata) {
		super(area);
		this.envelope = envelope;
		this.extCode = extCode;
		this.mandatoryMetadata = mandatoryMetadata;
		this.optionalMetadata = optionalMetadata;
	}

	public DetailedArea() {
	}

	/**
	 * @return The SNAC code for this area. Useful for Mapit.
	 */
	public String getExtCode() {
		return extCode;
	}

	/**
	 * @param extCode
	 *            the SNAC code to set
	 */
	public void setExtCode(String extCode) {
		this.extCode = extCode;
	}

	/**
	 * This is not a polygon wrapping the area by its borders; there's an
	 * OpenSpace API for that. This is just a rectangle that is just big enough
	 * to contain this area.
	 * 
	 * @return Ordnance Survey easting/northing envelope of this area.
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
	 * 
	 * @return The "mandatory" metadata for this area.
	 */
	public String getMandatoryMetadata() {
		return mandatoryMetadata;
	}

	/**
	 * @param mandatoryMetadata
	 *            the mandatoryMetadata to set
	 */
	public void setMandatoryMetadata(String mandatoryMetadata) {
		this.mandatoryMetadata = mandatoryMetadata;
	}

	/**
	 * @return the "optional" metadata
	 */
	public String getOptionalMetadata() {
		return optionalMetadata;
	}

	/**
	 * @param optionalMetadata
	 *            the optionalMetadata to set
	 */
	public void setOptionalMetadata(String optionalMetadata) {
		this.optionalMetadata = optionalMetadata;
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
				+ ((envelope == null) ? 0 : envelope.hashCode());
		result = prime * result + ((extCode == null) ? 0 : extCode.hashCode());
		result = prime
				* result
				+ ((mandatoryMetadata == null) ? 0 : mandatoryMetadata
						.hashCode());
		result = prime
				* result
				+ ((optionalMetadata == null) ? 0 : optionalMetadata.hashCode());
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
		if (!(obj instanceof DetailedArea)) {
            return false;
        }
		DetailedArea other = (DetailedArea) obj;
		if (envelope == null) {
			if (other.envelope != null) {
                return false;
            }
		} else if (!envelope.equals(other.envelope)) {
            return false;
        }
		if (extCode == null) {
			if (other.extCode != null) {
                return false;
            }
		} else if (!extCode.equals(other.extCode)) {
            return false;
        }
		if (mandatoryMetadata == null) {
			if (other.mandatoryMetadata != null) {
                return false;
            }
		} else if (!mandatoryMetadata.equals(other.mandatoryMetadata)) {
            return false;
        }
		if (optionalMetadata == null) {
			if (other.optionalMetadata != null) {
                return false;
            }
		} else if (!optionalMetadata.equals(other.optionalMetadata)) {
            return false;
        }
		return true;
	}

}
