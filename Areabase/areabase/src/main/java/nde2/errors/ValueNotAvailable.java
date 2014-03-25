package nde2.errors;
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
import nde2.pull.types.DataSetItem;

/**
 * This is not an exception. ValueNotAvailable is thrown whenever a value is
 * literally not available, as it was not returned by the server. In reality, it
 * is thrown when a {@link DataSetItem#getValue()} value is equal to
 * {@link Integer#MIN_VALUE}, which seems like a sensible choice for 32-bit or
 * higher systems.
 * <p>
 * <b>Handling:</b> Display a "N/A" inside the offending value's cell, or skip
 * this value when comparing things.
 * 
 * @author filip
 * 
 */
public class ValueNotAvailable extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 57513490567813L;

	public ValueNotAvailable() {
		super();
	}

	public ValueNotAvailable(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ValueNotAvailable(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public ValueNotAvailable(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
