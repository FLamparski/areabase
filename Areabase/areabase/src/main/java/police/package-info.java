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
 * <p>A client for the UK Police Data API.</p>
 *
 * <p><a href="http://data.police.uk/docs/">See the API documentation</a></p>
 *
 * <p>The Police Data API allows its users to access crime data. It is a RESTful service,
 * and uses JSON to return data. Therefore, this code uses either the {@link com.google.gson.JsonParser} low-level
 * classes, or the {@link com.google.gson.Gson} reflection-based parser depending on where it makes sense.</p>
 */
package police;