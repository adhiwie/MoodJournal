package com.adhiwie.moodjournal.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.adhiwie.moodjournal.R;

public class Popup {

    public void showPopup(Context context, String title, String message) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_screen);
        dialog.setTitle(title);

        TextView tv_message = (TextView) dialog.findViewById(R.id.message);
        tv_message.setText(message);

        Button ok = (Button) dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


}
