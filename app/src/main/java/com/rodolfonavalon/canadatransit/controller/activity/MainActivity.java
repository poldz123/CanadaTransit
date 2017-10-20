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
        TransitLand.retrieveOperators(this, operators -> {
            for (Operator operator : operators) {
                if ("Agence métropolitaine de transport".equals(operator.getName())) {
                    Timber.d(operator.getName() + " || Number of Operator: " + operator.getRepresentedInFeedOneStopIds().size());
                    retrieveOperatorVersion(operator);
                }
            }
        }, error -> {
            Timber.e("retrieveOperators Error");
        });
    }

    private void retrieveOperatorVersion(Operator operator) {
        TransitLand.retrieveOperatorFeed(this, operator, operatorFeeds -> {
//            Timber.d("Operator active version: " + operatorFeed.getActiveFeedVersion());
                Timber.d(operator.getName() + " || Number of OperatorFeed: " + operatorFeeds.size());
//            TransitLand.retrieveOperatorFeedVersion(MainActivity.this, operatorFeed, operatorFeedVersion -> {
//                Timber.d("Operator feed version download URL: " + operatorFeedVersion.getDownloadUrl());
//            }, error -> {
//                Timber.e("retrieveOperatorFeedVersion Error: " + error);
//            });
        }, error -> {
            Timber.e("retrieveOperatorVersion Error: " + error);
        });
    }
}
