package com.onfido.android.app.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.List;

public class DemoActivity extends AppCompatActivity {

    private Onfido client;
    private ErrorDialogFeature errorDialogFeature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = OnfidoFactory.create(this).getClient();
        setContentView(R.layout.demo_main);

        errorDialogFeature = new ErrorDialogFeature();
        errorDialogFeature.attach(this);

        final FlowStep[] flowStepsWithOptions = new FlowStep[]{
                new MessageScreenStep("Welcome","This flow only asks for document and face","Start"),
                FlowStep.CAPTURE_FACE,
                new MessageScreenStep("Thank you","","Close")
        };

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(client.createIntent(
                        MainActivity.getTestOnfidoConfigBuilder()
                        .withCustomFlow(flowStepsWithOptions)
                        .build()), 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                startCheck(data);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "User cancelled.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startCheck(Intent intent){
        final OnfidoConfig config   = client.getOnfidoConfigFrom(intent);
        final Applicant applicant   = client.getApplicantFrom(intent);
        final List flowSteps = Arrays.asList(config.getFlowSteps());

        final List<Report> currentReports = new ArrayList<>();
        currentReports.add(new Report(Report.Type.DOCUMENT));
        currentReports.add(new Report(Report.Type.IDENTITY));

        setLoadingFragment(getString(com.onfido.android.sdk.capture.R.string.message_loading_identify_verification));

        client.createOnfidoApiClient().check(applicant, Check.Type.EXPRESS, currentReports, new OnfidoAPI.Listener<Check>() {
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

    private void completedCheck(Check check){
        setContentView(R.layout.demo_main);
        Toast.makeText(DemoActivity.this,
                "Success. Result: " + check.getResult() + ". Status: " + check.getStatus(), Toast.LENGTH_LONG).show();
        startActivity(new Intent().setClass(DemoActivity.this,FinalActivity.class));
    }

    private void showErrorMessage(String message){
        errorDialogFeature.show(message, new ErrorDialogFeature.Listener() {
            @Override
            public void onErrorDialogClose() {

            }
        });
    }
}
