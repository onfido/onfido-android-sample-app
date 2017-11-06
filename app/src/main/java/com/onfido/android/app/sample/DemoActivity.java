package com.onfido.android.app.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.onfido.android.sdk.capture.ExitCode;
import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.OnfidoFactory;
import com.onfido.android.sdk.capture.ui.ErrorDialogFeature;
import com.onfido.android.sdk.capture.ui.options.FlowStep;
import com.onfido.android.sdk.capture.ui.options.MessageScreenStep;
import com.onfido.android.sdk.capture.upload.Captures;
import com.onfido.api.client.data.Applicant;

public class DemoActivity extends BaseActivity {

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
        client.handleActivityResult(resultCode, data, new Onfido.OnfidoResultListener() {
            @Override
            public void userCompleted(Applicant applicant, Captures captures) {
                startCheck(applicant);
            }

            @Override
            public void userExited(ExitCode exitCode, Applicant applicant) {
                showToast("User cancelled.");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void startCheck(Applicant applicant) {
        //Call your back end to initiate the check

        completedCheck();
    }

    private void setWelcomeScreen() {
        setContentView(R.layout.demo_main);

        final FlowStep[] flowStepsWithOptions = new FlowStep[]{
                new MessageScreenStep("Welcome", "In the following steps you will be asked to perform a verification check", "Start"),
                FlowStep.CAPTURE_DOCUMENT,
                FlowStep.MESSAGE_FACE_VERIFICATION,
                FlowStep.CAPTURE_FACE,
                new MessageScreenStep("Thank you", "We will use your captured document and face to perform a verification check", "Start Check")
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

    private void completedCheck() {
        startActivity(new Intent().setClass(DemoActivity.this, FinalActivity.class));
    }
}
