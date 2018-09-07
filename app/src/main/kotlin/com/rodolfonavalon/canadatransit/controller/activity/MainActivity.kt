package com.rodolfonavalon.canadatransit.controller.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rodolfonavalon.canadatransit.R
import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.controller.util.DatabaseUtil
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dao = CanadaTransitApplication.appDatabase.transitLandDao()
//        DatabaseUtil.query(dao.loadOperators(), { operators ->
//            Timber.d("Operators: ${operators.count()}")
//            for (operator in operators) {
//                if (operator.operatorOneStopId == "o-f24-octranspo") {
//                    Timber.d("Tags is null")
//                }
//            }
//        }, ::onError)

        UpdateManager.updateOperators()

//        TransitLandApi.retrieveOperators(::onOperatorsRetrieved, ::onError)
    }

    fun onOperatorsRetrieved(operators: List<Operator>) {
        Timber.d("Was able to download operators: " + operators.count())
        for (operator in operators) {
            TransitLandApi.retrieveOperatorFeed(operator, { operatorFeeds ->
                onOperatorFeedRetrieved(operator, operatorFeeds)
            }, ::onError)
        }
    }

    fun onOperatorFeedRetrieved(operator: Operator, operatorFeeds: List<OperatorFeed>) {
        if (operatorFeeds.isEmpty()) {
            Timber.d("Empty operator feeds, will not use it.")
            return
        }
        // Retrieve the first operator feed within the operator, we do not want other
        // feeds since most of them are identical.
        val operatorFeed = operatorFeeds.first()
        // TODO save the operator and operator feed version
//        TransitLandApi.retrieveOperatorFeedVersion(operatorFeed, ::onOperatorFeedVersionRetreived, ::onError)
    }

    fun onOperatorFeedVersionRetreived(operatorFeedVersion: OperatorFeedVersion) {
        Timber.d("Was able to download the operator feed version for OC-Transpo")
        operatorFeedVersion.download()
    }

    fun onError(throwable: Throwable) {
        Timber.e(throwable)
    }
}
