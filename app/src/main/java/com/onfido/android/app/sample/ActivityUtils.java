package com.onfido.android.app.sample;

import com.onfido.android.sdk.capture.OnfidoConfig;
import com.onfido.api.client.data.Address;
import com.onfido.api.client.data.Applicant;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by ricardo.freitas on 14/11/16.
 */

public class ActivityUtils {
    public static Applicant getTestApplicant() {
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

    public static OnfidoConfig.Builder getTestOnfidoConfigBuilder() {
        return OnfidoConfig.builder().withApplicant(getTestApplicant());
    }
}
