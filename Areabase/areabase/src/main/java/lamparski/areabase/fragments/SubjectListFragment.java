package lamparski.areabase.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
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
 * Created by filip on 19/02/14.
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

    private List<DetailedSubject> subjects;
    private Map<DetailedSubject, Integer> subjectsWithCount;
    private SubjectListAdapter mAdapter;

    @Override
    public void refreshContent() {
        new AsyncTask<Area, Void, Map<DetailedSubject, Integer>>(){

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
                    for(Entry<Subject, Integer> entry : baseHash.entrySet()){
                        DetailedSubject detailedSubject = entry.getKey().getDetailed();
                        targetHash.put(detailedSubject, entry.getValue());
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    onIOError();
                } catch (Exception e) {
                    e.printStackTrace();
                    showToastCrossThread(e.toString(), 0);
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
