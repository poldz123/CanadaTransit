package com.rodolfonavalon.canadatransit.controller.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.rodolfonavalon.canadatransit.R;
import com.rodolfonavalon.canadatransit.controller.transit.TransitLand;
import com.rodolfonavalon.canadatransit.model.database.Operator;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrieveOperators();
    }

    private void retrieveOperators() {
        TransitLand.retrieveOperators(operators -> {
            for (Operator operator : operators) {
                if (operator.getName().equals("OC Transpo")) {
                    retrieveOperatorVersion(operator);
                }
            }
        }, error -> {
            Timber.e("retrieveOperators Error");
        });
    }

    private void retrieveOperatorVersion(Operator operator) {
        TransitLand.retrieveOperatorFeed(operator, operatorFeed -> {
            Timber.d("Operator active version: " + operatorFeed.getActiveFeedVersion());
            TransitLand.retrieveOperatorFeedVersion(operatorFeed, operatorFeedVersion -> {
                Timber.d("Operator feed version download URL: " + operatorFeedVersion.getDownloadUrl());
            }, error -> {
                Timber.e("retrieveOperatorFeedVersion Error: " + error);
            });
        }, error -> {
            Timber.e("retrieveOperatorVersion Error: " + error);
        });
    }
}
