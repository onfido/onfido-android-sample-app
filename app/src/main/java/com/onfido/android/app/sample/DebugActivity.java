package com.onfido.android.app.sample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.L;
import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.OnfidoConfig;
import com.onfido.android.sdk.capture.OnfidoFactory;
import com.onfido.api.client.OnfidoAPI;
import com.onfido.api.client.OnfidoAPIFactory;
import com.onfido.api.client.data.Address;
import com.onfido.api.client.data.Applicant;
import com.onfido.api.client.data.Check;
import com.onfido.api.client.data.DocType;
import com.onfido.api.client.data.DocumentUpload;
import com.onfido.api.client.data.ErrorData;
import com.onfido.api.client.data.Report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class DebugActivity extends AppCompatActivity {

    static final String TOKEN = "ONFIDO_API_TOKEN";
    
    private Onfido client;

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText applicantId;
    private EditText checkId;
    private Button applicantButton;
    private Button uploadButton;
    private Button checkButton;
    private Button statusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        client = OnfidoFactory.create(this).getClient();
        final OnfidoConfig.Builder builder = OnfidoConfig.builder();

        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        email = (EditText) findViewById(R.id.email);
        applicantId = (EditText) findViewById(R.id.applicantId);
        checkId = (EditText) findViewById(R.id.checkId);
        applicantButton = (Button) findViewById(R.id.applicantButton);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        checkButton = (Button) findViewById(R.id.checkButton);
        statusButton = (Button) findViewById(R.id.statusButton);

        client = OnfidoFactory.create(this).getClient();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(client.createIntent(builder.build()));
            }
        });

        applicantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doExecuteCreateApplicantRequest();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doExecuteUploadRequest();
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doExecuteCheckRequest();
            }
        });

        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doExecuteStatusRequest();
            }
        });

    }

    private void doExecuteCreateApplicantRequest() {
        executeApplicantRequest(
                firstName.getText().toString(),
                lastName.getText().toString(),
                email.getText().toString()
        );
    }

    private void executeApplicantRequest(String first, String last, String email) {
        OnfidoAPI interactor = OnfidoAPIFactory.create(TOKEN);

        Address address = Address.builder()
                .withCountry(Locale.UK)
                .withFlatNumber("5")
                .withTown("London")
                .withPostcode("E4 555")
                .build();

        Applicant.Builder applicantBuilder = Applicant.builder()
                .withFirstName(first)
                .withLastName(last)
                .withAddresses(Collections.singletonList(address))
                .withDateOfBirth(new GregorianCalendar(1974, 04, 25).getGregorianChange());

        interactor.create(
                applicantBuilder.build(),
                new OnfidoAPI.Listener<Applicant>() {
                    @Override
                    public void onSuccess(Applicant applicant) {
                        L.d(DebugActivity.this, "REQTST", "success!");
                        applicantId.setText(applicant.getId());
                    }

                    @Override
                    public void onFailure() {

                    }

                    @Override
                    public void onError(ErrorData errorData) {
                        L.d(DebugActivity.this, "REQTST", "ERROR: " + errorData.getMessage());
                    }
                }
        );
    }

    private void doExecuteUploadRequest() {
        byte[] data = getData();
        Applicant applicant = Applicant.builder()
                .withId(applicantId.getText().toString())
                .build();
        executeUploadRequest(applicant, data);
    }

    private void executeUploadRequest(Applicant applicant, byte[] data) {
        OnfidoAPI interactor = OnfidoAPIFactory.create(TOKEN);
        interactor.upload(
                applicant,
                "img.jpg",
                DocType.NATIONAL_ID_CARD, // ex. "national_identity_card"
                "image/jpeg",
                data,
                new OnfidoAPI.Listener<DocumentUpload>() {
                    @Override
                    public void onSuccess(DocumentUpload documentUpload) {
                        L.d(DebugActivity.this, "REQTST", "success! " + documentUpload.getId());
                    }

                    @Override
                    public void onFailure() {

                    }

                    @Override
                    public void onError(ErrorData errorData) {
                        String message = errorData == null ? "null error data" : errorData.getMessage();
                        L.d(DebugActivity.this, "REQTST", "ERROR: " + message);
                    }
                }
        );
    }

    private byte[] getData() {
        InputStream inputStream = getResources().openRawResource(R.raw.jpg);
        int length = 0;

        try {
            length = inputStream.available();
        } catch (IOException e) {
            L.d("REQTST", "input stream: " + e.getMessage());
        }

        ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[length];
        int i = Integer.MAX_VALUE;
        try {
            while ((i = inputStream.read(buff, 0, buff.length)) > 0) {
                bufferedOutputStream.write(buff, 0, i);
            }
        } catch (IOException e) {
            L.d("REQTST", "write: " + e.getMessage());
        }

        return bufferedOutputStream.toByteArray();
    }

    private void doExecuteCheckRequest() {
        Applicant applicant = Applicant.builder().withId(applicantId.getText().toString()).build();
        List<Report> reports = Arrays.asList(new Report(Report.Type.IDENTITY));
        executeCheckRequest(applicant, reports);
    }

    private void executeCheckRequest(Applicant applicant, List<Report> reports) {
        OnfidoAPI interactor = OnfidoAPIFactory.create(TOKEN);
        interactor.check(applicant, Check.Type.EXPRESS, reports, new OnfidoAPI.Listener<Check>() {
            @Override
            public void onSuccess(Check check) {
                L.d(DebugActivity.this, "success!");
                checkId.setText(check.getId());
            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onError(ErrorData errorData) {
                String message = errorData == null ? "null error data" : errorData.getMessage();
                L.d(DebugActivity.this, "ERROR: " + message);
            }
        });
    }

    private void doExecuteStatusRequest() {
        Applicant applicant = Applicant.builder()
                .withId(applicantId.getText().toString())
                .build();
        Check check = Check.builder().withId(checkId.getText().toString()).build();
        executeStatusRequest(applicant, check);
    }

    private void executeStatusRequest(Applicant applicant, final Check check) {
        OnfidoAPI interactor = OnfidoAPIFactory.create(TOKEN);
        interactor.checkStatus(applicant, check, new OnfidoAPI.Listener<Check>() {
            @Override
            public void onSuccess(Check updated) {
                L.d(DebugActivity.this, "success! status=" + updated.getStatus());
            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onError(ErrorData errorData) {
                String message = errorData == null ? "null error data" : errorData.getMessage();
                L.d(DebugActivity.this, "ERROR: " + message);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
