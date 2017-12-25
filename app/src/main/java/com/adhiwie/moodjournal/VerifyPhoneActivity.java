package com.adhiwie.moodjournal;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.adhiwie.moodjournal.model.UserData;
import com.adhiwie.moodjournal.service.KeepAppRunningService;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Map;

public class VerifyPhoneActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ConstraintLayout constraintLayout;

    private static final int REQUEST_CODE_SIGN_IN = 123;

    @Override
    public void onStart() {
        super.onStart();

        //checkGoogleApi();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        constraintLayout = findViewById(R.id.constraintLayout);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(VerifyPhoneActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build())
                        ).build(),
                REQUEST_CODE_SIGN_IN
        );
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
    }

    protected void showSnackbar(String message) {
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void checkGoogleApi() {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int result = googleApi.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This app requires google play services").setTitle("Do you want to update?");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    final String appPackageName = "com.google.android.gms";
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+appPackageName)));
                    }
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            builder.create().show();
        }
    }

    private void writeUserDataToFirebase(final FirebaseUser mUser){

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("users").child(mUser.getUid()).getValue() == null) {
                    UserData userData = new UserData("", "IF it is in the evening, THEN I will complete the mood questionnaires", 0, 0, 0, 0, "", "", 0.0, 0.0, 0, 0, 0);
                    Map<String, Object> userDataValue = userData.toMap();
                    dbRef.child("users").child(mUser.getUid()).updateChildren(userDataValue);
                }

                startService(new Intent(getApplicationContext(), KeepAppRunningService.class));

                Intent intent = new Intent(VerifyPhoneActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
