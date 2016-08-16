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
import com.onfido.android.sdk.capture.ui.options.FlowStep;
import com.onfido.android.sdk.capture.ui.options.MessageScreenOptions;
import com.onfido.api.client.data.Address;
import com.onfido.api.client.data.Applicant;
import com.onfido.api.client.data.Check;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private Onfido client;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Choose example option");
        client = OnfidoFactory.create(this).getClient();

        findViewById(R.id.tv_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(client.createIntent(getTestOnfidoConfigBuilder()
                        .withShouldCollectDetails(true)
                        .build()), 1);
            }
        });
        findViewById(R.id.tv_signup_async).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(client.createIntent(getTestOnfidoConfigBuilder()
                        .withAsyncCheck(true)
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
        findViewById(R.id.tv_account_async).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(client.createIntent(getTestOnfidoConfigBuilder()
                        .withAsyncCheck(true)
                        .withShouldCollectDetails(false)
                        .build()), 1);
            }
        });

        final FlowStep[] flowSteps = new FlowStep[]{FlowStep.CAPTURE_DOCUMENT,FlowStep.CAPTURE_FACE};
        findViewById(R.id.tv_custom_flow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(client.createIntent(getTestOnfidoConfigBuilder()
                        .withCustomFlow(flowSteps)
                        .build()), 1);
            }
        });

        final FlowStep[] flowStepsWithOptions = new FlowStep[]{
                new FlowStep(new MessageScreenOptions("Test title","Description","NEXT Button!")),
                FlowStep.CAPTURE_DOCUMENT,
                FlowStep.CAPTURE_FACE
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
                final Check check = client.extractCheckResult(data);
                Toast.makeText(this, "Success. Result: " + check.getResult() + ". Status: " + check.getStatus(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "User cancelled.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private OnfidoConfig.Builder getTestOnfidoConfigBuilder() {
        return OnfidoConfig.builder().withSyncWaitTime(30).withApplicant(getTestApplicant());
    }

    @NonNull
    private static Applicant getTestApplicant() {
        final List<Address> addressList = new ArrayList<>();
        addressList.add(Address.builder()
                .withCountry(Locale.UK)
                .withFlatNumber("5")
                .withTown("London")
                .withPostcode("E4 555")
                .build()
        );
        return Applicant.builder()
                .withFirstName("deineir")
                .withLastName("oi3i3")
                .withDateOfBirth(new GregorianCalendar(1974, 04, 25).getGregorianChange())
                .withAddresses(addressList).build();
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
}
