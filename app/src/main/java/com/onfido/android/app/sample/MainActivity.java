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
import com.onfido.api.client.data.Address;
import com.onfido.api.client.data.Applicant;
import com.onfido.api.client.data.Check;
import com.onfido.api.client.data.ErrorData;
import com.onfido.api.client.data.Report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

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
                startActivityForResult(client.createIntent(getTestOnfidoConfigBuilder()
                        .withShouldCollectDetails(true)
                        .build()), 1);
            }
        });

        findViewById(R.id.tv_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(client.createIntent(getTestOnfidoConfigBuilder()
                        .withShouldCollectDetails(false)
                        .build()), 1);
            }
        });

        final FlowStep[] flowSteps = new FlowStep[]{
                new MessageScreenStep("Welcome","This a custom standard flow","Start"),
                FlowStep.APPLICANT_CREATE,
                FlowStep.CAPTURE_DOCUMENT,
                FlowStep.CAPTURE_FACE,
                FlowStep.SYNC_LOADING,
                new MessageScreenStep("Thank you","","Close")
        };
        findViewById(R.id.tv_custom_flow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(client.createIntent(getTestOnfidoConfigBuilder()
                        .withCustomFlow(flowSteps)
                        .build()), 1);
            }
        });

        final FlowStep[] flowStepsWithOptions = new FlowStep[]{
                new MessageScreenStep("Welcome","This flow only asks for document and face","Start"),
                //FlowStep.CAPTURE_DOCUMENT,
                FlowStep.CAPTURE_FACE,
                //FlowStep.SYNC_LOADING,
                new MessageScreenStep("Thank you","","Close")
        };
        findViewById(R.id.tv_custom_flow_options).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(client.createIntent(getTestOnfidoConfigBuilder()
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

        List flowSteps = Arrays.asList(config.getFlowSteps());

        final List<Report> currentReports = new ArrayList<>();
        if (flowSteps.contains(FlowStep.CAPTURE_DOCUMENT)) {
            currentReports.add(new Report(Report.Type.DOCUMENT));
        }
        if (flowSteps.contains(FlowStep.CAPTURE_FACE)) {
            currentReports.add(new Report(Report.Type.IDENTITY));
        }

        client.createOnfidoApiClient().check(applicant, Check.Type.EXPRESS, currentReports, new OnfidoAPI.Listener<Check>() {
                    @Override
                    public void onSuccess(Check check) {
                        Toast.makeText(MainActivity.this,
                                "Success. Result: " + check.getResult() + ". Status: " + check.getStatus(), Toast.LENGTH_LONG).show();
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

    public static OnfidoConfig.Builder getTestOnfidoConfigBuilder() {
        return OnfidoConfig.builder().withApplicant(getTestApplicant());
    }

    @NonNull
    private static Applicant getTestApplicant() {
        final List<Address> addressList = new ArrayList<>();
        addressList.add(Address.builder()
                .withCountry(Locale.UK)
                .withBuildingName("40")
                .withStreet("Long Acre")
                .withTown("London")
                .withPostcode("WC2E 9LG")
                .build()
        );
        return Applicant.builder()
                .withFirstName("Android User")
                .withLastName("Test")
                .withDateOfBirth(new GregorianCalendar(1974, 04, 25).getGregorianChange())
                .withAddresses(addressList)
                .build();
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
