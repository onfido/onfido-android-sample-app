package com.onfido.android.app.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onfido.android.sdk.capture.ui.BaseFragment;
import com.onfido.android.sdk.capture.ui.NextActionListener;

public class SummaryFragment extends BaseFragment {

    private NextActionListener nextActionListener;

    public static SummaryFragment createInstance() {
        SummaryFragment fragment = new SummaryFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            nextActionListener = (NextActionListener) activity;
        } catch (ClassCastException exception) {
            String activityName = activity.getClass().getSimpleName();
            throw new IllegalStateException("Activity \"" + activityName + "\" must implement NextActionListener interface.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        nextActionListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.summary_fragment, container, false);
        return rootView;
    }
}
