package lamparski.areabase;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.common.collect.BiMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import lamparski.areabase.fragments.DatasetGraphFragment;
import lamparski.areabase.services.AreaDataService;
import lamparski.areabase.services.AreaDataService.AreaDataBinder;
import lamparski.areabase.services.AreaDataService.DatasetDumpCallbacks;
import nde2.helpers.ArrayHelpers;
import nde2.pull.types.Area;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.Dataset;
import nde2.pull.types.DateRange;

import static lamparski.areabase.widgets.CommonDialogs.serviceCockupNotify;

public class GraphActivity extends Activity
        implements ActionBar.OnNavigationListener, DatasetDumpCallbacks {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    public static final String GRAPH_AREA = "current_area";
    public static final String GRAPH_DATASET_FAMILY = "dataset_family";
    private static final String STATE_DATASETS = "current_datasets";

    private Area area;
    private DataSetFamily family;
    private HashMap<DateRange, Dataset> datasets = null;
    private BiMap<String, DateRange> dateRangeTable;
    private String[] dateLabelsArray;
    private ActionBar actionBar;
    private AreaDataService areaDataService;
    private boolean is_live, isAreaDataServiceBound;
    private long dbgDatasetPullStart;
    protected ServiceConnection mAreaDataServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (is_live) {
                Log.e("GraphActivity",
                        "The AreaDataService disconnected unexpectedly.");
                isAreaDataServiceBound = false;
                serviceCockupNotify(name, GraphActivity.this);
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("GraphActivity", "AreaDataService connected");
            AreaDataBinder binder = (AreaDataBinder) service;
            areaDataService = binder.getService();
            isAreaDataServiceBound = true;
            if(areaDataService != null) {
                is_live = true;
            }
            dbgDatasetPullStart = System.currentTimeMillis();
            areaDataService.datasetDump(area, family, GraphActivity.this);
        }
    };
    private GraphActivityNavigationSpinnerAdapter actionBarNavListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_graph);

        setProgressBarIndeterminateVisibility(true);

        if(getIntent() != null && getIntent().getExtras() != null){
            if(getIntent().getExtras().containsKey(GRAPH_DATASET_FAMILY)){
                family = (DataSetFamily) getIntent().getExtras().getSerializable(GRAPH_DATASET_FAMILY);
            }
            if(getIntent().getExtras().containsKey(GRAPH_AREA)){
                area = (Area) getIntent().getExtras().getSerializable(GRAPH_AREA);
            }
        } else {
            throw new IllegalArgumentException("Cannot initialise without correct arguments!");
        }

        dateRangeTable = ArrayHelpers.remapDateRanges(family.getDateRanges());
        Set<String> dateLabels = dateRangeTable.keySet();
        dateLabelsArray = dateLabels.toArray(new String[dateLabels.size()]);

        if(savedInstanceState != null){
            if (savedInstanceState.containsKey(GRAPH_AREA)){
                area = (Area) savedInstanceState.getSerializable(GRAPH_AREA);
            }
            if (savedInstanceState.containsKey(STATE_DATASETS)){
                datasets = (HashMap<DateRange, Dataset>) savedInstanceState.getSerializable(STATE_DATASETS);
            }
            if (savedInstanceState.containsKey(GRAPH_DATASET_FAMILY)){
                family = (DataSetFamily) savedInstanceState.getSerializable(GRAPH_DATASET_FAMILY);
            }
        }

        // Set up the action bar to show a dropdown list.
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setDisplayShowTitleEnabled(false);
        // Show the Up button in the action bar.
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set up the action bar list adapter
        actionBarNavListAdapter = new GraphActivityNavigationSpinnerAdapter();
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                actionBarNavListAdapter,
                this);

        Intent intent = new Intent(this, AreaDataService.class);
        if(datasets == null){
            getApplicationContext().bindService(intent, mAreaDataServiceConnection, BIND_AUTO_CREATE);
        }
        actionBar.setSelectedNavigationItem(0);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
        outState.putSerializable(STATE_DATASETS, datasets);
        outState.putSerializable(GRAPH_AREA, area);
        outState.putSerializable(GRAPH_DATASET_FAMILY, family);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.graph, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        Log.d("GraphActivity", "onNavigationItemSelected(position = "+position+")");
        // When the given dropdown item is selected, show its contents in the
        // container view.
        if(datasets != null && !datasets.isEmpty()){
            Log.d("GraphActivity", "Selecting dataset with position " + position);
            Dataset dataset = datasets.get(dateRangeTable.get(dateLabelsArray[position]));
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, DatasetGraphFragment.newInstance(dataset))
                    .commit();
        }
        return true;
    }

    @Override
    public void datasetsDownloaded(HashMap<DateRange, Dataset> datasets) {
        Log.i("GraphActivity",
                String.format("%d datasets arrived in %dms, enabling navigation...",
                        datasets.size(), System.currentTimeMillis() - dbgDatasetPullStart));
        setProgressBarIndeterminateVisibility(false);
        this.datasets = datasets;
        actionBarNavListAdapter.notifyDatasetChanged();
        actionBar.setSelectedNavigationItem(0);
    }

    @Override
    public void onError(Throwable tr) {
        Crouton.makeText(this, getString(R.string.error_cannot_fetch_area_data), Style.ALERT);
        Log.e("GraphActivity", "Error downloading datasets", tr);
    }

    private class GraphActivityNavigationSpinnerAdapter implements SpinnerAdapter {
        private HashSet<DataSetObserver> observers;

        public GraphActivityNavigationSpinnerAdapter(){
            observers = new HashSet<DataSetObserver>();
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent);
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            Log.d("GraphActivity", "SpinnerAdapter: request to register observer " + observer.toString());
            observers.add(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            Log.d("GraphActivity", "SpinnerAdapter: request to unregister observer " + observer.toString());
            observers.remove(observer);
        }

        @Override
        public int getCount() {
            return datasets != null ? datasets.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return datasets.get(dateRangeTable.get(dateLabelsArray[position]));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d("GraphActivity",
                    String.format("actionbar list getView(position = %d, convertView = %s); datasets: %s",
                            position,
                            convertView != null ? convertView.toString() : "null",
                            datasets != null ? datasets.size() + " items" : "null"));
            Log.d("GraphActivity",
                    String.format("... dateLabelsArray.length = %d, dateLabelsArray[position] = %s",
                            dateLabelsArray.length,
                            dateLabelsArray[position]));
            if(convertView == null){
                convertView = getLayoutInflater().inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            }

            ((TextView) convertView.findViewById(android.R.id.text1)).setText(dateLabelsArray[position]);

            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return datasets == null || datasets.isEmpty();
        }

        public void notifyDatasetChanged(){
            if(datasets != null){
                for(DataSetObserver observer : observers){
                    observer.onChanged();
                }
            }
        }
    }
}
