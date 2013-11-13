package lamparski.areabase;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavDrawerSectionAdapter extends
		ArrayAdapter<NavDrawerListItemModel> {

	public NavDrawerSectionAdapter(Context context,
			List<NavDrawerListItemModel> objects) {
		super(context, R.layout.navbar_list_item, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView = convertView;
		// Initialise the view if empty
		if (itemView == null) {
			itemView = ((LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE)).inflate(
					R.layout.navbar_list_item, parent, false);
		}

		// Get the object to work with
		NavDrawerListItemModel model = getItem(position);

		// Fill the view:
		ImageView iconView = (ImageView) itemView
				.findViewById(R.id.navbar_list_item_navicon);
		iconView.setImageResource(model.getIconId());
		TextView caption = (TextView) itemView
				.findViewById(R.id.navbar_list_item_txt_item);
		caption.setText(model.getStringId());

		return itemView;
	}
}