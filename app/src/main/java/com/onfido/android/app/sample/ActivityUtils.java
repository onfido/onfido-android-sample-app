package com.onfido.android.app.sample;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ricardo.freitas on 14/11/16.
 */

public class ActivityUtils {
    static public void setFragment(AppCompatActivity activity, final Fragment fragment) {
        if (activity.isFinishing()) {
            return;
        }

        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, fragment);
        ft.commitAllowingStateLoss();
    }
}
