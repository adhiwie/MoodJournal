package com.adhiwie.moodjournal;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adhiwie.moodjournal.model.MoodModel;
import com.adhiwie.moodjournal.model.PHQModel;
import com.adhiwie.moodjournal.service.FetchAddressIntentService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import link.fls.swipestack.SwipeStack;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MoodQuestionActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private SwipeStack swipeStack;
    private boolean isMood = true;
    private View moodLayout;
    private View phqLayout;
    private List<Integer> phqScores;
    private Map<String, Object> moodValue;

    private Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    public AddressResultReceiver mResultReceiver;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_question);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        phqScores = new ArrayList<>();

        getLastLocation();

        final List<String> questionList = new ArrayList<>();
        final String[] questionArray = getResources().getStringArray(R.array.phq_questions);
        Collections.addAll(questionList, questionArray);

        moodLayout = findViewById(R.id.mood_layout);
        phqLayout = findViewById(R.id.phq_layout);

        if (isMood) {
            phqLayout.setVisibility(View.GONE);
            moodLayout.setVisibility(View.VISIBLE);
        } else {
            phqLayout.setVisibility(View.VISIBLE);
            moodLayout.setVisibility(View.GONE);
        }

        swipeStack = (SwipeStack) findViewById(R.id.swipeStack);

        if (swipeStack != null) {
            swipeStack.setAdapter(new SwipeStackAdapter(questionList));
            swipeStack.setListener(new SwipeStack.SwipeStackListener() {
                @Override
                public void onViewSwipedToLeft(int position) {
                    //Toast.makeText(getApplicationContext(), questionList.get(position), Toast.LENGTH_SHORT).show();
                    RadioGroup radioGroup = (RadioGroup) swipeStack.getTopView().findViewById(R.id.radioGroup);
                    int phqScore = -1;
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.option1:
                            phqScore = 0;
                            break;
                        case R.id.option2:
                            phqScore = 1;
                            break;
                        case R.id.option3:
                            phqScore = 2;
                            break;
                        case R.id.option4:
                            phqScore = 3;
                            break;
                    }
                    phqScores.add(phqScore);
                }

                @Override
                public void onViewSwipedToRight(int position) {
                    //Toast.makeText(getApplicationContext(), questionList.get(position), Toast.LENGTH_SHORT).show();
                    RadioGroup radioGroup = (RadioGroup) swipeStack.getTopView().findViewById(R.id.radioGroup);
                    int phqScore = -1;
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.option1:
                            phqScore = 0;
                            break;
                        case R.id.option2:
                            phqScore = 1;
                            break;
                        case R.id.option3:
                            phqScore = 2;
                            break;
                        case R.id.option4:
                            phqScore = 3;
                            break;
                    }
                    phqScores.add(phqScore);

                }

                @Override
                public void onStackEmpty() {
                    Calendar cal = Calendar.getInstance();
                    long timestamp = cal.getTimeInMillis();

                    double longitude;
                    double latitude;
                    String locAddress;

                    if (mLastLocation == null) {
                        longitude = 0;
                        latitude = 0;
                        locAddress = "";
                    } else {
                        longitude = mLastLocation.getLongitude();
                        latitude = mLastLocation.getLatitude();
                        locAddress = address;
                    }

                    com.adhiwie.moodjournal.model.Location location = new com.adhiwie.moodjournal.model.Location(longitude, latitude, locAddress);

                    PHQModel phqModel = new PHQModel(mUser.getUid(), timestamp, phqScores, location);
                    Map<String, Object> phqValue = phqModel.toMap();

                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                    dbRef.child("phq_score").push().setValue(phqValue);
                    dbRef.child("users").child(mUser.getUid()).child("daily_reminder_status").setValue(1);
                    dbRef.child("mood_score").push().setValue(moodValue);

                    Toast.makeText(getApplicationContext(), "Thanks. Your data has been recorded!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                if (isMood) {
                    SeekBar seekBar1 = (SeekBar) findViewById(R.id.seekBar);
                    SeekBar seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
                    SeekBar seekBar3 = (SeekBar) findViewById(R.id.seekBar3);

                    int happinessScore = seekBar1.getProgress() + 1;
                    int stressScore = seekBar2.getProgress() + 1;
                    int arousalScore = seekBar3.getProgress() + 1;

                    Calendar cal = Calendar.getInstance();
                    long timestamp = cal.getTimeInMillis();

                    com.adhiwie.moodjournal.model.Location location = new com.adhiwie.moodjournal.model.Location(mLastLocation.getLongitude(), mLastLocation.getLatitude(), address);

                    MoodModel moodModel = new MoodModel(mUser.getUid(), happinessScore, stressScore, arousalScore, timestamp, location);
                    moodValue = moodModel.toMap();

                    isMood = false;
                    phqLayout.setVisibility(View.VISIBLE);
                    moodLayout.setVisibility(View.GONE);
                } else {
                    RadioGroup radioGroup = (RadioGroup) swipeStack.getTopView().findViewById(R.id.radioGroup);
                    if (radioGroup.getCheckedRadioButtonId() == -1) {
                        Snackbar.make(view, "Sorry, you need to answer the question", Snackbar.LENGTH_SHORT).show();
                    } else {
                        swipeStack.swipeTopViewToRight();
                    }
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private class SwipeStackAdapter extends BaseAdapter {

        private List<String> mData;

        public SwipeStackAdapter(List<String> data) {
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.card, parent, false);
            TextView textViewCard = (TextView) convertView.findViewById(R.id.content);
            textViewCard.setText(mData.get(position));

            return convertView;
        }
    }

    protected void getLastLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mLastLocation = location;
                            startIntentService();
                        }
                    }
                });
    }

    protected void startIntentService() {
        mResultReceiver = new AddressResultReceiver(new Handler());
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    public class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            address = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY);

        }
    }

}
