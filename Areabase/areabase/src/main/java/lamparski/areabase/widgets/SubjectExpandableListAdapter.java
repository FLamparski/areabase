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
            convertView = inflater.inflate(R.layout.subject_view_listitem, null);
        }

        DataSetFamily fam = toplevelItems.get(groupPosition);
        ((TextView) convertView.findViewById(R.id.subject_view_listitem_title)).setText(fam.getName());
        // Will need to get detailed versions?
        ((TextView) convertView.findViewById(R.id.subject_view_listitem_subtitle)).setText(fam.getName());
        ((TextView) convertView.findViewById(R.id.subject_view_listitem_count)).setText(Integer.toString(childItems.get(fam).size()));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.subject_view_listitem, null);
        }

        DataSetItem item = (DataSetItem) getChild(groupPosition, childPosition);
        String item_subtitle = String.format("%s -- %s", item.getTopic().getDescription(), item.getTopic().getCoinageUnit());
        ((TextView) convertView.findViewById(R.id.subject_view_listitem_title)).setText(item.getTopic().getTitle());
        ((TextView) convertView.findViewById(R.id.subject_view_listitem_subtitle)).setText(item_subtitle);
        ((TextView) convertView.findViewById(R.id.subject_view_listitem_count)).setText(String.format("%.2f", item.getValue()));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
