package com.adhiwie.diary.user.permission;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.adhiwie.diary.R;
import com.adhiwie.diary.exception.IncompatibleAPIException;

public class RuntimePermission {

    private final Context context;

    public RuntimePermission(Context context) throws IncompatibleAPIException {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            throw new IncompatibleAPIException();
        this.context = context;
    }


    @SuppressLint("NewApi")
    public boolean hasPermission(String p) {
        int p_code = context.checkSelfPermission(p);
        return p_code == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isRationalRequired(Activity a, String p) {
        return ActivityCompat.shouldShowRequestPermissionRationale(a, p);
    }

    public void showRational(Activity a) {
        final Dialog dialog = new Dialog(a);
        dialog.setContentView(R.layout.popup_screen);
        dialog.setTitle("Permission Required");
        dialog.setCancelable(false);

        TextView tv_message = (TextView) dialog.findViewById(R.id.message);
        tv_message.setText("The app won't work if you will not enable the requested permissions. "
                + "Go to settings page and enable all permissions.");

        Button ok = (Button) dialog.findViewById(R.id.ok);
        ok.setText("Enable All Permissions");
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void askPermissions(Activity a, String[] p) {
        ActivityCompat.requestPermissions(a, p, 123);
    }

    public ArrayList<String> getMissingPermissions(String[] required_permissions) {
        ArrayList<String> missing_permissions = new ArrayList<String>();
        for (String p : required_permissions)
            if (!hasPermission(p))
                missing_permissions.add(p);
        return missing_permissions;
    }

}
