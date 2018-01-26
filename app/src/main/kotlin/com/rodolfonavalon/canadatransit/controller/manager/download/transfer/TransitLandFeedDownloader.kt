package com.rodolfonavalon.canadatransit.controller.manager.download.transfer

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.download.DownloadManager
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
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
        // TODO: Implement on cancel
    }

    override fun onError(error: Throwable) {
        // TODO: delete the temp file
        // Dispose the retrofit call
        disposable?.dispose()
        downloadManager.failure()
    }

    private fun onRetrieveFeedVersion(feedVersion: OperatorFeedVersion) {
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
                // Forwarded source for reading the buffer input stream from retrofit
                val forwardingSource = DownloadForwardingSource(body.source(), body.contentLength(), emitter)
                // Close the emitter once the buffer is completely consumed
                emitter.setCancellable(body::close)
                try {
                    // Create file to save the data
                    // TODO: create path from file manager
                    // TODO: create file as temp
                    // TODO: replace old file with temp file
                    val file = File("")
                    // Read the buffer to the Forwarded Source
                    val sink = Okio.buffer(Okio.sink(file))
                    sink.writeAll(forwardingSource)
                    sink.close()
                    // Complete the emitter with the file
                    emitter.onNext(DownloadForwardingProperty(file = file))
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
        // TODO: assert here
    }

    private fun onFeedDownloaded(file: File?) {
        // TODO: assert here
        downloadManager.success()
    }
}