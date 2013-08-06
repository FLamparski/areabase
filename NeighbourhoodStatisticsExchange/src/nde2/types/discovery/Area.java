package nde2.types.discovery;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.methodcalls.discovery.GetAreaChildrenMethodCall;
import nde2.methodcalls.discovery.GetAreaDetailMethodCall;
import nde2.methodcalls.discovery.GetAreaParentMethodCall;
import nde2.methodcalls.discovery.GetCompatibleSubjectsMethodCall;
import nde2.types.NDE2Result;

import org.xml.sax.SAXException;

/**
 * This class of objects represents geographic of administrative areas returned
 * by the NDE2 Discovery web service.
 * 
 * @see {@link DetailedArea} -- a version of the area that has slightly more
 *      data; you want this for the Ordnance Survey envelope.
 * @author filip
 * 
 */
public class Area extends NDE2Result {
	/**
	 * 
	 */
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
	public static final int LEVELTYPE_COM = 164;
	/**
	 * Country
	 */
	public static final int LEVELTYPE_CTRY = 10;
	/**
	 * County
	 */
	public static final int LEVELTYPE_CTY = 12;
	/**
	 * Education Area
	 */
	public static final int LEVELTYPE_EA = 180;
	/**
	 * England and Wales
	 */
	public static final int LEVELTYPE_EW = 9;
	/**
	 * Former county
	 */
	public static final int LEVELTYPE_FCTY = 96;
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
	public static final int LEVELTYPE_GB = 8;
	/**
	 * Government Office Region
	 */
	public static final int LEVELTYPE_GOR = 11;
	/**
	 * Health Authority
	 */
	public static final int LEVELTYPE_HA = 19;
	/**
	 * Local Authority
	 */
	public static final int LEVELTYPE_LA = 13;
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
	public static final int LEVELTYPE_NDC = 201;
	/**
	 * National Park
	 */
	public static final int LEVELTYPE_NP = 17;
	/**
	 * Output area
	 */
	public static final int LEVELTYPE_OA = 15;
	/**
	 * Parish
	 */
	public static final int LEVELTYPE_P = 16;
	/**
	 * Postcode area
	 */
	public static final int LEVELTYPE_PA = 25;
	/**
	 * Postcode
	 */
	public static final int LEVELTYPE_PCD = 22;
	/**
	 * Postcode 2
	 */
	public static final int LEVELTYPE_PCD2 = 257;
	/**
	 * Primary Care Organisation
	 */
	public static final int LEVELTYPE_PCO = 20;
	/**
	 * Postcode district
	 */
	public static final int LEVELTYPE_PD = 24;
	/**
	 * Postcode sector
	 */
	public static final int LEVELTYPE_PS = 23;
	/**
	 * Regional office
	 */
	public static final int LEVELTYPE_RO = 97;
	/**
	 * Strategic Health Authority
	 */
	public static final int LEVELTYPE_SHA = 81;
	/**
	 * Statistical neighbourhood
	 */
	public static final int LEVELTYPE_SN = 143;
	/**
	 * Scottish Parliamentary region
	 */
	public static final int LEVELTYPE_SPR = 144;
	/**
	 * Urban area
	 */
	public static final int LEVELTYPE_U = 28;
	/**
	 * United Kingdom -- England, Scotland, Wales, N Ireland
	 */
	public static final int LEVELTYPE_UK = 7;
	/**
	 * Urban subdivision
	 */
	public static final int LEVELTYPE_USD = 29;
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
	public static final int LEVELTYPE_WPC = 27;
	/**
	 * Denotes an invalid/"null" leveltype
	 */
	public static final int LEVELTYPE_NULL = -1;

	private String name;
	private long areaId;
	private int levelTypeId;
	private int hierarchyId;
	private Area parent;
	private List<Area> children;
	private Map<Subject, Integer> compatibleDatasets;

	public Area(String name, long areaId, int levelTypeId, int hierarchyId) {
		this.name = name;
		this.areaId = areaId;
		this.levelTypeId = levelTypeId;
		this.hierarchyId = hierarchyId;
		children = null;
		compatibleDatasets = null;
	}

	protected Area(Area copy) {
		this.name = copy.name;
		this.areaId = copy.areaId;
		this.levelTypeId = copy.levelTypeId;
		this.hierarchyId = copy.hierarchyId;
		this.parent = copy.parent;
		this.children = copy.children;
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
	public long getAreaId() {
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
	 * Set this area's parent (containing area). Note that this should not be
	 * used for user code unless absolutely needed.
	 * 
	 * @param parent
	 *            This area's parent/containing area.
	 */
	public void setParent(Area parent) {
		this.parent = parent;
	}

	/**
	 * <i>Note:</i> If the Area was obtained in a way different from postcoding
	 * it, it may not have a parent initialised. This means it'll have to fetch
	 * it. Some parents are skipped. Ask NDE2.
	 * <p>
	 * If the parent has been set by setParent(), then that object is returned
	 * instead.
	 * 
	 * @return This {@link Area}'s parent
	 * @throws NDE2Exception
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 * @throws ValueNotAvailable
	 */
	public Area getParent() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception, ValueNotAvailable {
		if (parent == null) {
			parent = new GetAreaParentMethodCall().addArea(this)
					.getAreaParent();
		}
		return parent;
	}

	/**
	 * <i>Note:</i> Areas are not initialised with children. First call to this
	 * method may take a while. Use asynchronously.
	 * 
	 * @return This {@link Area}'s children (one level)
	 * @throws NDE2Exception
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 * @throws ValueNotAvailable
	 */
	public List<Area> getChildren() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception, ValueNotAvailable {
		if (children == null)
			children = new GetAreaChildrenMethodCall().addArea(this)
					.getAreaChildren();
		return children;
	}

	/**
	 * <i>Note:</i> This method may take a while. Use asynchronously.
	 * 
	 * @return A {@link DetailedArea} object which is essentially an
	 *         {@link Area} with more information attached. Useful for Ordnance
	 *         Survey eastings/northings, also includes metadata if available.
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws NDE2Exception
	 */
	public DetailedArea getDetailed() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception {
		return new GetAreaDetailMethodCall().addArea(this).getAreaDetail();
	}

	/**
	 * <i>Note:</i> Areas are not initialised with compatible subjects. First
	 * call to this method may take a while. Use asynchronously.
	 * 
	 * 
	 * 
	 * @return A list of compatible {@link Subject}s, together with their count
	 *         as a {@link Map}, where the Subject is the key, and the count is
	 *         the value.
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws NDE2Exception
	 */
	public Map<Subject, Integer> getCompatibleSubjects()
			throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException, NDE2Exception {
		if (compatibleDatasets == null)
			compatibleDatasets = new GetCompatibleSubjectsMethodCall().addArea(
					this).getCompatibleSubjects();
		return compatibleDatasets;
	}

}
