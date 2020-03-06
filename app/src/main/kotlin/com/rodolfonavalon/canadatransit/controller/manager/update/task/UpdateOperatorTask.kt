package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.controller.util.extension.dbInsert
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.user.UserTransit
import io.reactivex.rxkotlin.addTo
import org.joda.time.DateTime
import timber.log.Timber

class UpdateOperatorTask : AbstractUpdateTask<List<Operator>>() {

    override fun onStart(trackingId: String) {
        super.onStart(trackingId)
        Timber.d("OPERATOR: Fetching operators...")
        TransitLandApi.retrieveOperators(
                ::onReceived,
                this::onError)
                .addTo(this.disposables)
    }

    private fun onReceived(operators: List<Operator>) {
        Timber.d("OPERATOR: Saving ${operators.count()} operators...")
        val dao = CanadaTransitApplication.appDatabase.operatorDao()
        dao.dbInsert {
            insert(operators)
        }.subscribe({ _ ->
            onSaved(operators)
        }, this::onError).addTo(this.disposables)
    }

    private fun onSaved(operators: List<Operator>) {
        var userTransits = mutableListOf<UserTransit>()
        val userDao = CanadaTransitApplication.appDatabase.userTransitDao()
        for (operator in operators) {
            userTransits.add(UserTransit(operator.operatorOneStopId, DateTime.now()))
        }
        Timber.d("Successfully saved ${operators.count()} operators")
        this.onSuccess(operators)
    }
}
