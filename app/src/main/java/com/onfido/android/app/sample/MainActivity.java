package com.onfido.android.app.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.onfido.android.sdk.capture.DateOfBirthConverter;
import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.OnfidoConfig;
import com.onfido.android.sdk.capture.OnfidoFactory;
import com.onfido.api.client.data.Address;
import com.onfido.api.client.data.Applicant;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

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
                client.start(getTestOnfidoConfigBuilder()
                        .withShouldCollectDetails(true)
                        .build());
            }
        });
        findViewById(R.id.tv_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.start(getTestOnfidoConfigBuilder()
                        .withShouldCollectDetails(false)
                        .build());
            }
        });
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
                .withDateOfBirth(DateOfBirthConverter.newInstance(1958, 10, 10).asString())
                .withEmail(new Random().nextInt(100000) + "me@me.com")
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
