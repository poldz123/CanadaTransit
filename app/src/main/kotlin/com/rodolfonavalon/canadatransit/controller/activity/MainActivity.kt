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
            if (operator.oneStopId == "o-f24-octranspo") {
                TransitLandApi.retrieveOperatorFeed(operator, ::onOperatorFeedRetrieved, ::onError)
                return
            }
        }
    }

    fun onOperatorFeedRetrieved(operatorFeeds: List<OperatorFeed>) {
        Timber.d("Was able to download operator feeds for OC-Transpo: " + operatorFeeds.count())
        TransitLandApi.retrieveOperatorFeedVersion(operatorFeeds.first(), ::onOperatorFeedVersionRetreived, ::onError)
    }

    fun onOperatorFeedVersionRetreived(operatorFeedVersion: OperatorFeedVersion) {
        Timber.d("Was able to download the operator feed version for OC-Transpo")
        operatorFeedVersion.download()
    }

    fun onError(throwable: Throwable) {
        Timber.e(throwable)
    }
}
