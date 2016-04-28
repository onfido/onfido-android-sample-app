package com.onfido.android.app.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.OnfidoConfig;
import com.onfido.android.sdk.capture.OnfidoFactory;

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
        final OnfidoConfig.Builder builder = OnfidoConfig.builder().withSyncWaitTime(5);

        findViewById(R.id.tv_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.withShouldCollectDetails(true);
                client.start(builder.build());
            }
        });
        findViewById(R.id.tv_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.withShouldCollectDetails(false);
                client.start(builder.build());
            }
        });
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
