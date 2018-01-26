package com.rodolfonavalon.canadatransit.controller.manager.download.transfer

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.download.DownloadManager
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import com.rodolfonavalon.canadatransit.controller.util.FileUtil
import com.rodolfonavalon.canadatransit.model.database.OperatorFeedVersion
import com.rodolfonavalon.canadatransit.model.database.dao.AppDatabase
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import okio.Okio
import retrofit2.Response
import java.io.File
import java.io.IOException

/**
 * TransitFeedDownloader
 */
class TransitLandFeedDownloader(val downloadManager: DownloadManager, val feedOneStopId: String): Downloader {
    var disposable: Disposable? = null
    var feedVersion: OperatorFeedVersion? = null

    override fun onStart() {
        val transitDao = CanadaTransitApplication.appDatabase.transitLandDao()
        AppDatabase.query(transitDao.findOperatorFeedVersion(feedOneStopId),
                this::onRetrieveFeedVersion,
                this::onError)
    }

    override fun onCancel() {
        DebugUtil.assertMainThread()
        // delete the temp file if exist
        if (feedVersion != null)
            FileUtil.createFile(CanadaTransitApplication.appContext, feedVersion!!).delete()
        // Dispose the retrofit call
        disposable?.dispose()
    }

    override fun onError(error: Throwable) {
        DebugUtil.assertMainThread()
        // delete the temp file if exist
        if (feedVersion != null)
            FileUtil.createFile(CanadaTransitApplication.appContext, feedVersion!!).delete()
        // Dispose the retrofit call
        disposable?.dispose()
        downloadManager.failure()
    }

    private fun onRetrieveFeedVersion(feedVersion: OperatorFeedVersion) {
        DebugUtil.assertMainThread()
        this.feedVersion = feedVersion
        this.disposable = TransitLandApi.downloadOperatorFeed(feedVersion)
                .flatMap(this::onFeedDownload)
                .filter(DownloadForwardingProperty::downloaded)
                .doOnNext(this::onFeedProgress)
                .map(DownloadForwardingProperty::file)
                .subscribe(this::onFeedDownloaded, this::onError)
    }

    private fun onFeedDownload(responseBody: Response<ResponseBody>): Observable<DownloadForwardingProperty> {
        return Observable.create { emitter ->
            val body = responseBody.body()
            if (body != null) {
                DebugUtil.assertWorkerThread()
                // Forwarded source for reading the buffer input stream from retrofit
                val forwardingSource = DownloadForwardingSource(body.source(), body.contentLength(), emitter)
                // Close the emitter once the buffer is completely consumed
                emitter.setCancellable(body::close)
                try {
                    // Create file to save the data
                    DebugUtil.assertTrue(feedVersion != null, "OperatorFeedVersion is null for id: $feedOneStopId")
                    val tempFile = FileUtil.createFile(CanadaTransitApplication.appContext, feedVersion!!, true)
                    // Read the buffer to the Forwarded Source
                    val sink = Okio.buffer(Okio.sink(tempFile))
                    sink.writeAll(forwardingSource)
                    sink.close()
                    // Copy and delete the temp file with the feed file
                    val feedFile = FileUtil.createFile(CanadaTransitApplication.appContext, feedVersion!!)
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

    private fun onFeedProgress(property: DownloadForwardingProperty) {
        DebugUtil.assertMainThread()
        DebugUtil.assertFalse(property.downloaded, "Feed progress is triggered where it was already downloaded for id: $feedOneStopId")
        // TODO: Need to implement the feed progress
    }

    private fun onFeedDownloaded(file: File?) {
        DebugUtil.assertMainThread()
        DebugUtil.assertTrue(file != null, "OperatorFeedVersion feed file is null for id: $feedOneStopId")
        downloadManager.success()
    }
}