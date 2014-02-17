package lamparski.areabase.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.HashMap;
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

    private AreaDataService.SubjectDumpIface mSubjectDumpCallbacks = new AreaDataService.SubjectDumpIface() {
        @Override
        public void subjectDumpReady(Map<DataSetFamily, Dataset> map) {
            mAdapter.setItems(map);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onError(Throwable tr) {
            showToastCrossThread(tr.toString(), Toast.LENGTH_LONG);
            tr.printStackTrace();
        }
    };

    @Override
    public void refreshContent() {
        mService.subjectDump(area, subjectName, mSubjectDumpCallbacks);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View theView = inflater.inflate(R.layout.fragment_subject_view, container, false);

        mAdapter = new SubjectExpandableListAdapter(getActivity(), EMPTY);
        ((ExpandableListView) theView.findViewById(R.id.subject_view_expandable_list)).setAdapter(mAdapter);

        return theView;
    }
}
