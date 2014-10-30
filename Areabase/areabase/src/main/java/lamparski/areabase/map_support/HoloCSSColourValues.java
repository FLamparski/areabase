package lamparski.areabase.map_support;
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
public enum HoloCSSColourValues {
	CYAN("#33B5E5"), VIOLET("#AA66CC"), LIME("#99CC00"), YELLOW("#FFBB33"), PINK(
			"#FF4444"), AQUAMARINE("#0099CC"), PURPLE("#9933CC"), GREEN(
			"#669900"), ORANGE("#FF8800"), RED("#CC0000");

	private String cssColorValue;

	private HoloCSSColourValues(String cssValue) {
		this.cssColorValue = cssValue;
	}

	public String getCssValue() {
		return cssColorValue;
	}
}