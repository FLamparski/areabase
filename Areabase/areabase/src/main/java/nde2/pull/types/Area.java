package nde2.pull.types;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import nde2.errors.NDE2Exception;
import nde2.pull.methodcalls.discovery.GetAreaChildren;
import nde2.pull.methodcalls.discovery.GetAreaParent;
import nde2.pull.methodcalls.discovery.GetCompatibleSubjects;

/**
 * This class of objects represents geographic of administrative areas returned
 * by the NDE2 Discovery web service.
 * 
 * @see {@link DetailedArea} -- a version of the area that has slightly more
 *      data; you want this for the Ordnance Survey envelope.
 * @author filip
 * 
 */
public class Area implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int HIERARCHY_2011_STATISTICAL_GEOGRAPHY = 26;
	public static final int HIERARCHY_2001_STATISTICAL_GEOGRAPHY = 2;
	public static final int HIERARCHY_1998_ADMINISTRATIVE = 3;
	public static final int HIERARCHY_2003_ADMINISTRATIVE = 4;
	public static final int HIERARCHY_2003_ELECTORAL = 7;
	public static final int HIERARCHY_2003_HEALTH = 8;
	public static final int HIERARCHY_2003_PARISH = 9;
	public static final int HIERARCHY_2003_EDUCATION = 10;
	public static final int HIERARCHY_2004_ADMINISTRATIVE = 11;
	public static final int HIERARCHY_NEW_DEAL_FOR_COMMUNITIES_BEST_FIT = 12;
	public static final int HIERARCHY_PROVISIONAL_PARLIAMENTARY_CONSTITUENCIES_2007 = 14;
	public static final int HIERARCHY_2006_HEALTH = 15;
	public static final int HIERARCHY_2006_ADMINISTRATIVE = 17;
	public static final int HIERARCHY_2005_ADMINISTRATIVE = 16;
	public static final int HIERARCHY_2007_ADMINISTRATIVE = 18;
	public static final int HIERARCHY_2010_WESTMINSTER_PARLIAMENTARY_CONSTITUENCIES = 23;
	public static final int HIERARCHY_2008_ADMINISTRATIVE = 22;
	public static final int HIERARCHY_2010_ADMINISTRATIVE = 25;
	public static final int HIERARCHY_2009_ADMINISTRATIVE = 24;
	public static final int HIERARCHY_2011_ADMINISTRATIVE = 27;
	public static final int HIERARCHY_2011_PARISH = 29;
	public static final int HIERARCHY_2011_WESTMINSTER_PARLIAMENTARY_CONSTITUENCIES = 28;
	/**
	 * Denotes an invalid/"null" hierarchy
	 */
	public static final int HIERARCHY_NULL = -1;

	/**
	 * Community
	 */
	public static final int LEVELTYPE_COMMUNITY = 164;
	/**
	 * Country
	 */
	public static final int LEVELTYPE_COUNTRY = 10;
	/**
	 * County
	 */
	public static final int LEVELTYPE_COUNTY = 12;
	/**
	 * Education Area
	 */
	public static final int LEVELTYPE_EDUCATION_AREA = 180;
	/**
	 * England and Wales
	 */
	public static final int LEVELTYPE_ENGLAND_WALES = 9;
	/**
	 * Former county
	 */
	public static final int LEVELTYPE_FORMER_COUNTY = 96;
	/**
	 * ???
	 */
	public static final int LEVELTYPE_FLA = 251;
	/**
	 * Gazetteer
	 */
	public static final int LEVELTYPE_GAZ = 21;
	/**
	 * Gazetteer 2
	 */
	public static final int LEVELTYPE_GAZ2 = 259;
	/**
	 * Great Britain
	 */
	public static final int LEVELTYPE_GREAT_BRITAIN = 8;
	/**
	 * Government Office Region
	 */
	public static final int LEVELTYPE_GOV_OFFICE_REGION = 11;
	/**
	 * Health Authority
	 */
	public static final int LEVELTYPE_HEALTH_AUTHORITY = 19;
	/**
	 * Local Authority
	 */
	public static final int LEVELTYPE_LOCAL_AUTHORITY = 13;
	/**
	 * Lower layer super output area
	 */
	public static final int LEVELTYPE_LSOA = 141;
	/**
	 * Middle layer super output area
	 */
	public static final int LEVELTYPE_MSOA = 140;
	/**
	 * New Deal for Community
	 */
	public static final int LEVELTYPE_NEW_DEAL_FOR_COMMUNITY = 201;
	/**
	 * National Park
	 */
	public static final int LEVELTYPE_NATIONAL_PARK = 17;
	/**
	 * Output area
	 */
	public static final int LEVELTYPE_OA = 15;
	/**
	 * Parish
	 */
	public static final int LEVELTYPE_PARISH = 16;
	/**
	 * Postcode area
	 */
	public static final int LEVELTYPE_POSTCODE_AREA = 25;
	/**
	 * Postcode
	 */
	public static final int LEVELTYPE_POSTCODE = 22;
	/**
	 * Postcode 2
	 */
	public static final int LEVELTYPE_POSTCODE2 = 257;
	/**
	 * Primary Care Organisation
	 */
	public static final int LEVELTYPE_PRIMARY_CARE_ORG = 20;
	/**
	 * Postcode district
	 */
	public static final int LEVELTYPE_POSTCODE_DISTRICT = 24;
	/**
	 * Postcode sector
	 */
	public static final int LEVELTYPE_POSTCODE_SECTOR = 23;
	/**
	 * Regional office
	 */
	public static final int LEVELTYPE_REGIONAL_OFFICE = 97;
	/**
	 * Strategic Health Authority
	 */
	public static final int LEVELTYPE_STRATEGIC_HEALTH_AUTHORITY = 81;
	/**
	 * Statistical neighbourhood
	 */
	public static final int LEVELTYPE_STAT_NEIGHBOURHOOD = 143;
	/**
	 * Scottish Parliamentary region
	 */
	public static final int LEVELTYPE_SCOTTISH_PARL_REGION = 144;
	/**
	 * Urban area
	 */
	public static final int LEVELTYPE_URBAN_AREA = 28;
	/**
	 * United Kingdom -- England, Scotland, Wales, N Ireland
	 */
	public static final int LEVELTYPE_UK = 7;
	/**
	 * Urban subdivision
	 */
	public static final int LEVELTYPE_URBAN_SUBDIV = 29;
	/**
	 * Upper layer super output area
	 */
	public static final int LEVELTYPE_USOA = 153;
	/**
	 * Ward
	 */
	public static final int LEVELTYPE_WARD = 14;
	/**
	 * Westminster Parliamentary constituency
	 */
	public static final int LEVELTYPE_WESTMINSTER_PARL_CONSTITUENCY = 27;
	/**
	 * Denotes an invalid/"null" leveltype
	 */
	public static final int LEVELTYPE_NULL = -1;

	private String name;
	private int areaId;
	private int levelTypeId;
	private int hierarchyId;
	private Area parent;
	private Set<Area> children;
	private Map<Subject, Integer> compatibleSubjects;

	public Area(String name, int areaId, int levelTypeId, int hierarchyId) {
		this.name = name;
		this.areaId = areaId;
		this.levelTypeId = levelTypeId;
		this.hierarchyId = hierarchyId;
		children = null;
		compatibleSubjects = null;
	}

	protected Area(Area copy) {
		this.name = copy.name;
		this.areaId = copy.areaId;
		this.levelTypeId = copy.levelTypeId;
		this.hierarchyId = copy.hierarchyId;
		this.parent = copy.parent;
		this.children = copy.children;
	}

	public Area() {
	}

	/**
	 * 
	 * @return The area's proper name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return The area's internal ID
	 */
	public int getAreaId() {
		return areaId;
	}

	/**
	 * Comparable to Area.LEVELTYPE_ constants.
	 * 
	 * @return This area's administrative level type.
	 */
	public int getLevelTypeId() {
		return levelTypeId;
	}

	/**
	 * Comparable to Area.HIERARCHY_ constants.
	 * 
	 * @return This area's administrative hierarchy ID
	 */
	public int getHierarchyId() {
		return hierarchyId;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param areaId
	 *            the areaId to set
	 */
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	/**
	 * @param levelTypeId
	 *            the levelTypeId to set
	 */
	public void setLevelTypeId(int levelTypeId) {
		this.levelTypeId = levelTypeId;
	}

	/**
	 * @param hierarchyId
	 *            the hierarchyId to set
	 */
	public void setHierarchyId(int hierarchyId) {
		this.hierarchyId = hierarchyId;
	}

	/**
	 * Set this area's parent (containing area).
	 * 
	 * @param parent
	 *            This area's parent/containing area.
	 */
	public void setParent(Area parent) {
		this.parent = parent;
	}

	/**
	 * 
	 * @return A Set of child areas, fetching the information from NeSS if
	 *         necessary.
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws NDE2Exception
	 */
	public Set<Area> getChildren() throws IOException, XmlPullParserException,
			NDE2Exception {
		if (children == null) {
			children = new GetAreaChildren(this).execute();
		}
		return children;
	}

	/**
	 * 
	 * @return The parent area of this area.
	 * @throws NDE2Exception
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public Area getParent() throws IOException, XmlPullParserException,
			NDE2Exception {
		if (parent == null) {
			parent = new GetAreaParent(this).execute();
		}
		return parent;
	}

	/**
	 * @return {@link Subject Subjects} that are compatible with this area.
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws NDE2Exception
	 */
	public Map<Subject, Integer> getCompatibleSubjects() throws IOException,
			XmlPullParserException, NDE2Exception {
		if (compatibleSubjects == null) {
            {
                compatibleSubjects = new GetCompatibleSubjects(this).execute();
            }
        }
		return compatibleSubjects;
	}

	/**
	 * @param compatibleSubjects
	 *            {@link Subject Subjects} to set as compatible with this area.
	 */
	public void setCompatibleSubjects(Map<Subject, Integer> compatibleSubjects) {
		this.compatibleSubjects = compatibleSubjects;
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
		result = prime * result + (int) (areaId ^ (areaId >>> 32));
		result = prime * result + hierarchyId;
		result = prime * result + levelTypeId;
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
		if (!(obj instanceof Area)) {
            return false;
        }
		Area other = (Area) obj;
		if (areaId != other.areaId) {
            return false;
        }
		if (hierarchyId != other.hierarchyId) {
            return false;
        }
		if (levelTypeId != other.levelTypeId) {
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
