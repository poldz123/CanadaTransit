package com.rodolfonavalon.canadatransit.controller.manager

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.model.database.OperatorFeedVersion
import com.rodolfonavalon.canadatransit.model.database.dao.AppDatabase
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import okio.Buffer
import okio.ForwardingSource
import okio.Okio
import okio.Source
import retrofit2.Response
import java.io.File
import java.io.IOException

/**
 * DownloadImpl
 */
private interface DownloadImpl {
    fun onStart()
    fun onCancel()
    fun onError(error: Throwable)
}

/**
 * DownloadManager
 */
class DownloadManager private constructor() {

    fun start() {

    }

    fun stop() {

    }

    fun next() {
    }

    fun success() {

    }

    fun failure() {

    }

    companion object {
        private val instance: DownloadManager = DownloadManager()
        // TODO: download tracking key
    }
}

/**
 * DownloadForwardingProperty
 */
private data class DownloadForwardingProperty(val progress: Float? = null,
                                              val file: File? = null,
                                              val downloaded: Boolean = (file != null))

/**
 * DownloadForwardingSource
 */
private class DownloadForwardingSource(source: Source,
                                       val totalBytesToRead: Long,
                                       val emitter: ObservableEmitter<DownloadForwardingProperty>): ForwardingSource(source) {
    var currentBytesRead: Long = 0L

    override fun read(sink: Buffer, byteCount: Long): Long {
        // Read the next chunk of data
        val bytesRead = super.read(sink, byteCount)
        // Must check that the bytes being read is not yet complete
        if (isReadExhausted(bytesRead)) {
            currentBytesRead.plus(0L)
            emitter.onNext(DownloadForwardingProperty(progress = 1.0F))
        } else {
            currentBytesRead.plus(bytesRead)
            emitter.onNext(DownloadForwardingProperty(progress = bytesRead.toFloat() / totalBytesToRead.toFloat()))
        }
        return bytesRead
    }

    private fun isReadExhausted(bytesRead: Long): Boolean {
        return bytesRead == -1L
    }
}

/**
 * TransitFeedDownloader
 */
private class TransitFeedDownloader(val downloadManager: DownloadManager, val feedOneStopId: String): DownloadImpl {
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