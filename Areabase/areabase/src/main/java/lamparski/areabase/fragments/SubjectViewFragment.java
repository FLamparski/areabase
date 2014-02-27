package lamparski.areabase.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lamparski.areabase.AreaActivity;
import lamparski.areabase.R;
import lamparski.areabase.services.AreaDataService;
import lamparski.areabase.widgets.SubjectExpandableListAdapter;
import nde2.pull.types.Area;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.Dataset;

/**
 * A way to display datasets for a particular subject for a particular area.
 * It shows all datasets (using {@link Dataset} objects) in a {@link nde2.pull.types.Subject} as a
 * two-level list (an {@link android.widget.ExpandableListView}), where level 1 is the title of the
 * dataset, and level 2 is the content.
 *
 * @author filip
 * @see lamparski.areabase.fragments.SubjectListFragment
 */
public class SubjectViewFragment extends DetailViewFragment {

    private static final HashMap<DataSetFamily, Dataset> EMPTY = new HashMap<DataSetFamily, Dataset>();

    private SubjectExpandableListAdapter mAdapter;
    private ProgressBar mPlaceholderProgressBar;

    private AreaDataService.SubjectDumpIface mSubjectDumpCallbacks = new AreaDataService.SubjectDumpIface() {
        @Override
        public void subjectDumpReady(Map<DataSetFamily, Dataset> map) {
            if(getActivity() != null){
                getActivity().setProgressBarIndeterminateVisibility(false);
            }
            mAdapter.setItems(map);
            mAdapter.notifyDataSetChanged();
            refreshContentTries = 0;
        }

        @Override
        public void onProgress(final int position, final int size) {
            if(getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPlaceholderProgressBar.setMax(size);
                        mPlaceholderProgressBar.setProgress(position);
                    }
                });
            }
        }

        @Override
        public void onError(Throwable tr) {
            if(getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().setProgressBarIndeterminateVisibility(false);
                    }
                });
            }
            showCroutonCrossThread(tr.toString());
            Log.e("SubjectViewFragment", "SubjectDumpCallbacks onError()", tr);
        }
    };

    private Runnable refreshContentAction = new Runnable() {
        @Override
        public void run() {
            try {
                if(area == null){
                    area = ((AreaActivity) getActivity()).getArea();
                }
                ((TextView) getView().findViewById(R.id.subject_view_header)).setText(subjectName.toUpperCase(Locale.UK));
                getActivity().setProgressBarIndeterminateVisibility(true);
                mPlaceholderProgressBar.setProgress(0);
                mService.subjectDump(area, subjectName, mSubjectDumpCallbacks);
            } catch (NullPointerException npe) {
                refreshContent();
            }
        }
    };

    private int refreshContentTries = 0;

    @Override
    public void refreshContent() {
        if(is_live){
            if(++refreshContentTries <= 10){
                new Handler().postDelayed(refreshContentAction, 100);
            } else {
                Log.e("Subject View", "Exceeded the maximum number of tries for refreshContent() when service is connected.");
            }
        } else {
            if(++refreshContentTries <= 20){
                refreshContent();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View theView = inflater.inflate(R.layout.fragment_subject_view, container, false);

        if(getArguments() != null){
            if(getArguments().containsKey("argument-area")){
                area = (Area) getArguments().getSerializable("argument-area");
            }

            if(getArguments().containsKey("argument-subject-name")){
                subjectName = getArguments().getString("argument-subject-name");
            }
        }

        mPlaceholderProgressBar = (ProgressBar) theView.findViewById(R.id.subject_view_progress_bar);

        mAdapter = new SubjectExpandableListAdapter(getActivity(), EMPTY);
        ExpandableListView datasetView = (ExpandableListView) theView.findViewById(R.id.subject_view_expandable_list);
        datasetView.setEmptyView(mPlaceholderProgressBar);
        datasetView.setAdapter(mAdapter);

        return theView;
    }
}
