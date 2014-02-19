package lamparski.areabase.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lamparski.areabase.R;
import lamparski.areabase.services.AreaDataService;
import lamparski.areabase.widgets.SubjectExpandableListAdapter;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.Dataset;

/**
 * Created by filip on 17/02/14.
 */
public class SubjectViewFragment extends DetailViewFragment {

    private static final HashMap<DataSetFamily, Dataset> EMPTY = new HashMap<DataSetFamily, Dataset>();

    private SubjectExpandableListAdapter mAdapter;
    private ProgressBar mPlaceholderProgressBar;

    private AreaDataService.SubjectDumpIface mSubjectDumpCallbacks = new AreaDataService.SubjectDumpIface() {
        @Override
        public void subjectDumpReady(Map<DataSetFamily, Dataset> map) {
            getActivity().setProgressBarIndeterminateVisibility(false);
            mAdapter.setItems(map);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onProgress(final int position, final int size) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPlaceholderProgressBar.setMax(size);
                    mPlaceholderProgressBar.setProgress(position);
                }
            });
        }

        @Override
        public void onError(Throwable tr) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getActivity().setProgressBarIndeterminateVisibility(false);
                }
            });
            showToastCrossThread(tr.toString(), Toast.LENGTH_LONG);
            tr.printStackTrace();
        }
    };

    @Override
    public void refreshContent() {
        ((TextView) getView().findViewById(R.id.subject_view_header)).setText(subjectName.toUpperCase(Locale.UK));
        getActivity().setProgressBarIndeterminateVisibility(true);
        mPlaceholderProgressBar.setProgress(0);
        mService.subjectDump(area, subjectName, mSubjectDumpCallbacks);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View theView = inflater.inflate(R.layout.fragment_subject_view, container, false);

        mPlaceholderProgressBar = (ProgressBar) theView.findViewById(R.id.subject_view_progress_bar);

        mAdapter = new SubjectExpandableListAdapter(getActivity(), EMPTY);
        ExpandableListView datasetView = (ExpandableListView) theView.findViewById(R.id.subject_view_expandable_list);
        datasetView.setEmptyView(mPlaceholderProgressBar);
        datasetView.setAdapter(mAdapter);

        return theView;
    }
}
