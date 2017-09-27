package com.adhiwie.moodjournal;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.adhiwie.moodjournal.model.UserData;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {


    private String[] titles;
    private String[] subtitles;
    private String[] questions;
    private TextView titleTextView;
    private TextView subtitleTextView;
    private TextView questionTextView;
    private EditText nameEditText;
    private Button timeButton;
    private Button action;

    private String name;
    private int index;
    private int hour;
    private int minute;
    private String timeInString;
    private long timeInMillis;

    private FirebaseAuth mAuth;
    private ConstraintLayout constraintLayout;

    public static final String PREF_KEY_FIRST_START = "PREF_KEY_FIRST_START";
    private static final int REQUEST_CODE_SIGN_IN = 123;
    private static final int REQUEST_CODE_INTRO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        constraintLayout = findViewById(R.id.constraintLayout);
        mAuth = FirebaseAuth.getInstance();
        index = 0;

        titles = getResources().getStringArray(R.array.onboard_titles);
        subtitles = getResources().getStringArray(R.array.onboard_subtitles);
        questions = getResources().getStringArray(R.array.questions);

        titleTextView = findViewById(R.id.title);
        subtitleTextView = findViewById(R.id.subtitle);
        questionTextView = findViewById(R.id.question);
        nameEditText = findViewById(R.id.your_name);
        timeButton = findViewById(R.id.set_time);
        action = findViewById(R.id.action);

        setIndex(index);


        /*
        boolean firstStart = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_KEY_FIRST_START, true);

        if (firstStart) {
            Intent intent = new Intent(this, AppIntroActivity.class);
            startActivityForResult(intent, REQUEST_CODE_INTRO);
        }
        */

        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        if (index == 0) {
            nameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId== EditorInfo.IME_ACTION_DONE){
                        setIndex(++index);
                        name = nameEditText.getText().toString();
                        nameEditText.setVisibility(View.GONE);
                        timeButton.setVisibility(View.VISIBLE);
                        questionTextView.setText(name+", "+questionTextView.getText().toString());
                    }
                    return false;
                }
            });
        }

    }

    private void setIndex(int index) {
        titleTextView.setText(titles[index]);
        subtitleTextView.setText(subtitles[index]);
        questionTextView.setText(questions[index]);
    }

    public void showTimePicker(View v) {
        DialogFragment newFragment = new SignUpActivity.TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void onClick(View v) {

        if (index < 2) {
            setIndex(++index);
        }

        if (index == 1) {
            name = nameEditText.getText().toString();
            nameEditText.setVisibility(View.GONE);
            timeButton.setVisibility(View.VISIBLE);
            questionTextView.setText(name+","+questionTextView.getText().toString());
        }

        if (index == 2) {
            timeButton.setVisibility(View.GONE);
            action.setText("Start phone verification");
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build())
                                    ).build(),
                            REQUEST_CODE_SIGN_IN
                    );
                }
            });
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            SignUpActivity signUpActivity = (SignUpActivity) getActivity();
            signUpActivity.setTime(hourOfDay, minute);
        }
    }

    private void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;

        String minuteInString;
        if (minute < 10) {
            minuteInString = "0" + String.valueOf(minute);
        } else {
            minuteInString = String.valueOf(minute);
        }
        timeButton.setText(String.valueOf(hour) + ":" + minuteInString);
        timeInString = timeButton.getText().toString();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        timeInMillis = cal.getTimeInMillis();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser user = mAuth.getCurrentUser();
                writeUserDataToFirebase(user);
                return;
            } else {

                if (response == null) {
                    showSnackbar("Sign in is cancelled.");
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar("Sign in failed. No internet connection.");
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar("Sign in failed. Unknown error.");
                    return;
                }
            }

            showSnackbar("Unknown sign in response.");
        }

        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(PREF_KEY_FIRST_START, false)
                        .apply();
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(PREF_KEY_FIRST_START, true)
                        .apply();
                //User cancelled the intro so we'll finish this activity too.
                finish();
            }
        }
    }

    protected void showSnackbar(String message) {
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void writeUserDataToFirebase(final FirebaseUser mUser){

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("users").child(mUser.getUid()).getValue() == null) {
                    String plan = "IF the time is "+timeInString+", THEN I will record my mood.";
                    UserData userData = new UserData(plan, 0, 0, 0, timeInMillis, timeInString, "", 0.0, 0.0);
                    Map<String, Object> userDataValue = userData.toMap();
                    dbRef.child("users").child(mUser.getUid()).updateChildren(userDataValue);
                }

                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
