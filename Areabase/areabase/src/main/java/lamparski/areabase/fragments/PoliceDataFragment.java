package lamparski.areabase.fragments;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

import org.mysociety.mapit.Mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import lamparski.areabase.AreaActivity;
import lamparski.areabase.R;
import lamparski.areabase.cardproviders.CrimeCardProvider;
import lamparski.areabase.widgets.CommonDialogHandlers;
import nde2.helpers.ArrayHelpers;
import nde2.pull.types.Area;
import police.errors.APIException;
import police.methodcalls.StreetLevelCrimeMethodCall;
import police.types.Crime;

/**
 * Shows the breakdown of crimes for the past 12 months for this area.
 * TODO: make it also show a chart of these crimes in time, not just a pie.
 *
 * @author filip
 */
public class PoliceDataFragment extends DetailViewFragment {
    private class CrimeLegendEntry {
        public String category;
        public int count;
        public int colour;

        private CrimeLegendEntry(String category, int count, int colour) {
            this.category = category;
            this.count = count;
            this.colour = colour;
        }
    }

    private class CrimeListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCrimeList.size();
        }

        @Override
        public Object getItem(int position) {
            return mCrimeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.subject_view_groupitem, parent, false);
            }

            CrimeLegendEntry category = mCrimeList.get(position);

            ((TextView) convertView.findViewById(R.id.subject_view_groupitem_title)).setText(category.category);
            ((TextView) convertView.findViewById(R.id.subject_view_groupitem_count)).setText(Integer.toString(category.count));
            ((TextView) convertView.findViewById(R.id.subject_view_groupitem_count)).setTextColor(category.colour);

            return convertView;
        }
    }

    final private AsyncTask<Area, Void, Collection<Crime>> fetchCrimeDataTask = new AsyncTask<Area, Void, Collection<Crime>>(){

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(getActivity() != null){
                getActivity().setProgressBarIndeterminateVisibility(true);
            }
        }

        @Override
        protected Collection<Crime> doInBackground(Area... params) {
            double[][] areaPoly = new double[0][];
            try {
                areaPoly = Mapper.getGeometryForArea(area);
            } catch (Exception e) {
                Log.e("PoliceDataFragment", "Error when fetching area geometry", e);
                onIOError();
            }
            double[][] simplerPoly = ArrayHelpers.every_nth_pair(areaPoly, 10);

            Collection<Crime> crimes = null;
            try {
                crimes = new StreetLevelCrimeMethodCall().addAreaPolygon(simplerPoly).getStreetLevelCrime();
            } catch (IOException e) {
                Log.e("PoliceDataFragment", "IO exception when fetching area crimes", e);
                onIOError();
            } catch (APIException e) {
                Log.e("PoliceDataFragment", "API exception when fetching area crimes", e);
                onPoliceApiError(e);
            }

            return crimes;
        }

        @Override
        protected void onPostExecute(Collection<Crime> crimes) {
            super.onPostExecute(crimes);
            if(getActivity() != null){
                getActivity().setProgressBarIndeterminateVisibility(false);
            }
            onCrimeDataFound(crimes);
        }
    };

    private static final int[] SLICE_COLOURS = new int[5];
    private int currentSliceColour = 0;

    private PieGraph mGraph;
    private CrimeListAdapter mListAdapter;
    private ArrayList<CrimeLegendEntry> mCrimeList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SLICE_COLOURS[0] = getResources().getColor(android.R.color.holo_blue_bright);
        SLICE_COLOURS[1] = getResources().getColor(android.R.color.holo_green_light);
        SLICE_COLOURS[2] = getResources().getColor(android.R.color.holo_orange_light);
        SLICE_COLOURS[3] = getResources().getColor(android.R.color.holo_red_light);
        SLICE_COLOURS[4] = getResources().getColor(android.R.color.holo_purple);
        mCrimeList = new ArrayList<CrimeLegendEntry>();
        mListAdapter = new CrimeListAdapter();
    }

    private Runnable refreshContentAction = new Runnable() {
        @Override
        public void run() {
            try {
                if(area == null){
                    area = ((AreaActivity) getActivity()).getArea();
                }

                fetchCrimeDataTask.execute(area);
            } catch (Exception e) {
                Log.w("PoliceDataFragment", "Refresh try #" + refreshContentTries
                        + " failed because of an Exception", e);
                refreshContent();
            }
        }
    };

    private int refreshContentTries = 0;

    @Override
    public void refreshContent() {
        if(is_live){
            new Handler().postDelayed(refreshContentAction, 100);
        } else {
            if(++refreshContentTries <= 20){
                refreshContent();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View theView = inflater.inflate(R.layout.police_data_fragment, container, false);

        mGraph = (PieGraph) theView.findViewById(R.id.police_data_fragment_crime_graph);
        ListView crimeList = (ListView) theView.findViewById(R.id.listView);
        crimeList.setAdapter(mListAdapter);

        return theView;
    }

    protected void onCrimeDataFound(Collection<Crime> mostRecentCrimes){
        if(mostRecentCrimes != null){
            Map<String, Integer> crimeSlice = CrimeCardProvider.crimeSlice(mostRecentCrimes);
            mCrimeList.clear();
            mGraph.removeSlices();
            for(String category : crimeSlice.keySet()){
                CrimeLegendEntry e = new CrimeLegendEntry(category, crimeSlice.get(category), nextSliceColour());
                PieSlice sForE = new PieSlice();
                sForE.setColor(e.colour);
                sForE.setValue(e.count);
                sForE.setTitle(e.category);
                mGraph.addSlice(sForE);
                mCrimeList.add(e);
            }
        mListAdapter.notifyDataSetChanged();
        } else {
            Crouton.makeText(getActivity(), "No crime information received", Style.INFO).show();
        }
    }

    protected void onPoliceApiError(final APIException error){
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog dlg = new Builder(getActivity())
                            .setTitle(R.string.error_police_api)
                            .setMessage(getResources().getString(R.string.error_police_api_body, error.toString()))
                            .setNeutralButton(android.R.string.ok, CommonDialogHandlers.JUST_DISMISS)
                            .create();
                    dlg.show();
                }
            });
        }
        Log.w("PoliceDataFragment", "Police API error", error);
    }

    private int nextSliceColour(){
        int baseColor = SLICE_COLOURS[currentSliceColour++ % SLICE_COLOURS.length];
        int variation = currentSliceColour / SLICE_COLOURS.length;
        float[] hsv = new float[3];
        Color.colorToHSV(baseColor, hsv);
        hsv[2] = (float) (hsv[2] + (0.1 * variation));
        hsv[1] = (float) (hsv[1] + (0.1 * variation));
        return Color.HSVToColor(hsv);
    }
}
