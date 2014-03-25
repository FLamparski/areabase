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
/**
 * Note that this is not the exact location of the crime, but rather
 * an arbitrary grid point closest to the actual location
 */
public class Location {
	private String latitude;
	private String longitude;
	private Street street;

	@SuppressWarnings("unused")
	private Location() {
	}

	/**
	 * @param latitude latitude of the normalised crime location
	 * @param longitude longitude of the normalised crime location
	 * @param street street name
	 */
	public Location(String latitude, String longitude, Street street) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.street = street;
	}

	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}

	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}

	/**
	 * @return the street
	 */
	public Street getStreet() {
		return street;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Location [latitude=" + latitude + ", longitude=" + longitude
				+ ", street=" + street.toString() + "]";
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
				+ ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result
				+ ((longitude == null) ? 0 : longitude.hashCode());
		result = prime * result + ((street == null) ? 0 : street.hashCode());
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
		if (!(obj instanceof Location)) {
            return false;
        }
		Location other = (Location) obj;
		if (latitude == null) {
			if (other.latitude != null) {
                return false;
            }
		} else if (!latitude.equals(other.latitude)) {
            return false;
        }
		if (longitude == null) {
			if (other.longitude != null) {
                return false;
            }
		} else if (!longitude.equals(other.longitude)) {
            return false;
        }
		if (street == null) {
			if (other.street != null) {
                return false;
            }
		} else if (!street.equals(other.street)) {
            return false;
        }
		return true;
	}

}
