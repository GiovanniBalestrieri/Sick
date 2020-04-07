/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.location.sample.locationaddress;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Asynchronously handles an intent using a worker thread. Receives a ResultReceiver object and a
 * location through an intent. Tries to fetch the address for the location using a Geocoder, and
 * sends the result to the ResultReceiver.
 */
public class FetchAddressIntentService extends IntentService {
    private static final String TAG = "FetchAddressIS";

    /**
     * The receiver where results are forwarded from this service.
     */
    private ResultReceiver mReceiver;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public FetchAddressIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    /**
     * Tries to get the location address using a Geocoder. If successful, sends an address to a
     * result receiver. If unsuccessful, sends an error message instead.
     * Note: We define a {@link android.os.ResultReceiver} in * MainActivity to process content
     * sent from this service.
     *
     * This service calls this method from the default worker thread with the intent that started
     * the service. When this method returns, the service automatically stops.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        // Check if receiver was properly registered.
        if (mReceiver == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

        // Make sure that the location data was really sent over through an extra. If it wasn't,
        // send an error error message and return.
        if (location == null) {
            errorMessage = getString(R.string.no_location_data_provided);
            Log.wtf(TAG, errorMessage);
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            return;
        }

        StringBuilder bundleLocation  = new StringBuilder(100);
        bundleLocation.append(Double.toString(location.getLongitude()));
        bundleLocation.append(",");
        bundleLocation.append(Double.toString(location.getLatitude()));
        bundleLocation.append(",");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String data = simpleDateFormat.format(new Date());
        bundleLocation.append(data);

        Log.d("AAA",bundleLocation.toString());

        deliverResultToReceiver(Constants.SUCCESS_RESULT,bundleLocation.toString());
    }

    /**
     * Sends a resultCode and message to the receiver.
     */
    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
}
