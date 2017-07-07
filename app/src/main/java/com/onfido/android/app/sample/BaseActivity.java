package com.onfido.android.app.sample;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;

import com.onfido.api.client.OnfidoAPI;

public class BaseActivity extends AppCompatActivity {

    final String SOURCE = "Android Sample App";
    OnfidoAPI onfidoAPI;

    String getVersionCode() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}
