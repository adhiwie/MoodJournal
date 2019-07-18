package com.adhiwie.diary.report;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adhiwie.diary.ConsentMgr;
import com.adhiwie.diary.MainActivity;
import com.adhiwie.diary.R;
import com.adhiwie.diary.utils.Log;
import com.adhiwie.diary.utils.SharedPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DiaryReportActivity extends AppCompatActivity {

    private List<JSONObject> dataInJsonObject;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_diary_report);

        recyclerView = (RecyclerView) findViewById(R.id.rv_diaries);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        try {
            initialiseData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mAdapter = new DiaryAdapter(dataInJsonObject);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!new ConsentMgr(getApplicationContext()).isConsentGiven()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void initialiseData() throws JSONException {
        SharedPref sharedPref = new SharedPref(getApplicationContext());
        String moodData = sharedPref.getString("DIARY_STUDY_DATA");

        JSONArray jsonArray;

        dataInJsonObject = new ArrayList<>();

        if (moodData == null) {
            new Log().e("No data available");
        } else {
            jsonArray = new JSONArray(moodData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = new JSONObject(jsonArray.getString(i));
                dataInJsonObject.add(jsonObj);
            }
        }
    }

    private class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryAdapterViewHolder> {
        private List<JSONObject> dataInJsonObject;
        private String[] quality = {"Extremely Bad","Very Bad","Bad","Average","Good","Very Good","Extremely Good"};
        private String[] productivity = {"Not a bit","Slightly","Fairly","Moderately","Quite a bit","Very","Extremely"};

        public DiaryAdapter(List<JSONObject> dataInJsonObject) {
            this.dataInJsonObject = dataInJsonObject;
        }

        @Override
        public DiaryAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_item, parent, false);
            DiaryAdapterViewHolder viewHolder = new DiaryAdapterViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(DiaryAdapterViewHolder holder, int position) {
            TextView tv_date = holder.tv_date;
            TextView tv_tasks_done = holder.tv_tasks_done;
            TextView tv_quality = holder.tv_quality;
            TextView tv_productivity = holder.tv_productivity;
            TextView tv_time_spent = holder.tv_time_spent;
            TextView tv_tasks_not_done = holder.tv_tasks_not_done;

            try {
                tv_tasks_done.setText(dataInJsonObject.get(position).getString("tasks_done"));
                tv_quality.setText(quality[dataInJsonObject.get(position).getInt("q1")-1]);
                tv_productivity.setText(productivity[dataInJsonObject.get(position).getInt("q2")-1]);
                tv_time_spent.setText(dataInJsonObject.get(position).getString("time_spent"));
                tv_tasks_not_done.setText(dataInJsonObject.get(position).getString("tv_tasks_not_done"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return dataInJsonObject.size();
        }

        public class DiaryAdapterViewHolder extends RecyclerView.ViewHolder {
            TextView tv_date;
            TextView tv_tasks_done;
            TextView tv_quality;
            TextView tv_productivity;
            TextView tv_time_spent;
            TextView tv_tasks_not_done;

            public DiaryAdapterViewHolder(View itemView) {
                super(itemView);

                tv_date = (TextView) findViewById(R.id.tv_date);
                tv_tasks_done = (TextView) findViewById(R.id.tv_tasks_done);
                tv_quality = (TextView) findViewById(R.id.tv_quality);
                tv_productivity = (TextView) findViewById(R.id.tv_productivity);
                tv_time_spent = (TextView) findViewById(R.id.tv_time_spent);
                tv_tasks_not_done = (TextView) findViewById(R.id.tv_tasks_not_done);
            }
        }
    }
}
