package lamparski.areabase.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lamparski.areabase.R;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.DataSetItem;
import nde2.pull.types.Dataset;

/**
 * Created by filip on 17/02/14.
 */
public class SubjectExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<DataSetFamily> toplevelItems;
    private Map<DataSetFamily, List<DataSetItem>> childItems;

    public SubjectExpandableListAdapter(Context context, @Nullable Map<DataSetFamily, Dataset> items){
        if(items != null){
            setItems(items);
        }
        mContext = context;
    }

    public void setItems(@Nonnull Map<DataSetFamily, Dataset> items){
        assert items != null;
        toplevelItems = new ArrayList<DataSetFamily>(items.keySet());
        childItems = new HashMap<DataSetFamily, List<DataSetItem>>();
        for(DataSetFamily k : toplevelItems) {
            Collection<DataSetItem> dsitems = items.get(k).getItems();
            if(dsitems != null)
                childItems.put(k, new ArrayList<DataSetItem>(dsitems));
        }
    }

    public Map<DataSetFamily, List<DataSetItem>> getChildItems (){
        return childItems;
    }

    @Override
    public int getGroupCount() {
        return toplevelItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childItems.get(toplevelItems.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return toplevelItems.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childItems.get(toplevelItems.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.subject_view_groupitem, null);
        }

        DataSetFamily fam = toplevelItems.get(groupPosition);
        ((TextView) convertView.findViewById(R.id.subject_view_groupitem_title)).setText(fam.getName());
        ((TextView) convertView.findViewById(R.id.subject_view_groupitem_count)).setText(Integer.toString(childItems.get(fam).size()));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.subject_view_listitem, null);
        }

        DataSetItem item = (DataSetItem) getChild(groupPosition, childPosition);

        ((TextView) convertView.findViewById(R.id.subject_view_listitem_title)).setText(item.getTopic().getTitle());
        ((TextView) convertView.findViewById(R.id.subject_view_listitem_subtitle)).setText(item.getTopic().getDescription());
        float value = item.getValue();
        String coinageUnit = item.getTopic().getCoinageUnit();
        String valueString = "";
        if(coinageUnit.equals("Count"))
            valueString = String.format("%.0f", value);
        else if (coinageUnit.equals("Percentage"))
            valueString = String.format("%.1f%%", value);
        else if (coinageUnit.equals("Square metres (m2)(thousands)")) {
            value *= 1000;
            valueString = String.format("%.2f m²", value);
        } else if (coinageUnit.equals("Pounds Sterling (thousands)"))
            valueString = String.format("£%.3fk", value);
        else if (coinageUnit.equals("Pounds Sterling"))
            valueString = String.format("£%.2f", value);
        else if (coinageUnit.equals("Score"))
            valueString = String.format("%.1f", value);
        else
            valueString = String.format("%.1f %s", value, coinageUnit);
        ((TextView) convertView.findViewById(R.id.subject_view_listitem_count)).setText(valueString);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
