package com.js98012.vacplanner;

import android.app.Activity;
import android.widget.Toast;

public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Toast toast;
    private String p;
    private Activity activity;

    public BackPressCloseHandler(Activity context,String p) {
        this.activity = context;
        this.p=p;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity,
                "뒤로가기를 한번 더 누르면 "+p+"됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}

