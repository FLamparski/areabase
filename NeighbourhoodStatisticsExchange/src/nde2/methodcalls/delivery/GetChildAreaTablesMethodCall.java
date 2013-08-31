package nde2.methodcalls.delivery;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.types.delivery.Dataset;
import nde2.types.discovery.Area;
import nde2.types.discovery.DataSetFamily;
import nde2.types.discovery.VariableFamily;

import org.xml.sax.SAXException;

public class GetChildAreaTablesMethodCall extends GetTablesMethodCall {
	private final static String METHOD_NAME = "getChildAreaTables";
	private Area parentArea;
	private int levelTypeId = Area.LEVELTYPE_NULL;

	/**
	 * Behaviour changed from parent -- now it only takes the first area in
	 * areas. Try to use
	 * {@link GetChildAreaTablesMethodCall#addParentArea(Area)} instead.
	 */
	@Override
	@Deprecated
	public GetChildAreaTablesMethodCall addAreas(List<Area> areas) {
		this.parentArea = areas.get(0);
		return this;
	}

	public GetChildAreaTablesMethodCall addParentArea(Area area) {
		this.parentArea = area;
		return this;
	}

	public GetChildAreaTablesMethodCall addLevelTypeId(int levelTypeId) {
		this.levelTypeId = levelTypeId;
		return this;
	}

	@Override
	public List<Dataset> getTables() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception, NullPointerException, ParseException {
		/*
		 * Validate arguments for the remote method call, and build a parameter
		 * list.
		 */
		Map<String, String> params = new HashMap<String, String>();
		if (parentArea != null) {
			params.put("ParentAreaId", Long.toString(parentArea.getAreaId()));
		} else {
			// MUST have an area here.
			throw new NullPointerException("Must supply an Area");
		}
		if (dsFamilies != null) {
			StringBuilder dsFamilyListBuilder = new StringBuilder();
			for (DataSetFamily dsfam : dsFamilies) {
				// Not bothering about trailing commas - the server just ignores
				// them
				dsFamilyListBuilder.append(
						Integer.toString(dsfam.getFamilyId())).append(",");
			}
			params.put("Datasets", dsFamilyListBuilder.toString());
		}
		if (variableFamilies != null) {
			StringBuilder varFamilyListBuilder = new StringBuilder();
			for (VariableFamily varfam : variableFamilies) {
				// Not bothering about trailing commas - the server just ignores
				// them
				varFamilyListBuilder.append(
						Integer.toString(varfam.getVariableFamilyId())).append(
						",");
			}
			params.put("Variables", varFamilyListBuilder.toString());
		} else if (dsFamilies == null) {
			// MUST have at least one VariableFamily or DataSetFamily
			throw new NullPointerException(
					"Must supply at least one DataSetFamily or at least one VariableFamily or both.");
		}
		if (timePeriod != null) {
			SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			StringBuilder timePeriodBuilder = new StringBuilder()
					.append(mDateFormat.format(timePeriod.getStartDate()))
					.append(":")
					.append(mDateFormat.format(timePeriod.getEndDate()));
			params.put("TimePeriod", timePeriodBuilder.toString());
		}
		if (levelTypeId != Area.LEVELTYPE_NULL) {
			params.put("LevelTypeId", Integer.toString(levelTypeId));
		}

		return doCall(METHOD_NAME, params); // Let's hope nothing breaks
	}

	public List<Dataset> getChildAreaTables() throws XPathExpressionException,
			NullPointerException, ParserConfigurationException, SAXException,
			IOException, NDE2Exception, ParseException {
		return getTables();
	}

}
