package com.adhiwie.moodjournal.plan;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.adhiwie.moodjournal.MainActivity;
import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.communication.helper.PlanDataTransmission;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.file.FileMgr;
import com.adhiwie.moodjournal.user.data.UserData;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.SharedPref;

import org.json.JSONException;

import java.util.Calendar;

public class PlanActivity extends Activity {

    private final String CREATE_PLAN_STEP_NUMBER = "CREATE_PLAN_STEP_NUMBER";
    private int total_steps = 4;
    private int step;
    private String[] routines;
    private int routine;
    private SharedPref sp;
    private Button control_btn;
    private LinearLayout intro_layout;
    private LinearLayout step_1_layout;
    private LinearLayout step_2_layout;
    private LinearLayout step_3_layout;
    private ArrayAdapter<String> list_adapter;
    private TextView time_tv;
    private Button change_time;
    private TextView time_message;
    private int hour;
    private int minutes;
    private String routine_desc;
    private String timing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Drawable background;

        if(Build.VERSION.SDK_INT >= 21)
            background = getResources().getDrawable(R.drawable.blue_background, null);
        else
            background = getResources().getDrawable(R.drawable.blue_background);


        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(background);
        actionBar.setCustomView(R.layout.actionbar_layout);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);

        TextView actionbar_title = (TextView) findViewById(R.id.tvActionBarTitle);
        actionbar_title.setText(getResources().getString(R.string.title_activity_create_plan));

        setContentView(R.layout.activity_create_plan);

        if (getIntent().getIntExtra("step", 0) == 0) {
            initLayout();
        }

        if( !(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler) )
        {
            Thread.setDefaultUncaughtExceptionHandler( new CustomExceptionHandler(getApplicationContext()) );
        }

        sp = new SharedPref(getApplicationContext());

        if(!new PlanMgr(getApplicationContext()).isPlanGiven())
        {
            initLayout();
        }
    }

    private void initLayout(){
        step = 0;

        intro_layout = (LinearLayout) findViewById(R.id.intro_layout);
        step_1_layout = (LinearLayout) findViewById(R.id.step_1_layout);
        step_2_layout = (LinearLayout) findViewById(R.id.step_2_layout);
        step_3_layout = (LinearLayout) findViewById(R.id.step_3_layout);
        control_btn = (Button) findViewById(R.id.create_plan);

        intro_layout.setVisibility(View.VISIBLE);
        step_1_layout.setVisibility(View.GONE);
        step_2_layout.setVisibility(View.GONE);
        step_3_layout.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                switch (step) {
                    case 0:
                        this.finish();
                        break;
                    case 1:
                        initLayout();
                        break;
                    case 2:
                        goToStepOne();
                        break;
                    case 3:
                        goToStepTwo(routine);
                        break;

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onCreateButtonClick(View v) {
        goToStepOne();
    }

    private void goToStepOne() {
        step = 1;

        intro_layout.setVisibility(View.GONE);
        step_1_layout.setVisibility(View.VISIBLE);
        step_2_layout.setVisibility(View.GONE);
        step_3_layout.setVisibility(View.GONE);

        String[] timings = getResources().getStringArray(R.array.general_timings);
        list_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, timings);
        final ListView timing_list = (ListView) findViewById(R.id.timing_list);
        timing_list.setAdapter(list_adapter);
        timing_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                timing = timing_list.getItemAtPosition(position).toString().toLowerCase();
                routine = position;
                goToStepTwo(position);
            }
        });
    }



    private void goToStepTwo(int time_mode) {
        step = 2;

        intro_layout.setVisibility(View.GONE);
        step_1_layout.setVisibility(View.GONE);
        step_2_layout.setVisibility(View.VISIBLE);
        step_3_layout.setVisibility(View.GONE);
        switch (time_mode) {
            case 0:
                routines = getResources().getStringArray(R.array.morning_routines);
                break;
            case 1:
                routines = getResources().getStringArray(R.array.lunch_routines);
                break;
            case 2:
                routines = getResources().getStringArray(R.array.evening_routines);
                break;

        }
        list_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, routines);
        final ListView routine_list = (ListView) findViewById(R.id.routine_list);
        routine_list.setAdapter(list_adapter);
        routine_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                goToStepThree(routine_list.getItemAtPosition(position).toString().toLowerCase());
            }
        });
    }

    private void goToStepThree(String item) {
        step = 3;

        intro_layout.setVisibility(View.GONE);
        step_1_layout.setVisibility(View.GONE);
        step_2_layout.setVisibility(View.GONE);
        step_3_layout.setVisibility(View.VISIBLE);

        routine_desc = item;

        time_message = (TextView) findViewById(R.id.message);
        String msg = getResources().getString(R.string.create_plan_step_3_message, item);
        time_message.setText(msg);

        String timeString;
        switch (routine) {
            case 0:
                hour = 8;
                minutes = 0;
                timeString = "8:00";
                break;
            case 1:
                hour = 12;
                minutes = 0;
                timeString = "12:00";
                break;
            case 2:
                hour = 21;
                minutes = 0;
                timeString = "21:00";
                break;
            default:
                hour = 0;
                minutes = 0;
                timeString = "00:00";
                break;

        }

        time_tv = (TextView) findViewById(R.id.time_tv);
        time_tv.setText(timeString);

        change_time = (Button) findViewById(R.id.change_time);
        change_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment fragment = new TimePickerFragment();
                fragment.show(getFragmentManager(), "timePicker");
            }
        });

        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlanData data = new PlanData(new UserData(getApplicationContext()).getUuid(), timing, routine_desc, hour, minutes);

                PlanMgr planMgr = new PlanMgr(getApplicationContext());
                planMgr.setPlanHour(hour);
                planMgr.setPlanMinute(minutes);
                planMgr.setPlanGiven();
                planMgr.setPlanRoutineDesc(routine_desc);
                planMgr.setPlanTiming(timing);
                try {
                    planMgr.setPlanDataString(data.toJSONString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                PlanDataTransmission pt = new PlanDataTransmission(getApplicationContext());
                if(! pt.isDataTransmitted()) {
                    pt.transmitData();
                }

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    @SuppressLint("ValidFragment")
    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            hour = hourOfDay;
            minutes = minute;

            new Log().e("Hour of day"+hour);

            String mins;
            if (minute < 10) {
                mins = "0" + String.valueOf(minute);
            } else {
                mins = String.valueOf(minute);
            }

            time_tv.setText(String.valueOf(hourOfDay) + ":" + mins);

        }
    }

}
