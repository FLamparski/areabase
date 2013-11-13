package lamparski.areabase;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class NavDrawerListAdapter extends BaseAdapter {
	private final Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();
	private final ArrayAdapter<String> headers;
	public static final int TYPE_SECTION_HEADER = 0;

	public NavDrawerListAdapter(Context ctx) {
		headers = new ArrayAdapter<String>(ctx, R.layout.navbar_list_header);
	}

	public void addSection(String title, Adapter sectionAdapter) {
		sections.put(title, sectionAdapter);
		headers.add(title);
	}

	@Override
	public int getCount() {
		int total = 0;
		for (Adapter adapter : sections.values()) {
			total += adapter.getCount() + 1;
		}
		return total;
	}

	@Override
	public Object getItem(int position) {
		for (Object section : sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if the position is within this section
			if (position == 0)
				return section;
			if (position < size)
				return adapter.getItem(position - 1);

			// jump to next section
			position -= size;
		}
		throw new IndexOutOfBoundsException();
	}

	public int getViewTypeCount() {
		int total = 1; // headers are a type
		for (Adapter adapter : sections.values()) {
			total += adapter.getViewTypeCount() + 1;
		}
		return total;
	}

	public int getItemViewType(int position) {
		int type = 1;
		for (Object section : sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if the position is within this section
			if (position == 0)
				return TYPE_SECTION_HEADER;
			if (position < size)
				return type + adapter.getItemViewType(position - 1);

			// jump to next section
			position -= size;
			type += adapter.getViewTypeCount();
		}
		throw new IndexOutOfBoundsException();
	}

	public boolean areAllItemsSelectable() {
		return false;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public boolean isEnabled(int position) {
		return (getItemViewType(position) != TYPE_SECTION_HEADER);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int sectionnum = 0;
		for (Object section : sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position is within this section
			if (position == 0)
				return headers.getView(sectionnum, convertView, parent);
			if (position < size)
				return adapter.getView(position - 1, convertView, parent);

			position -= size;
			sectionnum++;
		}
		throw new IndexOutOfBoundsException();
	}

}
