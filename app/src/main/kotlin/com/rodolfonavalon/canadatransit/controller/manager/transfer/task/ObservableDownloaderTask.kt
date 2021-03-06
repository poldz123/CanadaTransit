package com.rodolfonavalon.canadatransit.controller.manager.transfer.task

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.transfer.Downloadable
import com.rodolfonavalon.canadatransit.controller.manager.transfer.TransferManager
import com.rodolfonavalon.canadatransit.controller.manager.transfer.Transferable
import com.rodolfonavalon.canadatransit.controller.manager.transfer.util.AbstractTransferTask
import com.rodolfonavalon.canadatransit.controller.manager.transfer.util.TransferForwardingProperty
import com.rodolfonavalon.canadatransit.controller.manager.transfer.util.TransferForwardingSource
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import com.rodolfonavalon.canadatransit.controller.util.FileUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.IOException
import okhttp3.ResponseBody
import okio.Okio
import retrofit2.Response
import timber.log.Timber

/**
 * ObservableDownloaderTask
 *
 * This should return an object that derived from [Transferable] which will be used
 * to download the file from the web.
 *
 * //TODO: Do not download when it already exist in the download directory
 */
class ObservableDownloaderTask(private val transferManager: TransferManager, private val entity: Downloadable) :
        AbstractTransferTask<Downloadable>(transferManager, entity) {
    var downloadedFile: File? = null

    override fun onStart(trackingId: String) {
        super.onStart(trackingId)
        Timber.v("Download entity has STARTED")
        this.disposable = entity.transferObservable()
                .observeOn(Schedulers.io())
                .flatMap(::observableOnDownload)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(::observableOnNext)
                .doOnComplete(::observableOnComplete)
                .doOnError(::onError)
                .subscribe()
    }

    private fun observableOnDownload(responseBody: Response<ResponseBody>): Observable<TransferForwardingProperty> {
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
                    emitter.onNext(TransferForwardingProperty(filePath = feedFile.path))
                    emitter.onComplete()
                } catch (ex: IOException) {
                    emitter.onError(ex)
                }
            } else {
                onError(UnknownError())
            }
        }
    }

    private fun observableOnNext(property: TransferForwardingProperty) {
        if (!property.transferred) {
            onProgress(property)
        } else {
            downloadedFile = property.transferredFile
        }
    }

    private fun observableOnComplete() {
        Timber.v("Entity download has been SUCCESSFUL")
        DebugUtil.assertMainThread()
        DebugUtil.assertTrue(downloadedFile != null, "Entity file is null")
        transferManager.success()
    }
}
