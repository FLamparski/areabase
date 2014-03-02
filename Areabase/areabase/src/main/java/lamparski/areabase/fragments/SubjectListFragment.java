package lamparski.areabase.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lamparski.areabase.AreaActivity;
import lamparski.areabase.R;
import nde2.pull.types.Area;
import nde2.pull.types.DetailedSubject;
import nde2.pull.types.Subject;

/**
 * Shows a list of subjects (and the number of datasets they contain) for a given {@link nde2.pull.types.Area}.
 * The items are links which, when clicked, should take the user to the {@link lamparski.areabase.fragments.SubjectViewFragment}
 * for the corresponding {@link nde2.pull.types.Subject}.
 *
 * @author filip
 * @see nde2.pull.types.Subject
 * @see lamparski.areabase.fragments.SubjectViewFragment
 * @see nde2.pull.types.Area
 */
public class SubjectListFragment extends DetailViewFragment implements OnItemClickListener {

    private class SubjectListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return subjects.size();
        }

        @Override
        public Object getItem(int position) {
            return subjects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.subject_view_listitem, parent, false);
            }

            DetailedSubject subject = subjects.get(position);
            ((TextView) convertView.findViewById(R.id.subject_view_listitem_title)).setText(subject.getName());
            ((TextView) convertView.findViewById(R.id.subject_view_listitem_subtitle)).setText(subject.getDescription());
            ((TextView) convertView.findViewById(R.id.subject_view_listitem_count)).setText(subjectsWithCount.get(subject).toString());

            return convertView;
        }
    }

    private Runnable refreshContentAction = new Runnable() {
        @Override
        public void run() {
            if(area == null){
                area = ((AreaActivity) getActivity()).getArea();
            }

            new AsyncTask<Area, Void, Map<DetailedSubject, Integer>>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    getActivity().setProgressBarIndeterminateVisibility(true);
                }

                @Override
                protected Map<DetailedSubject, Integer> doInBackground(Area... params) {
                    Area myArea = params[0];
                    Map<DetailedSubject, Integer> targetHash = new HashMap<DetailedSubject, Integer>();

                    try {
                        Map<Subject, Integer> baseHash = myArea.getCompatibleSubjects();
                        for (Entry<Subject, Integer> entry : baseHash.entrySet()) {
                            DetailedSubject detailedSubject = entry.getKey().getDetailed();
                            targetHash.put(detailedSubject, entry.getValue());
                        }
                    } catch (IOException ioe) {
                        Log.e("SubjectListFragment", "IO exception when fetching the subject list", ioe);
                        onIOError();
                    } catch (Exception e) {
                        Log.e("SubjectListFragment", "An exception when fetching the subject list", e);
                        showCroutonCrossThread(e.toString());
                    }

                    return targetHash;
                }

                @Override
                protected void onPostExecute(Map<DetailedSubject, Integer> result) {
                    super.onPostExecute(result);
                    getActivity().setProgressBarIndeterminateVisibility(false);
                    onSubjectHashFound(result);
                }
            }.execute(area);
        }
    };

    private List<DetailedSubject> subjects;
    private Map<DetailedSubject, Integer> subjectsWithCount;
    private SubjectListAdapter mAdapter;
    private int refreshContentTries = 0;

    @Override
    public void refreshContent() {
        if(is_live){
            try{
                new Handler().postDelayed(refreshContentAction, 100);
            } catch (NullPointerException npe){
                if(++refreshContentTries <= 10){
                    refreshContent();
                }
            }
        } else {
            if(++refreshContentTries <= 20){
                refreshContent();
            }
        }
    }

    protected void onSubjectHashFound(Map<DetailedSubject, Integer> hash){
        Log.d("SubjectListFragment", "Found " + hash.size() + " subjects for this area.");
        subjects.clear();
        subjects.addAll(hash.keySet());
        subjectsWithCount = hash;
        mAdapter.notifyDataSetChanged();
        mAdapter.getCount();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((AreaActivity) getActivity()).showSubjectView(area, subjects.get(position).getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subject_list, container, false);

        subjects = new ArrayList<DetailedSubject>();
        subjectsWithCount = new HashMap<DetailedSubject, Integer>();
        mAdapter = new SubjectListAdapter();

        ((TextView) view.findViewById(R.id.subject_view_header)).setText("AVAILABLE SUBJECTS");
        ListView theList = (ListView) view.findViewById(R.id.subject_list_view);
        theList.setAdapter(mAdapter);
        theList.setOnItemClickListener(this);

        return view;
    }
}
