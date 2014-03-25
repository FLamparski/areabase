package lamparski.areabase.cards;
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
import java.util.ArrayList;

/**
 * An {@link java.util.ArrayList} that also emits an event when an item is added.
 * @param <E> Element type.
 */
public class EventfulArrayList<E> extends ArrayList<E> {
	private static final long serialVersionUID = 0xC0C4133L;
	private OnItemAddedListener addCb = new OnItemAddedListener() {
		@Override
		public boolean onItemAdded(Object item) {
			return true;
		}
	};

    /**
     * Gets activated when an item is added to the list. The handler
     * can actually veto the add.
     */
	public interface OnItemAddedListener {
		/**
		 * Called when an item is added to the list
		 * 
		 * @param item
		 *            The item being added
		 * @return false if you want to veto this.
		 */
		public boolean onItemAdded(Object item);
	}

	/**
     * In addition to adding an element, will also emit an event.
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(E object) {
		return addCb.onItemAdded(object) ? super.add(object) : false;
	}

    /**
     * In addition to adding an element, will also emit an event.
     * {@inheritDoc}
     */
	@Override
	public void add(int index, E object) {
		if (addCb.onItemAdded(object)) {
            super.add(index, object);
        }
	}

	public void setOnItemAddedListener(OnItemAddedListener listener) {
		addCb = listener;
	}

	public boolean addSilent(E object) {
		return super.add(object);
	}

}
