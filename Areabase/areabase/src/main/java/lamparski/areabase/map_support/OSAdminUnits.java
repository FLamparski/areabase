package lamparski.areabase.map_support;
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
@Deprecated
public enum OSAdminUnits {
	/*
	 * @formatter:off
	 */
	COUNTY("County", "CTY"),
	COUNTY_ELECTORAL_DIVISION("County Electoral Division", "CED"),
	DISTRICT("District", "DIS"),
	DISTRICT_WARD("District Ward", "DIW"),
	EUROREGION("European Region", "EUR"),
	GREATER_LONDON_AUTHORITY("Greater London Authority", "GLA"),
	GLA_ASSEMBLY_CONSTITUENCY("Greater London Authority Assembly Constituency", "LAC"),
	LONDON_BOROUGH("London Borough", "LBO"),
	LONDON_BOROUGH_WARD("London Borough Ward", "LBW"),
	METROPOLITAN_DISTRICT("Metropolitan District", "MTD"),
	METROPOLITAN_WARD("Metropolitan Ward", "MTW"),
	SCOTTISH_PARLIAMENT_ELECTORAL_REGION("Scottish Parliament Electoral Region", "SPE"),
	SCOTTISH_PARLIAMENT_CONSTITUENCY("Scottish Parliament Constituency", "SPC"),
	UNITARY_AUTHORITY("Unitary Authority", "UTA"),
	UA_ELECTORAL_DIVISION("Unitary Authority Electoral Division", "UTE"),
	UA_WARD("Unitary Authority Ward", "UTW"),
	WELSH_ASSEMBLY_ELECTORAL_REGION("Welsh Assembly Electoral Region", "WAE"),
	WELSH_ASSEMBLY_CONSTITUENCY("Welsh Assembly Constituency", "WAC"),
	WESTMINSTER_CONSTITUENCY("Westminster Constituency", "WMC");
	/*
	 * @formatter:on
	 */
	private final String name;
	private final String id;

	OSAdminUnits(String name, String id) {
		this.id = id;
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
}
