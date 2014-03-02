package lamparski.areabase.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.BarGraph.OnBarClickedListener;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lamparski.areabase.R;
import nde2.helpers.CensusHelpers;
import nde2.pull.types.DataSetItem;
import nde2.pull.types.Dataset;

/**
 * Created by filip on 01/03/14.
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
            return null;
        }
    }

    private Dataset dataset;
    private List<DataSetItem> dataSetItems;
    private ArrayList<Bar> bars;

    private ListView legend;
    private BarGraph graph;

    public DatasetGraphFragment(){
    }

    @Override
    public void onClick(int index) {
        graph.getBars().get(index).setColor(getResources().getColor(android.R.color.holo_green_dark));
        legend.setSelection(index);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        graph.getBars().get(position).setColor(getResources().getColor(android.R.color.holo_green_dark));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        for(Bar bar : graph.getBars()){
            bar.setColor(getResources().getColor(android.R.color.holo_blue_dark));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(getArguments() != null && getArguments().containsKey("dataset")){
            dataset = (Dataset) getArguments().getSerializable("dataset");
            dataSetItems = new ArrayList<DataSetItem>(dataset.getItems());
        } else {
            throw new IllegalArgumentException("Need to have a Dataset specified");
        }

        View theView = inflater.inflate(R.layout.fragment_graphactivity, container, false);
        MListAdapter adapter = new MListAdapter();
        legend = (ListView) theView.findViewById(R.id.graph_activity_legend_list);
        legend.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        graph = (BarGraph) theView.findViewById(R.id.graph_activity_bargraph);

        bars = new ArrayList<Bar>(dataSetItems.size());
        for(DataSetItem item : dataSetItems){
            Bar bar = new Bar();
            bar.setName(item.getTopic().getTitle());
            bar.setValue(item.getValue());
            bar.setValueString(CensusHelpers.getValueString(item.getValue(), item.getTopic().getCoinageUnit()));
            bar.setColor(getResources().getColor(android.R.color.holo_blue_dark));
            bars.add(bar);
        }
        graph.setBars(bars);
        legend.setAdapter(adapter);
        return theView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static DatasetGraphFragment newInstance(Dataset dataset) {
        DatasetGraphFragment fragment = new DatasetGraphFragment();
        Bundle args = new Bundle();
        args.putSerializable("dataset", dataset);
        fragment.setArguments(args);
        return fragment;
    }
}
