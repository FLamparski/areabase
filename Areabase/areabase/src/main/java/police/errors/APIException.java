package police.errors;
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
 * An exception that can be thrown in case of a REST-ful service error
 */
public class APIException extends Exception {

	private int httpCode = 0;

	/**
	 * 
	 */
	private static final long serialVersionUID = -705215153574849785L;

	/**
	 * 
	 */
	public APIException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message the message attached to this exception
	 * @param cause a throwable cause (usually another exception)
	 */
	public APIException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message the message attached to this exception
	 */
	public APIException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public APIException(String message, int httpCode) {
		super(message);
		this.httpCode = httpCode;
	}

	/**
	 * @param cause a throwable cause (usually another exception)
	 */
	public APIException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the http code returned by the service
	 */
	public int getHttpCode() {
		return httpCode;
	}

	/**
	 * @param httpCode
	 *            the http code returned by the service
	 */
	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}

}
