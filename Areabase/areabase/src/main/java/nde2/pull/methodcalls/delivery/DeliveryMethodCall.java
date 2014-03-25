package nde2.pull.methodcalls.delivery;
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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Map;

import nde2.pull.methodcalls.BaseMethodCall;

/**
 * A base class from which all Delivery method calls inherit.
 */
public abstract class DeliveryMethodCall extends BaseMethodCall {
	protected static final String DELIVERY_ENDPOINT = "http://neighbourhood.statistics.gov.uk/NDE2/Deli/";

    /**
     * Fills in the Endpoint of {@link nde2.pull.methodcalls.BaseMethodCall#doCall(String, String, java.util.Map)}.
     * @param method which method to call
     * @param params the parameters
     * @return an {@link org.xmlpull.v1.XmlPullParser}.
     * @throws IOException
     * @throws XmlPullParserException
     */
	@Override
	protected XmlPullParser doCall(String method, Map<String, String> params)
			throws IOException, XmlPullParserException {
		return super.doCall(DELIVERY_ENDPOINT, method, params);
	}
}
