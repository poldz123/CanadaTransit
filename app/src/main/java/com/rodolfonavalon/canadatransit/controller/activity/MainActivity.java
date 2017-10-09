package com.rodolfonavalon.canadatransit.controller.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.rodolfonavalon.canadatransit.R;
import com.rodolfonavalon.canadatransit.controller.transit.TransitLand;
import com.rodolfonavalon.canadatransit.model.database.Operator;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TransitLand.retrieveOperators(operators -> {
            for (Operator operator : operators) {
                Log.d("OPERATOR", operator.getName());
            }
            Log.d("OPERATOR", "====DONE====");
        }, error -> {
            Timber.e("Error");
        });
    }

}
