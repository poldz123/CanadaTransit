package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.util.extension.dbQuery
import com.rodolfonavalon.canadatransit.controller.util.queue.task.AbstractObservableTask
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import timber.log.Timber

class UpdateOperatorFeedTask(updateManager: UpdateManager) : AbstractObservableTask<List<OperatorFeed>>() {
    val operatorFeedDao = CanadaTransitApplication.appDatabase.operatorFeedDao()
    val userOperatorDao = CanadaTransitApplication.appDatabase.userOperatorsDao()

    override fun onStart(trackingId: String) {
        super.onStart(trackingId)
        Timber.d("Retrieving operator feeds...")
//        operatorFeedDao.dbQuery {
//
//        }
//        this.disposables.add(
//
//        )
    }
}
