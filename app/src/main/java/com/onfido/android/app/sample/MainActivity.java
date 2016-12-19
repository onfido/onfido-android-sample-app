package com.onfido.android.app.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.OnfidoConfig;
import com.onfido.android.sdk.capture.OnfidoFactory;
import com.onfido.android.sdk.capture.ui.ErrorDialogFeature;
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

public class MainActivity extends AppCompatActivity implements ErrorDialogFeature.Listener {

    private Onfido client;
    ErrorDialogFeature errorDialogFeature;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Choose example option");

        errorDialogFeature = new ErrorDialogFeature();
        errorDialogFeature.attach(this);

        client = OnfidoFactory.create(this).getClient();

        findViewById(R.id.tv_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOnfidoActivity(getTestOnfidoConfigBuilder()
                        .withShouldCollectDetails(true)
                        .build());
            }
        });

        findViewById(R.id.tv_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOnfidoActivity(getTestOnfidoConfigBuilder()
                        .withShouldCollectDetails(false)
                        .build());
            }
        });

        final FlowStep[] flowSteps = new FlowStep[]{
                new MessageScreenStep("Welcome","This a custom standard flow","Start"),
                FlowStep.CAPTURE_DOCUMENT,
                FlowStep.CAPTURE_FACE,
                new MessageScreenStep("Thank you","","Close")
        };
        findViewById(R.id.tv_custom_flow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOnfidoActivity(getTestOnfidoConfigBuilder()
                        .withCustomFlow(flowSteps)
                        .build());
            }
        });

        final FlowStep[] flowStepsWithOptions = new FlowStep[]{
                new MessageScreenStep("Welcome","This is a custom flow with only a face capture","Start"),
                FlowStep.CAPTURE_FACE,
                new MessageScreenStep("Thank you","","Close")
        };
        findViewById(R.id.tv_custom_flow_options).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOnfidoActivity(getTestOnfidoConfigBuilder()
                        .withCustomFlow(flowStepsWithOptions)
                        .build());
            }
        });
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
            public void userExited(Applicant applicant, OnfidoAPI onfidoApi, OnfidoConfig config) {
                showToast("User cancelled.");
            }
        });
    }

    private void startOnfidoActivity(OnfidoConfig config){
        client.startActivityForResult(this, 1, config);
    }

    private void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void startCheck(OnfidoConfig config, Applicant applicant, OnfidoAPI onfidoAPI){
        List flowSteps = Arrays.asList(config.getFlowSteps());

        final List<Report> currentReports = new ArrayList<>();
        if (flowSteps.contains(FlowStep.CAPTURE_DOCUMENT)) {
            currentReports.add(new Report(Report.Type.DOCUMENT));
        }
        if (flowSteps.contains(FlowStep.CAPTURE_FACE)) {
            currentReports.add(new Report(Report.Type.IDENTITY));
        }

        onfidoAPI.check(applicant, Check.Type.EXPRESS, currentReports, new OnfidoAPI.Listener<Check>() {
                    @Override
                    public void onSuccess(Check check) {
                        showToast("Success. Result: " + check.getResult() + ". Status: " + check.getStatus());
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

    private void showErrorMessage(String message){
        errorDialogFeature.show(message, MainActivity.this);
    }

    private static OnfidoConfig.Builder getTestOnfidoConfigBuilder() {
        return ActivityUtils.getTestOnfidoConfigBuilder();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, DebugActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onErrorDialogClose() {

    }
}
