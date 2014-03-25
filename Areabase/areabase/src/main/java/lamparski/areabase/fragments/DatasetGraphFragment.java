package lamparski.areabase.fragments;
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
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.BarGraph.OnBarClickedListener;

import java.util.ArrayList;
import java.util.List;

import lamparski.areabase.R;
import nde2.helpers.CensusHelpers;
import nde2.pull.types.DataSetItem;
import nde2.pull.types.Dataset;
import nde2.pull.types.Topic;

/**
 * Displays data from a single dataset as a graph with a legend.
 *
 * @author filip
 */
public class DatasetGraphFragment extends Fragment
        implements OnItemSelectedListener, OnBarClickedListener {

    private class MListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataSetItems.size();
        }

        @Override
        public Object getItem(int position) {
            return dataSetItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null && getActivity() != null){
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                convertView = inflater.inflate(R.layout.subject_view_listitem, parent, false);
            }

            DataSetItem item = dataSetItems.get(position);
            Topic topic = item.getTopic();
            String valstr = CensusHelpers.getValueString(item.getValue(), topic.getCoinageUnit());

            ((TextView) convertView.findViewById(R.id.subject_view_listitem_title)).setText(topic.getTitle());
            ((TextView) convertView.findViewById(R.id.subject_view_listitem_subtitle)).setText(topic.getDescription());
            ((TextView) convertView.findViewById(R.id.subject_view_listitem_count)).setText(valstr);

            return convertView;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }
    }

    private static final int LEGEND = 0;
    private static final int GRAPH = 1;

    private Dataset dataset;
    private List<DataSetItem> dataSetItems;
    private ArrayList<Bar> bars;
    private int currentTab = -1;

    private ListView legend;
    private BarGraph graph;
    private TabHost tabHost;

    public DatasetGraphFragment(){
    }

    @Override
    public void onClick(int index) {
        graph.getBars().get(index).setColor(getResources().getColor(android.R.color.holo_green_dark));
        legend.setSelection(index);
        if(tabHost != null ) { tabHost.setCurrentTab(LEGEND); }
        legend.smoothScrollToPosition(index);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        graph.getBars().get(position).setColor(getResources().getColor(android.R.color.holo_green_dark));
        if(tabHost != null ) { tabHost.setCurrentTab(GRAPH); }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        for(Bar bar : graph.getBars()){
            bar.setColor(getResources().getColor(android.R.color.holo_blue_dark));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("DatasetGraphFragment", "Creating view...");
        if(getArguments() != null && getArguments().containsKey("dataset")){
            dataset = (Dataset) getArguments().getSerializable("dataset");
            dataSetItems = new ArrayList<DataSetItem>(dataset.getItems());
        } else {
            throw new IllegalArgumentException("Need to have a Dataset specified");
        }

        View theView = inflater.inflate(R.layout.fragment_graphactivity, container, false);

        tabHost = (TabHost) theView.findViewById(R.id.graph_activity_tabhost);
        if(tabHost != null){
            if(savedInstanceState != null && savedInstanceState.containsKey("which-tab")){
                currentTab = savedInstanceState.getInt("which-tab");
            }
            tabHost.setup();

            TabHost.TabSpec graphTabspec = tabHost.newTabSpec("tab-graph");
            graphTabspec.setIndicator(getResources().getString(R.string.tab_graph));
            graphTabspec.setContent(R.id.graph_activity_tab1);

            TabHost.TabSpec legendTabspec = tabHost.newTabSpec("tab-legend");
            legendTabspec.setIndicator(getResources().getString(R.string.tab_legend));
            legendTabspec.setContent(R.id.graph_activity_tab2);

            tabHost.addTab(graphTabspec);
            tabHost.addTab(legendTabspec);

            if(currentTab > -1){
                tabHost.setCurrentTab(currentTab);
            }
        }

        MListAdapter adapter = new MListAdapter();
        legend = (ListView) theView.findViewById(R.id.graph_activity_legend_list);
        legend.setAdapter(adapter);
        legend.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        graph = (BarGraph) theView.findViewById(R.id.graph_activity_bargraph);

        bars = new ArrayList<Bar>(dataSetItems.size());
        for(DataSetItem item : dataSetItems){
            if(canGraph(item)){
                Bar bar = new Bar();
                bar.setName(item.getTopic().getTitle());
                bar.setValue(item.getValue());
                bar.setValueString(CensusHelpers.getValueString(item.getValue(), item.getTopic().getCoinageUnit()));
                bar.setColor(getResources().getColor(android.R.color.holo_blue_dark));
                bars.add(bar);
            }
        }
        graph.setBars(bars);
        return theView;
    }

    /**
     * Determines if this dataset item should go onto graph. Functions as a sort of blacklist,
     * in that it tries its best to exclude totals from the graph.
     * @param item the item
     * @return whether it should be shown on graph.
     */
    private boolean canGraph(DataSetItem item) {
        String title = item.getTopic().getTitle().toLowerCase();
        if(title.contains("all people")){
            return false;
        } else if (title.contains("all usual residents")) {
            return false;
        } else if (title.contains("all households")){
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(tabHost != null){
            outState.putInt("which-tab", tabHost.getCurrentTab());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TextView) getView().findViewById(R.id.subject_view_header)).setText(dataset.getTitle());
    }

    /**
     * Create a new DatasetGraphFragment for the given Dataset.
     * @param dataset The dataset to display
     * @return the fragment
     */
    public static DatasetGraphFragment newInstance(Dataset dataset) {
        Log.d("DatasetGraphFragment",
                String.format("static newInstance called with %s", dataset.toString()));
        DatasetGraphFragment fragment = new DatasetGraphFragment();
        Bundle args = new Bundle();
        args.putSerializable("dataset", dataset);
        fragment.setArguments(args);
        return fragment;
    }
}
