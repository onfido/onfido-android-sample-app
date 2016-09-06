package com.onfido.android.app.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.OnfidoConfig;
import com.onfido.android.sdk.capture.OnfidoFactory;
import com.onfido.api.client.data.Check;

public class DemoActivity extends AppCompatActivity {

    private Onfido client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = OnfidoFactory.create(this).getClient();
        setContentView(R.layout.demo_main);

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(client.createIntent(OnfidoConfig.builder().withSyncWaitTime(30)
                        .withShouldCollectDetails(true)
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
                startActivity(new Intent().setClass(DemoActivity.this,FinalActivity.class));
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "User cancelled.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
