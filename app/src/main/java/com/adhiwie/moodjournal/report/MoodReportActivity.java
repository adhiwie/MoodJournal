package com.adhiwie.moodjournal.report;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.SharedPref;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MoodReportActivity extends AppCompatActivity {

    LineChart stressChart;
    LineChart activenessChart;
    LineChart happinessChart;
    List<Entry> stressDataList;
    LineDataSet stressLineDataSet;
    List<Entry> activenessDataList;
    LineDataSet activenessLineDataSet;
    List<Entry> happinessDataList;
    LineDataSet happinessLineDataSet;
    List<JSONObject> dataInJsonObject;
    long currentTimeMillis;

    private Toolbar mTopToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mood_report);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        try {
            initialiseData();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void initialiseData() throws JSONException {
        SharedPref sharedPref = new SharedPref(getApplicationContext());
        String moodData = sharedPref.getString("DAILY_MOOD_REPORT_DATA");

        JSONArray jsonArray;

        stressDataList = new ArrayList<Entry>();
        activenessDataList = new ArrayList<Entry>();
        happinessDataList = new ArrayList<Entry>();
        dataInJsonObject = new ArrayList<JSONObject>();

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
        stressLineDataSet.clear();
        activenessLineDataSet.clear();
        happinessLineDataSet.clear();

        JSONArray jsonArray = getWeeklyData(timeInMillis);

        //Update chart data
        Entry entryForStressData;
        for (int i = 0; i < 7; i++) {
            try {
                jsonArray.get(i);
                entryForStressData = new Entry(i, jsonArray.getJSONObject(i).getInt("q1"));
            } catch (JSONException e) {
                e.printStackTrace();
                entryForStressData = new Entry(i, 0);
            }

            stressDataList.add(entryForStressData);
        }

        stressChart.notifyDataSetChanged();
        stressChart.animateY(1000);

        //Update chart data
        Entry entryForActivenessData;
        for (int i = 0; i < 7; i++) {
            try {
                jsonArray.get(i);
                entryForActivenessData = new Entry(i, jsonArray.getJSONObject(i).getInt("q2"));
            } catch (JSONException e) {
                e.printStackTrace();
                entryForActivenessData = new Entry(i, 0);
            }

            activenessDataList.add(entryForActivenessData);
        }

        activenessChart.notifyDataSetChanged();
        activenessChart.animateY(1000);

        //Update chart data
        Entry entryForHappinessData;
        for (int i = 0; i < 7; i++) {
            try {
                jsonArray.get(i);
                entryForHappinessData = new Entry(i, jsonArray.getJSONObject(i).getInt("q3"));
            } catch (JSONException e) {
                e.printStackTrace();
                entryForHappinessData = new Entry(i, 0);
            }

            happinessDataList.add(entryForHappinessData);
        }

        happinessChart.notifyDataSetChanged();
        happinessChart.animateY(1000);
    }

    private LineDataSet getWeeklyStressDataSet(long timeInMillis) throws JSONException {
        JSONArray jsonArray = getWeeklyData(timeInMillis);

        Entry entry;

        for (int i = 0; i < 7; i++) {
            try {
                jsonArray.get(i);
                entry = new Entry(i, jsonArray.getJSONObject(i).getInt("q1"));
            } catch (JSONException e) {
                e.printStackTrace();
                entry = new Entry(i, 0);
            }

            stressDataList.add(entry);
        }

        stressLineDataSet = new LineDataSet(stressDataList, "Stress Level");
        stressLineDataSet.setCircleRadius(5f);
        stressLineDataSet.setCircleColor(getResources().getColor(R.color.MediumSeaGreen));
        stressLineDataSet.setDrawCircleHole(false);
        stressLineDataSet.setColor(getResources().getColor(R.color.MediumSeaGreen));
        stressLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        return stressLineDataSet;
    }

    private void drawWeeklyStressChart(long timeInMillis) throws JSONException {
        stressChart = (LineChart) findViewById(R.id.stress_chart);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(getWeeklyStressDataSet(timeInMillis));

        final LineData data = new LineData(dataSets);
        data.setValueFormatter(new ValueFormatter());

        stressChart.setData(data);
        stressChart.animateY(1000);
        stressChart.setTouchEnabled(false);
        Description description = new Description();
        description.setText("");
        stressChart.setDescription(description);
        Legend legend = stressChart.getLegend();
        legend.setEnabled(false);

        final String[] days = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return days[(int) value];
            }
        };

        final String[] level = new String[]{"No Data", "Very Stressed", "Stressed", "Neutral", "Relaxed", "Very Relaxed"};

        IAxisValueFormatter formatter1 = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return level[(int) value];
            }
        };

        XAxis xAxis = stressChart.getXAxis();
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0);

        YAxis yAxis = stressChart.getAxisLeft();
        yAxis.setLabelCount(6, true);
        yAxis.setValueFormatter(formatter1);
        yAxis.setAxisMaximum(5);
        yAxis.setAxisMinimum(0);

        YAxis yAxis1 = stressChart.getAxisRight();
        yAxis1.setEnabled(false);
    }

    private LineDataSet getWeeklyActivenessDataSet(long timeInMillis) throws JSONException {
        JSONArray jsonArray = getWeeklyData(timeInMillis);

        Entry entry;

        for (int i = 0; i < 7; i++) {
            try {
                jsonArray.get(i);
                entry = new Entry(i, jsonArray.getJSONObject(i).getInt("q2"));
            } catch (JSONException e) {
                e.printStackTrace();
                entry = new Entry(i, 0);
            }

            activenessDataList.add(entry);
        }

        activenessLineDataSet = new LineDataSet(activenessDataList, "Activeness Level");
        activenessLineDataSet.setCircleRadius(5f);
        activenessLineDataSet.setCircleColor(getResources().getColor(R.color.Red));
        activenessLineDataSet.setDrawCircleHole(false);
        activenessLineDataSet.setColor(getResources().getColor(R.color.Red));
        activenessLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        return activenessLineDataSet;
    }

    private void drawActivenessChart(long timeInMillis) throws JSONException {
        activenessChart = (LineChart) findViewById(R.id.activeness_chart);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(getWeeklyActivenessDataSet(timeInMillis));

        final LineData data = new LineData(dataSets);
        data.setValueFormatter(new ValueFormatter());

        activenessChart.setData(data);
        activenessChart.animateY(1000);
        activenessChart.setTouchEnabled(false);
        Description description = new Description();
        description.setText("");
        activenessChart.setDescription(description);
        Legend legend = activenessChart.getLegend();
        legend.setEnabled(false);

        final String[] days = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return days[(int) value];
            }
        };

        final String[] level = new String[]{"No Data", "Very Sleepy", "Sleepy", "Neutral", "Active", "Very Active"};

        IAxisValueFormatter formatter1 = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return level[(int) value];
            }
        };

        XAxis xAxis = activenessChart.getXAxis();
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0);

        YAxis yAxis = activenessChart.getAxisLeft();
        yAxis.setLabelCount(6, true);
        yAxis.setValueFormatter(formatter1);
        yAxis.setAxisMaximum(5);
        yAxis.setAxisMinimum(0);

        YAxis yAxis1 = activenessChart.getAxisRight();
        yAxis1.setEnabled(false);
    }

    private LineDataSet getWeeklyHappinessDataSet(long timeInMillis) throws JSONException {
        JSONArray jsonArray = getWeeklyData(timeInMillis);

        Entry entry;

        for (int i = 0; i < 7; i++) {
            try {
                jsonArray.get(i);
                entry = new Entry(i, jsonArray.getJSONObject(i).getInt("q3"));
            } catch (JSONException e) {
                e.printStackTrace();
                entry = new Entry(i, 0);
            }

            happinessDataList.add(entry);
        }

        happinessLineDataSet = new LineDataSet(happinessDataList, "Happiness Level");
        happinessLineDataSet.setCircleRadius(5f);
        happinessLineDataSet.setCircleColor(getResources().getColor(R.color.Yellow));
        happinessLineDataSet.setDrawCircleHole(false);
        happinessLineDataSet.setColor(getResources().getColor(R.color.Yellow));
        happinessLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        return happinessLineDataSet;
    }

    private void drawHappinessChart(long timeInMillis) throws JSONException {
        happinessChart = (LineChart) findViewById(R.id.happiness_chart);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(getWeeklyHappinessDataSet(timeInMillis));

        final LineData data = new LineData(dataSets);
        data.setValueFormatter(new ValueFormatter());

        happinessChart.setData(data);
        happinessChart.animateY(1000);
        happinessChart.setTouchEnabled(false);
        Description description = new Description();
        description.setText("");
        happinessChart.setDescription(description);
        Legend legend = happinessChart.getLegend();
        legend.setEnabled(false);

        final String[] days = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return days[(int) value];
            }
        };

        final String[] level = new String[]{"No Data", "Very Sad", "Sad", "Neutral", "Happy", "Very Happy"};

        IAxisValueFormatter formatter1 = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return level[(int) value];
            }
        };

        XAxis xAxis = happinessChart.getXAxis();
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0);

        YAxis yAxis = happinessChart.getAxisLeft();
        yAxis.setLabelCount(6, true);
        yAxis.setValueFormatter(formatter1);
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

    public class ValueFormatter implements IValueFormatter {

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return "";
        }
    }
}
