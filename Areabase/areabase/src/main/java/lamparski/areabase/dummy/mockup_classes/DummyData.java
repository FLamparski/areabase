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
*//**
 * 
 */
package lamparski.areabase.dummy.mockup_classes;

/**
 * Indicates that whatever code is annotated by this is a placeholder or uses
 * dummy data.
 * 
 * @author filip
 * 
 */
public @interface DummyData {
	/**
	 * 
	 * @return The reason for using dummy data.
	 */
	String why() default "Will implement later";

	/**
	 * 
	 * @return What real data should be used.
	 */
	String replace_with() default "Something that does what it says on the tin.";
}
