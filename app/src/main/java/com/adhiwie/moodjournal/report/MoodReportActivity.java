package com.adhiwie.moodjournal.report;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adhiwie.moodjournal.ConsentMgr;
import com.adhiwie.moodjournal.MainActivity;
import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.SharedPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MoodReportActivity extends AppCompatActivity {

    private List<JSONObject> dataInJsonObject;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mood_report);

        recyclerView = (RecyclerView) findViewById(R.id.rv_journal);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        try {
            initialiseData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        String diaryData = sharedPref.getString("DAILY_MOOD_REPORT_DATA");

        JSONArray jsonArray;

        dataInJsonObject = new ArrayList<>();

        if (diaryData == null) {
            new Log().e("No data available");
        } else {
            jsonArray = new JSONArray(diaryData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = new JSONObject(jsonArray.getString(i));
                dataInJsonObject.add(jsonObj);
            }

            mAdapter = new MoodAdapter(dataInJsonObject);
            recyclerView.setAdapter(mAdapter);
        }
    }

    private class MoodAdapter extends RecyclerView.Adapter<MoodAdapter.MoodAdapterViewHolder> {
        private List<JSONObject> dataInJsonObject;
        private String[] mood_level = {"Extremely Sad","Very Sad","Neutral","Happy","Very Happy"};
        private Drawable mood_level_drawable = null;

        public MoodAdapter(List<JSONObject> dataInJsonObject) {
            this.dataInJsonObject = dataInJsonObject;
        }

        @Override
        public MoodAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_item, parent, false);
            MoodAdapterViewHolder viewHolder = new MoodAdapterViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MoodAdapterViewHolder holder, int position) {
            TextView tv_date = holder.tv_date;
            TextView tv_mood_level = holder.tv_mood_level;
            TextView tv_notes = holder.tv_notes;
            ImageView iv_mood_level = holder.iv_mood_level;

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
                String dateString = formatter.format(new Date(dataInJsonObject.get(position).getLong("end_time")));

                tv_date.setText(dateString);
                tv_mood_level.setText(mood_level[dataInJsonObject.get(position).getInt("q1")-1]);
                tv_notes.setText(dataInJsonObject.get(position).getString("notes"));

                switch (dataInJsonObject.get(position).getInt("q1")) {
                    case 1:
                        mood_level_drawable = getDrawable(R.drawable.ic_mood_1);
                        break;
                    case 2:
                        mood_level_drawable = getDrawable(R.drawable.ic_mood_2);
                        break;
                    case 3:
                        mood_level_drawable = getDrawable(R.drawable.ic_mood_3);
                        break;
                    case 4:
                        mood_level_drawable = getDrawable(R.drawable.ic_mood_4);
                        break;
                    case 5:
                        mood_level_drawable = getDrawable(R.drawable.ic_mood_5);
                        break;
                    default:
                        break;
                }
                iv_mood_level.setBackground(mood_level_drawable);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return dataInJsonObject.size();
        }

        public class MoodAdapterViewHolder extends RecyclerView.ViewHolder {
            TextView tv_date;
            TextView tv_mood_level;
            TextView tv_notes;
            ImageView iv_mood_level;

            public MoodAdapterViewHolder(View itemView) {
                super(itemView);

                tv_date = (TextView) itemView.findViewById(R.id.tv_date);
                tv_mood_level = (TextView) itemView.findViewById(R.id.tv_mood_level);
                tv_notes = (TextView) itemView.findViewById(R.id.tv_notes);
                iv_mood_level = (ImageView) itemView.findViewById(R.id.iv_mood_level);
            }
        }
    }
}