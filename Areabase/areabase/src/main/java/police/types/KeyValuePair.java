package police.types;
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
import java.util.Map;
import java.util.Map.Entry;

/**
 * A simple key-value pair for when a {@link Map} would be a waste of memory.
 * Used here to allow certain functions to return two values of different types.
 * 
 * @author filip
 * 
 * @param <K>
 *            The type of the key.
 * @param <V>
 *            The type of the value.
 */
public class KeyValuePair<K, V> implements Entry<K, V> {

	private K key;
	private V value;

	/**
	 * @param key the "key" part of this KV pair
	 * @param value the "value" part of this KV pair
	 */
	public KeyValuePair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		// TODO Auto-generated method stub
		return key;
	}

	@Override
	public V getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public V setValue(V value) {
		V oldValue = this.value;
		this.value = value;
		return oldValue;
	}

}
