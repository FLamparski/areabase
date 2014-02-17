package lamparski.areabase.map_support;

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
