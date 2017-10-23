package com.adhiwie.moodjournal;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.adhiwie.moodjournal.model.UserData;
import com.adhiwie.moodjournal.service.KeepAppRunning;
import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

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

    ProgressBar progressBar;

    public static final String PREF_KEY_FIRST_START = "PREF_KEY_FIRST_START";
    private static final int REQUEST_CODE_SIGN_IN = 123;
    private static final int REQUEST_CODE_INTRO = 100;
    private static final String TAG = "SignUpActivity" ;

    private KeepAppRunning s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_sign_up);

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

        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);

        setIndex(index);

        boolean firstStart = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_KEY_FIRST_START, true);

        if (firstStart) {
            Intent intent = new Intent(this, AppIntroActivity.class);
            startActivityForResult(intent, REQUEST_CODE_INTRO);
        }


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

    public void setIndex(int index) {
        Log.d(TAG, "Index: "+String.valueOf(index));
        titleTextView.setText(titles[index]);
        subtitleTextView.setText(subtitles[index]);
        questionTextView.setText(questions[index]);
    }

    public void showTimePicker(View v) {
        DialogFragment newFragment = new SignUpActivity.TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void onClick(View v) {
        switch (index) {
            case 1:
                setIndex(++index);
                name = nameEditText.getText().toString();
                nameEditText.setVisibility(View.GONE);
                timeButton.setVisibility(View.VISIBLE);
                questionTextView.setText(name+","+questionTextView.getText().toString());
                break;
            case 2:
                timeButton.setVisibility(View.GONE);
                action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signInAnonymously();
                    }
                });
                break;
        }
    }

    private void signInAnonymously() {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            writeUserDataToFirebase(user);
                        } else {
                            showSnackbar("Authentication failed.");
                        }
                    }
                });
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

        String plan = "IF the time is "+timeInString+", THEN I will track my mood.";
        UserData userData = new UserData(name, plan, 0, 0, 0, timeInMillis, timeInString, "", 0.0, 0.0, 1, 0, 0);
        Map<String, Object> userDataValue = userData.toMap();
        dbRef.child("users").child(mUser.getUid()).updateChildren(userDataValue, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    String urlPreTest1 = "https://docs.google.com/forms/d/e/1FAIpQLSdkXpxR8BZe2UMwwKcq1t18N9d9II0yuJy8GC-8eaUWUjl8hA/viewform?usp=pp_url&entry.503799090=";
                    String urlPreTest2 = "&entry.584420084&entry.788256975";

                    String urlPostTest1 = "https://docs.google.com/forms/d/e/1FAIpQLSeBrAkHZMGR2pre63OpyZLCLt5oVg78FyTvxiNf-t0_aTZC9Q/viewform?usp=pp_url&entry.369076977=";
                    String urlPostTest2 = "&entry.1519610942";
                    Intent intent = new Intent(SignUpActivity.this, QuestionnaireActivity.class);
                    intent.putExtra("url", urlPreTest1+mUser.getUid()+urlPreTest2);
                    //startActivity(intent);
                    //finish();
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }
        });
    }
}
