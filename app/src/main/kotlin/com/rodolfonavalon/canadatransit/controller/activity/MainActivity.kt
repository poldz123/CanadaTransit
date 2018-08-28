package com.rodolfonavalon.canadatransit.controller.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rodolfonavalon.canadatransit.R
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TransitLandApi.retrieveOperators(::onOperatorsRetrieved, ::onError)
    }

    fun onOperatorsRetrieved(operators: List<Operator>) {
        Timber.d("Was able to download operators: " + operators.count())
        for (operator in operators) {
            TransitLandApi.retrieveOperatorFeed(operator, { operatorFeeds ->
                onOperatorFeedRetrieved(operator, operatorFeeds)
            }, ::onError)
            break
        }
    }

    fun onOperatorFeedRetrieved(operator: Operator, operatorFeeds: List<OperatorFeed>) {
        if (operatorFeeds.isEmpty()) {
            Timber.d("Empty operator feeds, will not use it.")
            // TODO: Remove the operator from the database when saving it.
            return
        }
        val operatorFeed = operatorFeeds.first()
        // A valid operator feed must have a feed version to point to the schedules
        if (operatorFeed.activeFeedVersion == null) {
            Timber.d("No active feed found, will not use it.")
            return
        }
        // TODO: save the operator and operator feed version
        TransitLandApi.retrieveOperatorFeedVersion(operatorFeed, ::onOperatorFeedVersionRetreived, ::onError)
    }

    fun onOperatorFeedVersionRetreived(operatorFeedVersion: OperatorFeedVersion) {
        Timber.d("Was able to download the operator feed version for OC-Transpo")
        operatorFeedVersion.download()
    }

    fun onError(throwable: Throwable) {
        Timber.e(throwable)
    }
}
