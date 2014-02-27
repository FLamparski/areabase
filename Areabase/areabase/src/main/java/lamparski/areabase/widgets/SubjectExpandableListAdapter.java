package lamparski.areabase.widgets;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
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

    public SubjectExpandableListAdapter(Context context, @Nullable Map<DataSetFamily, Dataset> items){
        if(items != null){
            setItems(items);
        }
        mContext = context;
    }

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

    public Map<DataSetFamily, List<DataSetItem>> getChildItems (){
        return childItems;
    }

    @Override
    public int getGroupCount() {
        return toplevelItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childItems.get(toplevelItems.get(groupPosition)).size() + 1;
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

        return convertView;
    }

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
        String valueString = getValueString(item.getValue(), item.getTopic().getCoinageUnit());
        ((TextView) convertView.findViewById(R.id.subject_view_listitem_count)).setText(valueString);

        return convertView;
    }

    private String getValueString(float value, String coinageUnit) {
        String valueString = "";
        if(coinageUnit.equals("Count")) {
            valueString = String.format("%.0f", value);
        } else if (coinageUnit.equals("Percentage")) {
            valueString = String.format("%.1f%%", value);
        } else if (coinageUnit.equals("Square metres (m2)(thousands)")) {
            value *= 1000;
            valueString = String.format("%.2f m²", value);
        } else if (coinageUnit.equals("Pounds Sterling (thousands)")) {
            valueString = String.format("£%.3fk", value);
        } else if (coinageUnit.equals("Pounds Sterling")) {
            valueString = String.format("£%.2f", value);
        } else if (coinageUnit.equals("Score")) {
            valueString = String.format("%.1f", value);
        } else {
            valueString = String.format("%.1f %s", value, coinageUnit);
        }
        return valueString;
    }

    private View inflateChartView(ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return inflater.inflate(R.layout.subject_view_listitem_chart, parent, false);
    }

    private View getChartChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Log.d("getChartChildView",
                String.format("groupPosition = %d, childPosition = %d", groupPosition, childPosition));
        if(convertView == null){
            Log.v("getChartChildView", "convertView is null, inflating...");
            convertView = inflateChartView(parent);
        } else {
            Log.v("getChartChildView", "recycling a chart child view");
        }
        List<DataSetItem> dataset = childItems.get(toplevelItems.get(groupPosition));
        BarGraph chart = (BarGraph) convertView.findViewById(R.id.subject_view_listitem_bargraph);
        if(chart == null){
            Log.w("getChartChildView", "cannot get the chart element, reinflating view");
            convertView = inflateChartView(parent);
            chart = (BarGraph) convertView.findViewById(R.id.subject_view_listitem_bargraph);
        }
        ArrayList<Bar> bars = new ArrayList<Bar>();
        for(DataSetItem item : dataset){
            String title = item.getTopic().getTitle();
            if(!(title.contains("All People") || title.contains("All Usual Residents"))){
                Bar itemBar = new Bar();
                itemBar.setName(title);
                itemBar.setValueString(getValueString(item.getValue(), item.getTopic().getCoinageUnit()));
                itemBar.setValue(item.getValue());
                itemBar.setColor(mContext.getResources().getColor(android.R.color.holo_blue_light));
                bars.add(itemBar);
            }
        }
        chart.setBars(bars);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(childPosition == 0){
            return getChartChildView(groupPosition, childPosition, isLastChild, convertView, parent);
        } else {
            return getRegularChildView(groupPosition, childPosition - 1, isLastChild, convertView, parent);
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
