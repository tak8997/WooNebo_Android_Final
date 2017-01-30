package com.example.samsung.woonebo_android.util;

import android.app.Activity;
import android.widget.Toast;

import com.example.samsung.woonebo_android.scan.DeviceScan;

/**
 * Created by SAMSUNG on 2016-11-16.
 */

public class BackPressed {
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressed(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
            System.exit(0);
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity,
                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다..", Toast.LENGTH_SHORT);
        toast.show();
    }
}
