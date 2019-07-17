package com.adhiwie.moodjournal.report;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.adhiwie.moodjournal.ConsentMgr;
import com.adhiwie.moodjournal.MainActivity;
import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.SharedPref;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MoodReportActivity extends AppCompatActivity {

    BarChart stressChart;
    BarChart activenessChart;
    BarChart happinessChart;
    List<BarEntry> stressDataList;
    BarDataSet stressBarDataSet;
    List<BarEntry> activenessDataList;
    BarDataSet activenessLineDataSet;
    List<BarEntry> happinessDataList;
    BarDataSet happinessLineDataSet;
    List<JSONObject> dataInJsonObject;
    long currentTimeMillis;
    String[] days;

    ValueFormatter  XAxisFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mood_report);

        days = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        XAxisFormatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return days[(int) value];
            }
        };

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
        String moodData = sharedPref.getString("DAILY_MOOD_REPORT_DATA");

        JSONArray jsonArray;

        stressDataList = new ArrayList<>();
        activenessDataList = new ArrayList<>();
        happinessDataList = new ArrayList<>();
        dataInJsonObject = new ArrayList<>();

        if (moodData == null) {
            stressDataList.clear();
            activenessDataList.clear();
            happinessDataList.clear();
            new Log().e("No mood data");
        } else {
            jsonArray = new JSONArray(moodData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = new JSONObject(jsonArray.getString(i));
                dataInJsonObject.add(jsonObj);
            }
        }

        currentTimeMillis = Calendar.getInstance().getTimeInMillis();
        populateDateRange();


        drawWeeklyStressChart(currentTimeMillis);
        drawActivenessChart(currentTimeMillis);
        drawHappinessChart(currentTimeMillis);
    }

    private void populateDateRange() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(currentTimeMillis);
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        SimpleDateFormat format = new SimpleDateFormat("d MMM");
        String start = format.format(c.getTime());
        c.add(Calendar.WEEK_OF_YEAR, 1);
        String end = format.format(c.getTime());
        SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");

        String year = formatYear.format(c.getTime());

        String template = getResources().getString(R.string.date_range, start, end, year);


        TextView dateRangeTextView = (TextView) findViewById(R.id.date_range_tv);
        dateRangeTextView.setText(template);
    }


    private long getTime(List<JSONObject> list, int index) throws JSONException {
        return list.get(index).getLong("end_time");
    }

    private JSONArray getWeeklyData(long timeInMillis) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        cal.setTimeInMillis(timeInMillis);
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        System.out.println("Start of this week:       " + cal.getTime());
        System.out.println("... in milliseconds:      " + cal.getTimeInMillis());
        long start_of_this_week = cal.getTimeInMillis();

        // start of the next week
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        System.out.println("Start of the next week:   " + cal.getTime());
        System.out.println("... in milliseconds:      " + cal.getTimeInMillis());
        long start_of_next_week = cal.getTimeInMillis();

        for (int i = 0; i < dataInJsonObject.size(); i++) {
            long time = getTime(dataInJsonObject, i);
            if (time >= start_of_this_week && time < start_of_next_week) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(time);
                int index = c.get(Calendar.DAY_OF_WEEK) - 1;
                jsonArray.put(index, dataInJsonObject.get(i));

                new Log().e("Day: " + index + " - Date: " + c.getTime() + " - Stress: " + dataInJsonObject.get(i).getInt("q1") + " - Activeness: " + dataInJsonObject.get(i).getInt("q2") + " - Happiness: " + dataInJsonObject.get(i).getInt("q3"));
                //weeklyData.add(dataInJsonObject.get(i));
            }
        }

        return jsonArray;

    }

    public void prevWeek(View v) throws JSONException {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        cal.setTimeInMillis(currentTimeMillis);
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.add(Calendar.WEEK_OF_YEAR, -1);
        currentTimeMillis = cal.getTimeInMillis();
        populateDateRange();

        updateData((currentTimeMillis));

    }

    public void nextWeek(View v) throws JSONException {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        cal.setTimeInMillis(currentTimeMillis);
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.add(Calendar.WEEK_OF_YEAR, 1);
        currentTimeMillis = cal.getTimeInMillis();
        populateDateRange();

        updateData((currentTimeMillis));

    }

    private void updateData(long timeInMillis) throws JSONException {
        stressBarDataSet.clear();
        activenessLineDataSet.clear();
        happinessLineDataSet.clear();

        JSONArray jsonArray = getWeeklyData(timeInMillis);

        //Update chart data
        BarEntry entryForStressData;
        for (int i = 0; i < 7; i++) {
            try {
                jsonArray.get(i);
                entryForStressData = new BarEntry(i, jsonArray.getJSONObject(i).getInt("q1"));
            } catch (JSONException e) {
                e.printStackTrace();
                entryForStressData = new BarEntry(i, 0);
            }

            stressDataList.add(entryForStressData);
        }

        stressChart.notifyDataSetChanged();
        stressChart.animateY(1000);

        //Update chart data
        BarEntry entryForActivenessData;
        for (int i = 0; i < 7; i++) {
            try {
                jsonArray.get(i);
                entryForActivenessData = new BarEntry(i, jsonArray.getJSONObject(i).getInt("q2"));
            } catch (JSONException e) {
                e.printStackTrace();
                entryForActivenessData = new BarEntry(i, 0);
            }

            activenessDataList.add(entryForActivenessData);
        }

        activenessChart.notifyDataSetChanged();
        activenessChart.animateY(1000);

        //Update chart data
        BarEntry entryForHappinessData;
        for (int i = 0; i < 7; i++) {
            try {
                jsonArray.get(i);
                entryForHappinessData = new BarEntry(i, jsonArray.getJSONObject(i).getInt("q3"));
            } catch (JSONException e) {
                e.printStackTrace();
                entryForHappinessData = new BarEntry(i, 0);
            }

            happinessDataList.add(entryForHappinessData);
        }

        happinessChart.notifyDataSetChanged();
        happinessChart.animateY(1000);
    }

    private BarDataSet getWeeklyStressDataSet(long timeInMillis) throws JSONException {
        JSONArray jsonArray = getWeeklyData(timeInMillis);

        BarEntry entry;

        for (int i = 0; i < 7; i++) {
            try {
                jsonArray.get(i);
                entry = new BarEntry(i, jsonArray.getJSONObject(i).getInt("q1"));
            } catch (JSONException e) {
                e.printStackTrace();
                entry = new BarEntry(i, 0);
            }

            stressDataList.add(entry);
        }

        stressBarDataSet = new BarDataSet(stressDataList, "Stress Level");
//        stressBarDataSet.setCircleRadius(5f);
//        stressBarDataSet.setCircleColor(getResources().getColor(R.color.colorPrimary));
//        stressBarDataSet.setDrawCircleHole(false);
        stressBarDataSet.setColor(getResources().getColor(R.color.colorPrimary));
        stressBarDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        return stressBarDataSet;
    }

    private void drawWeeklyStressChart(long timeInMillis) throws JSONException {
        stressChart = (BarChart) findViewById(R.id.stress_chart);

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(getWeeklyStressDataSet(timeInMillis));

        final BarData data = new BarData(dataSets);
        data.setValueFormatter(new MyValueFormatter());
        data.setDrawValues(false);

        stressChart.setData(data);
        stressChart.animateY(1000);
        stressChart.setTouchEnabled(false);
        Description description = new Description();
        description.setText("");
        stressChart.setDescription(description);
        Legend legend = stressChart.getLegend();
        legend.setEnabled(false);

        final String[] level = new String[]{"", "Very Stressed", "Stressed", "Neutral", "Relaxed", "Very Relaxed"};

        ValueFormatter YAxisFormatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return level[(int) value];
            }
        };

        XAxis xAxis = stressChart.getXAxis();
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(XAxisFormatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0);

        YAxis yAxis = stressChart.getAxisLeft();
        yAxis.setLabelCount(6, true);
        yAxis.setValueFormatter(YAxisFormatter);
        yAxis.setAxisMaximum(5);
        yAxis.setAxisMinimum(0);

        YAxis yAxis1 = stressChart.getAxisRight();
        yAxis1.setEnabled(false);
    }

    private BarDataSet getWeeklyActivenessDataSet(long timeInMillis) throws JSONException {
        JSONArray jsonArray = getWeeklyData(timeInMillis);

        BarEntry entry;

        for (int i = 0; i < 7; i++) {
            try {
                jsonArray.get(i);
                entry = new BarEntry(i, jsonArray.getJSONObject(i).getInt("q2"));
            } catch (JSONException e) {
                e.printStackTrace();
                entry = new BarEntry(i, 0);
            }

            activenessDataList.add(entry);
        }

        activenessLineDataSet = new BarDataSet(activenessDataList, "Activeness Level");
        activenessLineDataSet.setColor(getResources().getColor(R.color.red));
        activenessLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        return activenessLineDataSet;
    }

    private void drawActivenessChart(long timeInMillis) throws JSONException {
        activenessChart = (BarChart) findViewById(R.id.activeness_chart);

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(getWeeklyActivenessDataSet(timeInMillis));

        final BarData data = new BarData(dataSets);
        data.setValueFormatter(new MyValueFormatter());
        data.setDrawValues(false);

        activenessChart.setData(data);
        activenessChart.animateY(1000);
        activenessChart.setTouchEnabled(false);
        Description description = new Description();
        description.setText("");
        activenessChart.setDescription(description);
        Legend legend = activenessChart.getLegend();
        legend.setEnabled(false);

        final String[] level = new String[]{"", "Very Sleepy", "Sleepy", "Neutral", "Active", "Very Active"};

        ValueFormatter YAxisFormatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return level[(int) value];
            }
        };

        XAxis xAxis = activenessChart.getXAxis();
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(XAxisFormatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0);

        YAxis yAxis = activenessChart.getAxisLeft();
        yAxis.setLabelCount(6, true);
        yAxis.setValueFormatter(YAxisFormatter);
        yAxis.setAxisMaximum(5);
        yAxis.setAxisMinimum(0);

        YAxis yAxis1 = activenessChart.getAxisRight();
        yAxis1.setEnabled(false);
    }

    private BarDataSet getWeeklyHappinessDataSet(long timeInMillis) throws JSONException {
        JSONArray jsonArray = getWeeklyData(timeInMillis);

        BarEntry entry;

        for (int i = 0; i < 7; i++) {
            try {
                jsonArray.get(i);
                entry = new BarEntry(i, jsonArray.getJSONObject(i).getInt("q3"));
            } catch (JSONException e) {
                e.printStackTrace();
                entry = new BarEntry(i, 0);
            }

            happinessDataList.add(entry);
        }

        happinessLineDataSet = new BarDataSet(happinessDataList, "Happiness Level");
        happinessLineDataSet.setColor(getResources().getColor(R.color.yellow));
        happinessLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        return happinessLineDataSet;
    }

    private void drawHappinessChart(long timeInMillis) throws JSONException {
        happinessChart = (BarChart) findViewById(R.id.happiness_chart);

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(getWeeklyHappinessDataSet(timeInMillis));

        final BarData data = new BarData(dataSets);
        data.setValueFormatter(new MyValueFormatter());
        data.setDrawValues(false);

        happinessChart.setData(data);
        happinessChart.animateY(1000);
        happinessChart.setTouchEnabled(false);
        Description description = new Description();
        description.setText("");
        happinessChart.setDescription(description);
        Legend legend = happinessChart.getLegend();
        legend.setEnabled(false);

        final String[] level = new String[]{"", "Very Sad", "Sad", "Neutral", "Happy", "Very Happy"};

        ValueFormatter YAxisFormatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return level[(int) value];
            }
        };

        XAxis xAxis = happinessChart.getXAxis();
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(XAxisFormatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0);

        YAxis yAxis = happinessChart.getAxisLeft();
        yAxis.setLabelCount(6, true);
        yAxis.setValueFormatter(YAxisFormatter);
        yAxis.setAxisMaximum(5);
        yAxis.setAxisMinimum(0);

        YAxis yAxis1 = happinessChart.getAxisRight();
        yAxis1.setEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class MyValueFormatter extends ValueFormatter {

    }
}
