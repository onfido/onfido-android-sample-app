package com.onfido.android.app.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.onfido.android.sdk.capture.ExitCode;
import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.OnfidoConfig;
import com.onfido.android.sdk.capture.OnfidoFactory;
import com.onfido.android.sdk.capture.ui.ErrorDialogFeature;
import com.onfido.android.sdk.capture.ui.LoadingFragment;
import com.onfido.android.sdk.capture.ui.options.FlowStep;
import com.onfido.android.sdk.capture.ui.options.MessageScreenStep;
import com.onfido.api.client.OnfidoAPI;
import com.onfido.api.client.data.Applicant;
import com.onfido.api.client.data.Check;
import com.onfido.api.client.data.ErrorData;
import com.onfido.api.client.data.Report;

import java.util.ArrayList;
import java.util.List;

public class DemoActivity extends AppCompatActivity {

    private Onfido client;
    private ErrorDialogFeature errorDialogFeature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = OnfidoFactory.create(this).getClient();

        errorDialogFeature = new ErrorDialogFeature();
        errorDialogFeature.attach(this);

        setWelcomeScreen();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        client.handleActivityResult(requestCode, resultCode, data, new Onfido.OnfidoResultListener() {
            @Override
            public void userCompleted(Applicant applicant, OnfidoAPI onfidoAPI, OnfidoConfig onfidoConfig) {
                startCheck(onfidoConfig, applicant, onfidoAPI);
            }

            @Override
            public void userExited(ExitCode exitCode, Applicant applicant, OnfidoAPI onfidoApi, OnfidoConfig config) {
                showToast("User cancelled.");
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void startCheck(OnfidoConfig config, Applicant applicant, OnfidoAPI onfidoAPI){
        final List<Report> currentReports = new ArrayList<>();
        currentReports.add(new Report(Report.Type.DOCUMENT));
        currentReports.add(new Report(Report.Type.IDENTITY));

        setLoadingFragment(getString(com.onfido.android.sdk.capture.R.string.message_loading_identify_verification));

        onfidoAPI.check(applicant, Check.Type.EXPRESS, currentReports, new OnfidoAPI.Listener<Check>() {
                    @Override
                    public void onSuccess(Check check) {
                        completedCheck(check);
                    }

                    @Override
                    public void onFailure() {
                        showErrorMessage(getString(com.onfido.android.sdk.capture.R.string.error_connection_message));
                    }

                    @Override
                    public void onError(ErrorData errorData) {
                        showErrorMessage(errorData.getMessage());
                    }
                }
        );
    }

    private void setLoadingFragment(String message) {
        ActivityUtils.setFragment(this, LoadingFragment.createInstance(message));
    }

    private void closeLoadingScreen(){
        setWelcomeScreen();
    }

    private void setWelcomeScreen(){
        setContentView(R.layout.demo_main);

        final FlowStep[] flowStepsWithOptions = new FlowStep[]{
                new MessageScreenStep("Welcome","In the following steps you will be asked to perform a verification check","Start"),
                //FlowStep.APPLICANT_CREATE,
                FlowStep.CAPTURE_DOCUMENT,
                FlowStep.MESSAGE_FACE_VERIFICATION,
                FlowStep.CAPTURE_FACE,
                new MessageScreenStep("Thank you","We will use your captured document and face to perform a verification check","Start Check")
        };

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.startActivityForResult(DemoActivity.this, 1,
                        ActivityUtils.getTestOnfidoConfigBuilder()
                                .withCustomFlow(flowStepsWithOptions)
                                .build());
            }
        });
    }

    private void completedCheck(Check check){
        startActivity(new Intent().setClass(DemoActivity.this,FinalActivity.class));
        mCloseLoadingScreenOnExit = true;
    }

    private boolean mCloseLoadingScreenOnExit = false;

    @Override
    public void onStop() {
        super.onStop();
        if (mCloseLoadingScreenOnExit) closeLoadingScreen();
    }

    private void showErrorMessage(String message){
        closeLoadingScreen();
        errorDialogFeature.show(message);
    }
}
