package com.adhiwie.moodjournal.user.data;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.adhiwie.moodjournal.ConsentMgr;
import com.adhiwie.moodjournal.MainActivity;
import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.communication.helper.RegistrationDataTransmission;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class UserRegistrationActivity extends AppCompatActivity {

    private ProgressDialog progress;
    private String email;
    private int age;
    private String occupation;
    private String gender;
    private TextView gender_error;
    private LinearLayout root_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        progress = new ProgressDialog(this);
        gender_error = (TextView) findViewById(R.id.gender_error);
        root_layout = (LinearLayout) findViewById(R.id.root_layout);

    }

    public void onSubmit(View v) {
        TextInputLayout emailTextInputLayout = (TextInputLayout) findViewById(R.id.email_text_input_layout);
        EditText email_edittext = (EditText) findViewById(R.id.email_address);
        TextInputLayout ageTextInputLayout = (TextInputLayout) findViewById(R.id.age_text_input_layout);
        EditText age_edittext = (EditText) findViewById(R.id.age);
        TextInputLayout occupationTextInputLayout = (TextInputLayout) findViewById(R.id.occupation_text_input_layout);
        EditText occupation_edittext = (EditText) findViewById(R.id.occupation);

        RadioGroup gender_group = (RadioGroup) findViewById(R.id.gender);
        int genderId = gender_group.getCheckedRadioButtonId();

        if (TextUtils.isEmpty(email_edittext.getText().toString()) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email_edittext.getText().toString()).matches()) {
            emailTextInputLayout.setError("Email address is not valid");
        } else if (TextUtils.isEmpty(String.valueOf(age_edittext.getText().toString()))) {
            ageTextInputLayout.setError("Age may not be empty");
        } else if (TextUtils.isEmpty(occupation_edittext.getText().toString())) {
            occupationTextInputLayout.setError("Occupation may not be empty");
        } else if (findViewById(genderId) == null) {
            gender_error.setVisibility(View.VISIBLE);
        } else {
            RadioButton gender_radiobutton = (RadioButton) findViewById(genderId);
            email = email_edittext.getText().toString();
            age = Integer.parseInt(age_edittext.getText().toString());
            occupation = occupation_edittext.getText().toString();
            gender = gender_radiobutton.getText().toString();
            gender_error.setVisibility(View.GONE);
            submitRegistrationData();
        }
    }

    private void submitRegistrationData() {
        try {
            progress.setTitle("Please wait");
            progress.setMessage("We are registering your data...");
            progress.setCancelable(false);
            progress.show();

            UserData ud = new UserData(getApplicationContext());
            ud.setEmail(this.email);
            ud.setAge(this.age);
            ud.setOccupation(this.occupation);
            ud.setGroupId(3);

            RegistrationDataTransmission rdt = new RegistrationDataTransmission(getApplicationContext());
            rdt.registerNow(new RegistrationDataTransmission.RegisterationResultListener() {

                @Override
                public void onResultAvailable(boolean result) {
                    if (result == false) {
                        new Snackbar(root_layout).shortLength("Unable to register you. Check the network connectivity on your device.");
                        return;
                    }
                    progress.dismiss();
                    new Snackbar(root_layout).shortLength("Registration successful.");
                    new ConsentMgr(getApplicationContext()).setConsentGiven();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            });
        } catch (Exception e) {
            new Snackbar(root_layout).shortLength("Oops.. Something went wrong.");
            new Log().e(e.toString());
        }
    }
}
