package com.rodolfonavalon.canadatransit.controller.manager.transfer.download

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.transfer.Transfer
import com.rodolfonavalon.canadatransit.controller.manager.transfer.TransferManager
import com.rodolfonavalon.canadatransit.controller.manager.transfer.Transferable
import com.rodolfonavalon.canadatransit.controller.manager.transfer.util.TransferForwardingProperty
import com.rodolfonavalon.canadatransit.controller.manager.transfer.util.TransferForwardingSource
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import com.rodolfonavalon.canadatransit.controller.util.FileUtil
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import okio.Okio
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.io.IOException

/**
 * ObservableDownloader
 *
 * This should return an object that derived from [Transferable] which will be used
 * to download the file from the web.
 */
class ObservableDownloader(private val transferManager: TransferManager, private val entity: Transferable): Transfer.DownloadTransfer {
    var disposable: Disposable? = null

    override fun onStart() {
        Timber.v("Download entity has STARTED")
        DebugUtil.assertMainThread()
        this.disposable = entity.transferObservable()
                .flatMap(this::willDownload)
                .filter(TransferForwardingProperty::transferred)
                .doOnNext(this::onProgress)
                .map(TransferForwardingProperty::file)
                .subscribe(this::didDownload, this::onError)
    }

    override fun onCancel() {
        DebugUtil.assertMainThread()
        Timber.d("Download entity has been CANCELLED")
        // delete the temp file if exist
        FileUtil.createFile(CanadaTransitApplication.appContext, entity).delete()
        // Dispose the retrofit call
        disposable?.dispose()
    }

    override fun onError(error: Throwable) {
        Timber.e(error, "Download entity has FAILED")
        onCancel()
        transferManager.failure(entity.transferTrackingId())
    }

    override fun onProgress(property: TransferForwardingProperty) {
        DebugUtil.assertMainThread()
        DebugUtil.assertFalse(property.transferred, "Entity progress is triggered where it was already transferred")
        // TODO: Need to implement the feed progress
    }

    override fun willDownload(responseBody: Response<ResponseBody>): Observable<TransferForwardingProperty> {
        return Observable.create { emitter ->
            val body = responseBody.body()
            if (body != null) {
                DebugUtil.assertWorkerThread()
                // Forwarded source for reading the buffer input stream from retrofit
                val forwardingSource = TransferForwardingSource(body.source(), body.contentLength(), emitter)
                // Close the emitter once the buffer is completely consumed
                emitter.setCancellable(body::close)
                try {
                    // Create temp file to save the data
                    val tempFile = FileUtil.createFile(CanadaTransitApplication.appContext, entity, true)
                    // Read the buffer to the Forwarded Source
                    val sink = Okio.buffer(Okio.sink(tempFile))
                    sink.writeAll(forwardingSource)
                    sink.close()
                    // Copy and delete the temp file with the feed file
                    val feedFile = FileUtil.createFile(CanadaTransitApplication.appContext, entity)
                    tempFile.copyTo(feedFile, overwrite = true)
                    tempFile.delete()
                    // Complete the emitter with the file
                    emitter.onNext(TransferForwardingProperty(file = feedFile))
                    emitter.onComplete()
                } catch (ex: IOException) {
                    emitter.onError(ex)
                }
            } else {
                onError(UnknownError())
            }
        }
    }

    override fun didDownload(file: File?) {
        Timber.v("Entity download has been SUCCESSFUL")
        DebugUtil.assertMainThread()
        DebugUtil.assertTrue(file != null, "Entity file is null")
        transferManager.success(entity.transferTrackingId())
    }
}