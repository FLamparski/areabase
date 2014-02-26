package lamparski.areabase.cards;

import java.util.ArrayList;

public class EventfulArrayList<E> extends ArrayList<E> {
	private static final long serialVersionUID = 0xC0C4133L;
	private OnItemAddedListener addCb = new OnItemAddedListener() {
		@Override
		public boolean onItemAdded(Object item) {
			return true;
		}
	};

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	@Override
	public boolean add(E object) {
		return addCb.onItemAdded(object) ? super.add(object) : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#add(int, java.lang.Object)
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
