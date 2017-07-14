package com.adhiwie.moodjournal;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PlanActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private long notifiedAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView plan = (TextView) findViewById(R.id.plan);

                Map<String, Object> userDataValue = new HashMap<>();
                userDataValue.put("plan", plan.getText().toString());
                userDataValue.put("notified_at", notifiedAt);
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                dbRef.child("users").child(mUser.getUid()).updateChildren(userDataValue);

                Toast.makeText(getApplicationContext(), "Your plan has been changed.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(PlanActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getNotifiedAt(long notifiedAt) {
        this.notifiedAt = notifiedAt;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void onSetTimeClicked(View v){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(),"TimePicker");
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        private long notifiedAt;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            //Use the current time as the default values for the time picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            //Create and return a new instance of TimePickerDialog
            return new TimePickerDialog(getActivity(),this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        //onTimeSet() callback method
        public void onTimeSet(TimePicker view, int hourOfDay, int minute){
            //Do something with the user chosen time
            //Get reference of host activity (XML Layout File) TextView widget
            Button btn = (Button) getActivity().findViewById(R.id.time_button);

            //Set a message for user
            String mins = "";
            if(minute<10) {
                mins = "0"+String.valueOf(minute);
            } else {
                mins = String.valueOf(minute);
            }
            btn.setText(String.valueOf(hourOfDay)+ ":" + mins);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);
            notifiedAt = cal.getTimeInMillis();

            String planStr = "If it is ";
            String planStr2 = ", then I will complete the mood questionnaires";
            String str = planStr + btn.getText().toString() + planStr2;
            TextView plan = (TextView) getActivity().findViewById(R.id.plan);
            plan.setText(str);

            PlanActivity planActivity = (PlanActivity) getActivity();
            planActivity.getNotifiedAt(notifiedAt);
        }
    }

}
