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
public class InvalidParameterException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String parameterName;

	/**
	 * 
	 */
	public InvalidParameterException() {
		super();
		parameterName = "(unspecified)";
	}

	/**
	 * @param message explanation for the exception
	 * @param cause another throwable that caused this exception
	 */
	public InvalidParameterException(String message, Throwable cause) {
		super(message, cause);
		parameterName = "(unspecified)";
	}

	/**
	 * @param message explanation for the exception
	 */
	public InvalidParameterException(String message) {
		super(message);
		parameterName = "(unspecified)";
	}

	/**
	 * @param cause another throwable that caused this exception
	 */
	public InvalidParameterException(Throwable cause) {
		super(cause);
		parameterName = "(unspecified)";
	}

    /**
     *
     * @param parameterName the parameter that was set to something invalid
     * @param message explanation for the exception
     */
	public InvalidParameterException(String parameterName, String message) {
		super(message);
		this.parameterName = parameterName;
	}

    /**
     *
     * @param parameterName the parameter that was set to something invalid
     * @param message explanation for the exception
     * @param cause another throwable that caused this exception
     */
	public InvalidParameterException(String parameterName, String message,
			Throwable cause) {
		super(message, cause);
		this.parameterName = parameterName;
	}

	/**
	 * @return the invalid parameter's name
	 */
	public String getParameterName() {
		return parameterName;
	}

}
