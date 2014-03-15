package nde2.pull.methodcalls.delivery;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nde2.errors.InvalidParameterException;
import nde2.errors.NDE2Exception;
import nde2.pull.types.Area;
import nde2.pull.types.Dataset;

/**
 * Gets the child area tables.
 *
 * <p>
 *     In the current form, it doesn't work as intended, and other forms
 *     of supplying parameters may have to be used.
 * </p>
 *
 * @author filip
 */
public class GetChildAreaTables extends GetTables {
	private static final String METHOD_NAME = "getChildAreaTables";

	private int levelTypeId;

    /**
     * Specify a parent area
     * @param area the area to get child area tables for
     * @return modified object for currying
     */
	public GetChildAreaTables forParentArea(Area area) {
		areas.add(area);
		return this;
	}

    /**
     * Find children of this level type
     * @param levelTypeId level type id
     * @return modified object for currying
     */
	public GetChildAreaTables forChildrenOfLevelType(int levelTypeId) {
		this.levelTypeId = levelTypeId;
		return this;
	}

	@Override
	protected Map<String, String> collectParams()
			throws InvalidParameterException {
		Map<String, String> params = new HashMap<String, String>();
		collectParentAreaToParams(params);
		collectDatasetFamiliesToParams(params);
		collectDateRangeToParams(params);
		collectLevelTypeIdToParams(params);
		return params;
	}

	private void collectLevelTypeIdToParams(Map<String, String> params) {
		if (areas.iterator().next().getLevelTypeId() >= levelTypeId) {
			throw new InvalidParameterException("LevelTypeId",
					"The level type id of child areas must be lower than that of the parent.");
		}
		params.put("LevelTypeId", Integer.toString(levelTypeId));
	}

	private void collectParentAreaToParams(Map<String, String> params) {
		if (areas.size() != 1) {
			throw new InvalidParameterException("ParentArea",
					"You must specify exactly one area to query for.");
		}
		params.put("ParentAreaId",
				Integer.toString(areas.iterator().next().getAreaId()));
	}

	@Override
	protected XmlPullParser execute(Map<String, String> params)
			throws IOException, XmlPullParserException {
		return doCall(METHOD_NAME, params);
	}

	@Override
	public Set<Dataset> execute() throws InvalidParameterException,
			IOException, XmlPullParserException, NDE2Exception {
		XmlPullParser xpp = execute(collectParams());

		return processDataCubeResponseElement(xpp);
	}

	@Override
	public String toURLString() {
		return buildURLString(DELIVERY_ENDPOINT, METHOD_NAME, collectParams());
	}
}
