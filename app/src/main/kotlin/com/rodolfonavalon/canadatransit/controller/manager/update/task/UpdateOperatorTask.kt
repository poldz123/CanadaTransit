package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import com.rodolfonavalon.canadatransit.controller.util.extension.dbInsert
import com.rodolfonavalon.canadatransit.controller.util.queue.task.AbstractObservableTask
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import timber.log.Timber

class UpdateOperatorTask(private val updateManager: UpdateManager) : AbstractObservableTask<List<Operator>>() {

    override fun onStart(trackingId: String) {
        super.onStart(trackingId)
        Timber.d("Retrieving operators...")
        this.disposables.add(TransitLandApi.retrieveOperators(::onOperatorsReceived, ::onError))
    }

    private fun onOperatorsReceived(operators: List<Operator>) {
        if (operators.isEmpty()) {
            Timber.d("No operators was found, this could mean that the API has a BUG.")
            onOperatorsSaved(operators)
            return
        }

        Timber.d("Saving ${operators.count()} operators...")
        val dao = CanadaTransitApplication.appDatabase.operatorDao()
        dao.dbInsert {
            insert(operators)
        }.subscribe({ rowIds ->
            DebugUtil.assertTrue(rowIds.isNotEmpty(), "There are no operators being saved on a successful database transaction: $trackingId")
            onOperatorsSaved(operators)
        }, ::onError)
    }

    private fun onOperatorsSaved(operators: List<Operator>) {
        Timber.d("Successfully saved ${operators.count()} operators")
//
//        // TODO: remove block
//        val dao = CanadaTransitApplication.appDatabase.userOperatorsDao()
//        for (operator in operators) {
//            if (operator.operatorOneStopId == "o-f24-octranspo") {
//                dao.dbInsert {
//                    insert(UserOperators(operator.operatorOneStopId))
//                }.subscribe {
//                    Timber.d("o-f24-octranspo selected by the user")
//                    dao.dbQuery {
//                        load()
//                    }.subscribe { data ->
//                        Timber.d("Total user selected: ${data.count()}")
//                    }
//                }
//                break
//            }
//        }

        this.observable.onNext(operators)
        this.observable.onComplete()
        updateManager.success()
    }
}