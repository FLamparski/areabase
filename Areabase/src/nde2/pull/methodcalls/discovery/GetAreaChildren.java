package nde2.pull.methodcalls.discovery;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nde2.errors.NDE2Exception;
import nde2.pull.types.Area;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * <i>Encapsulates the GetAreaChildren() call to the NDE2 web service. Creation
 * follows the Builder pattern.</i>
 * 
 * <p>
 * This operation allows you to get the lower level areas for a supplied area,
 * regardless of level type. Only those areas immediately below the supplied
 * area are returned. For example, the child areas of a region would be the
 * counties and unitary authorities within that region. Repeatedly calling this
 * operation allows you drill down through area hierarchies.
 * 
 * @author filip
 * 
 */
public class GetAreaChildren extends DiscoveryMethodCall {
	private static final String METHOD_NAME = "GetAreaChildren";
	private Area parentArea;

	public GetAreaChildren(Area parent) {
		this.parentArea = parent;
	}

	@Override
	protected XmlPullParser execute(Map<String, String> params)
			throws IOException, XmlPullParserException {
		return doCall(METHOD_NAME, params);
	}

	@Override
	protected Map<String, String> collectParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("AreaId", Integer.toString(parentArea.getAreaId()));
		return params;
	}

	/**
	 * 
	 * @return A list of children {@link Area}s for the parent {@link Area}
	 *         supplied.
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws NDE2Exception
	 */
	public Set<Area> execute() throws IOException, XmlPullParserException,
			NDE2Exception {
		XmlPullParser xpp = execute(collectParams());
		Set<Area> childSet = processAreaSet(xpp);
		for (Area child : childSet) {
			child.setParent(parentArea);
		}
		return childSet;
	}

	@Override
	public String toURLString() {
		return buildURLString(DISCOVERY_ENDPOINT, METHOD_NAME, collectParams());
	}

}
