package lamparski.areabase.widgets;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lamparski.areabase.R;
import nde2.helpers.CensusHelpers;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.DataSetItem;
import nde2.pull.types.Dataset;

/**
 * A {@link android.widget.BaseExpandableListAdapter} implementation that deals with the census
 * data -- it's the Controller bit of {@link lamparski.areabase.fragments.SubjectViewFragment}'s
 * MVC flow.
 *
 * @author filip
 * @see android.widget.BaseExpandableListAdapter
 * @see lamparski.areabase.fragments.SubjectViewFragment
 * @see nde2.pull.types.Dataset
 */
public class SubjectExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<DataSetFamily> toplevelItems;
    private Map<DataSetFamily, List<DataSetItem>> childItems;

    /**
     * Create a new Adapter for the context given.
     * @param context the Context to use with this Adapter
     * @param items Optionally pre-set the items
     */
    public SubjectExpandableListAdapter(Context context, @Nullable Map<DataSetFamily, Dataset> items){
        if(items != null){
            setItems(items);
        }
        mContext = context;
    }

    /**
     * Sets the new dataset. Please then call {@link #notifyDataSetChanged()}.
     * @param items the new dataset.
     */
    public void setItems(@Nonnull Map<DataSetFamily, Dataset> items){
        toplevelItems = new ArrayList<DataSetFamily>(items.keySet());
        childItems = new HashMap<DataSetFamily, List<DataSetItem>>();
        for(DataSetFamily k : toplevelItems) {
            Collection<DataSetItem> dsitems = items.get(k).getItems();
            if(dsitems != null) {
                childItems.put(k, new ArrayList<DataSetItem>(dsitems));
            }
        }
    }

    /**
     * @return the currently loaded dataset
     */
    public Map<DataSetFamily, List<DataSetItem>> getChildItems (){
        return childItems;
    }

    @Override
    public int getGroupCount() {
        return toplevelItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int realSize = childItems.get(toplevelItems.get(groupPosition)).size();
        // We need to accommodate for the chartlink for those datasets that can be graphed.
        return isGraphable(groupPosition) ? realSize + 1 : realSize;
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
        if(convertView == null || convertView.findViewById(R.id.subject_view_groupitem_title) == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.subject_view_groupitem, null);
        }

        DataSetFamily fam = toplevelItems.get(groupPosition);
        ((TextView) convertView.findViewById(R.id.subject_view_groupitem_title)).setText(fam.getName());
        ((TextView) convertView.findViewById(R.id.subject_view_groupitem_count)).setText(Integer.toString(childItems.get(fam).size()));
        ImageView indicator = (ImageView) convertView.findViewById(R.id.subject_view_groupitem_graphability_indicator);
        if(isGraphable(groupPosition)){
            indicator.setVisibility(View.VISIBLE);
        } else {
            indicator.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    /**
     * Generates a view for a dataset item
     * @param groupPosition the ID of the dataset
     * @param childPosition the ID of the item
     * @param isLastChild (framework) is it the last child in the list
     * @param convertView a view that can be recycled (however sometimes this might not be possible)
     * @param parent (framework) parent viewgroup
     * @return the item view
     */
    private View getRegularChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
        if(convertView == null || convertView.findViewById(R.id.subject_view_listitem_title) == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.subject_view_listitem, parent, false);
        }

        DataSetItem item = (DataSetItem) getChild(groupPosition, childPosition);
        if(item == null){
            Log.w("getRegularChildView",
                    String.format("item #%d for group #%d (%s) not found",
                            childPosition,
                            groupPosition,
                            toplevelItems.get(groupPosition).getName()));
            return null;
        }

        ((TextView) convertView.findViewById(R.id.subject_view_listitem_title)).setText(item.getTopic().getTitle());
        ((TextView) convertView.findViewById(R.id.subject_view_listitem_subtitle)).setText(item.getTopic().getDescription());
        String valueString = CensusHelpers.getValueString(item.getValue(), item.getTopic().getCoinageUnit());
        ((TextView) convertView.findViewById(R.id.subject_view_listitem_count)).setText(valueString);

        return convertView;
    }

    private View getChartLinkChildView(int groupPosition, View convertView, ViewGroup parent){
        if(convertView == null
                || convertView.findViewById(R.id.subject_view_listitem_chartlink_icon) == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.subject_view_listitem_chartlink, parent, false);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(isGraphable(groupPosition)){
            if(childPosition == 0){
                return getChartLinkChildView(groupPosition, convertView, parent);
            } else {
                return getRegularChildView(groupPosition, childPosition - 1, isLastChild, convertView, parent);
            }
        } else {
            return getRegularChildView(groupPosition, childPosition, isLastChild, convertView, parent);
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return (isGraphable(groupPosition) && childPosition == 0);
    }

    public boolean isGraphable(int groupPosition){
        int size = childItems.get(toplevelItems.get(groupPosition)).size();
        return size > 1;
    }
}
