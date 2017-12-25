package com.adhiwie.moodjournal;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.view.View;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.app.OnNavigationBlockedListener;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;


public class AppIntroActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setButtonBackVisible(false);
        setButtonNextVisible(true);

        setPageScrollDuration(500);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setPageScrollInterpolator(android.R.interpolator.fast_out_slow_in);
        }

        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_1)
                .description(R.string.intro_desc_1)
                .image(R.drawable.art_canteen_intro1)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.lightGrey)
                .layout(R.layout.slide_intro)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_2)
                .description(R.string.intro_desc_2)
                .image(R.drawable.art_canteen_intro1)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.lightGrey)
                .layout(R.layout.slide_intro)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_3)
                .description(R.string.intro_desc_3)
                .image(R.drawable.art_canteen_intro1)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.lightGrey)
                .layout(R.layout.slide_intro)
                .build());

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
}
