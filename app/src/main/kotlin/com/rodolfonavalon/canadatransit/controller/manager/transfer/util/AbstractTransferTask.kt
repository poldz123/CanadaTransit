package com.rodolfonavalon.canadatransit.controller.manager.transfer.util

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.transfer.TransferManager
import com.rodolfonavalon.canadatransit.controller.manager.transfer.TransferTask
import com.rodolfonavalon.canadatransit.controller.manager.transfer.Transferable
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import com.rodolfonavalon.canadatransit.controller.util.FileUtil
import io.reactivex.disposables.Disposable
import timber.log.Timber

abstract class AbstractTransferTask<T : Transferable>(private val transferManager: TransferManager, private val transferable: T) : TransferTask {
    var disposable: Disposable? = null
    var trackingId: String = ""

    override fun onStart(trackingId: String) {
        DebugUtil.assertMainThread()
        this.trackingId = trackingId
    }

    override fun onCancel() {
        DebugUtil.assertMainThread()
        Timber.d("TransferTask has been CANCELLED: ${transferable.trackingId()}")
        // delete the temp file if exist
        FileUtil.createFile(CanadaTransitApplication.appContext, transferable).delete()
        // Dispose the retrofit call
        disposable?.dispose()
    }

    override fun onError(error: Throwable) {
        DebugUtil.assertMainThread()
        Timber.e(error, "TransferTask has FAILED: ${transferable.trackingId()}")
        // Error means that we are cancelling the task
        onCancel()
        // Trigger a failure task within the manager
        transferManager.failure()
    }

    override fun onProgress(property: TransferForwardingProperty) {
        DebugUtil.assertMainThread()
        DebugUtil.assertFalse(property.transferred, "Entity progress is triggered where it was already transferred")
        // TODO Need to implement the feed progress
    }
}
