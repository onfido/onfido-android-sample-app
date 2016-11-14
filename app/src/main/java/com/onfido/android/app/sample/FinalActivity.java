package com.onfido.android.app.sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.onfido.android.sdk.capture.ui.MessageFragment;
import com.onfido.android.sdk.capture.ui.NextActionListener;

public class FinalActivity extends AppCompatActivity implements NextActionListener {
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.final_holder);

        showFinalScreen();
    }
    
    public void showFinalScreen() {
        Fragment fragment = MessageFragment.createInstance(
                getString(R.string.message_title_finished),
                getString(R.string.welcome_bank_account),
                getString(R.string.result_bt_get_started)
        );
        setFragment(fragment);
    }

    private void setFragment(final Fragment fragment) {
        if (isFinishing()) {
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(com.onfido.android.sdk.capture.R.id.fl_content, fragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onNextClicked() {
        setFragment(SummaryFragment.createInstance());
    }
}
