package com.rodolfonavalon.canadatransit.controller.manager.transfer.download.downloader

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.transfer.TransferManager
import com.rodolfonavalon.canadatransit.controller.manager.transfer.download.DownloadForwardingProperty
import com.rodolfonavalon.canadatransit.controller.manager.transfer.download.DownloadForwardingSource
import com.rodolfonavalon.canadatransit.controller.manager.transfer.download.DownloadTransfer
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import com.rodolfonavalon.canadatransit.controller.util.FileUtil
import com.rodolfonavalon.canadatransit.model.database.TransferableEntity
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import okio.Okio
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.io.IOException

/**
 * EntityDownloader
 *
 * This should return an object that derived from [TransferableEntity] which will be used
 * to download the file from the web.
 */
abstract class EntityDownloader(private val transferManager: TransferManager, private val entity: TransferableEntity): DownloadTransfer {
    var disposable: Disposable? = null

    final override fun onStart() {
        Timber.v("Download entity has STARTED")
        DebugUtil.assertMainThread()
        this.disposable = entity.entityObservable()
                .flatMap(this::willDownload)
                .filter(DownloadForwardingProperty::downloaded)
                .doOnNext(this::onProgress)
                .map(DownloadForwardingProperty::file)
                .subscribe(this::didDownload, this::onError)
    }

    final override fun onCancel() {
        DebugUtil.assertMainThread()
        Timber.d("Download entity has been CANCELLED")
        // delete the temp file if exist
        FileUtil.createFile(CanadaTransitApplication.appContext, entity).delete()
        // Dispose the retrofit call
        disposable?.dispose()
    }

    final override fun onError(error: Throwable) {
        Timber.e(error, "Download entity has FAILED")
        onCancel()
        transferManager.failure()
    }

    final override fun onProgress(property: DownloadForwardingProperty) {
        DebugUtil.assertMainThread()
        DebugUtil.assertFalse(property.downloaded, "Entity progress is triggered where it was already downloaded")
        // TODO: Need to implement the feed progress
    }

    final override fun willDownload(responseBody: Response<ResponseBody>): Observable<DownloadForwardingProperty> {
        return Observable.create { emitter ->
            val body = responseBody.body()
            if (body != null) {
                DebugUtil.assertWorkerThread()
                // Forwarded source for reading the buffer input stream from retrofit
                val forwardingSource = DownloadForwardingSource(body.source(), body.contentLength(), emitter)
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
                    emitter.onNext(DownloadForwardingProperty(file = feedFile))
                    emitter.onComplete()
                } catch (ex: IOException) {
                    emitter.onError(ex)
                }
            } else {
                onError(UnknownError())
            }
        }
    }

    final override fun didDownload(file: File?) {
        Timber.v("Entity download has been SUCCESSFUL")
        DebugUtil.assertMainThread()
        DebugUtil.assertTrue(file != null, "Entity file is null")
        transferManager.success()
    }
}