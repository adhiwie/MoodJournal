package com.adhiwie.moodjournal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.Manifest;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.app.OnNavigationBlockedListener;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.heinrichreimersoftware.materialintro.slide.Slide;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class AppIntroActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_intro);
        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_1)
                .description(R.string.intro_desc_1)
                .background(R.color.light)
                .backgroundDark(R.color.colorAccent)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_2)
                .description(R.string.intro_desc_2)
                .background(R.color.light)
                .backgroundDark(R.color.colorAccent)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_3)
                .description(R.string.intro_desc_3)
                .background(R.color.light)
                .backgroundDark(R.color.colorAccent)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_4)
                .description(R.string.intro_desc_4)
                .background(R.color.light)
                .backgroundDark(R.color.colorAccent)
                .scrollable(false)
                .permissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION})
                .build());

        setButtonBackVisible(false);

        addOnNavigationBlockedListener(new OnNavigationBlockedListener() {
            @Override
            public void onNavigationBlocked(int position, int direction) {
                View contentView = findViewById(android.R.id.content);
                if (contentView != null) {
                    if (position == 3) {
                        Snackbar.make(contentView, "Please grant a permission for the location", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
