package com.rodolfonavalon.canadatransit.controller.manager.download.transfer

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.download.DownloadManager
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import com.rodolfonavalon.canadatransit.controller.util.FileUtil
import com.rodolfonavalon.canadatransit.model.database.DownloadableEntity
import com.rodolfonavalon.canadatransit.model.database.dao.AppDatabase
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import okio.Buffer
import okio.ForwardingSource
import okio.Okio
import okio.Source
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.io.IOException

/**
 * EntityDownloader
 */
abstract class EntityDownloader<Entity: DownloadableEntity>(private val downloadManager: DownloadManager): Downloader {
    var disposable: Disposable? = null
    var entity: DownloadableEntity? = null

    /**
     * Retrieves the observable query of the entity. This must return
     * the DAO query MAYBE observable.
     *
     * @return The query observable of the downloadable entity
     */
    abstract fun entityQuery(): Maybe<Entity>

    final override fun onStart() {
        Timber.v("Download entity has STARTED")
        AppDatabase.query(entityQuery(),
                this::retrieveFeedVersion,
                this::onError)
    }

    final override fun onCancel() {
        DebugUtil.assertMainThread()
        Timber.d("Download entity has been CANCELLED")
        // delete the temp file if exist
        if (entity != null)
            FileUtil.createFile(CanadaTransitApplication.appContext, entity!!).delete()
        // Dispose the retrofit call
        disposable?.dispose()
    }

    final override fun onError(error: Throwable) {
        Timber.e(error, "Download entity has FAILED")
        onCancel()
        downloadManager.failure()
    }

    private fun retrieveFeedVersion(entity: DownloadableEntity) {
        DebugUtil.assertMainThread()
        this.entity = entity
        this.disposable = entity.entityObservable()
                .flatMap(this::feedDownload)
                .filter(DownloadForwardingProperty::downloaded)
                .doOnNext(this::feedProgress)
                .map(DownloadForwardingProperty::file)
                .subscribe(this::feedDownloaded, this::onError)
    }

    private fun feedDownload(responseBody: Response<ResponseBody>): Observable<DownloadForwardingProperty> {
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
                    val tempFile = FileUtil.createFile(CanadaTransitApplication.appContext, entity!!, true)
                    // Read the buffer to the Forwarded Source
                    val sink = Okio.buffer(Okio.sink(tempFile))
                    sink.writeAll(forwardingSource)
                    sink.close()
                    // Copy and delete the temp file with the feed file
                    val feedFile = FileUtil.createFile(CanadaTransitApplication.appContext, entity!!)
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

    private fun feedProgress(property: DownloadForwardingProperty) {
        DebugUtil.assertMainThread()
        DebugUtil.assertFalse(property.downloaded, "Entity progress is triggered where it was already downloaded")
        // TODO: Need to implement the feed progress
    }

    private fun feedDownloaded(file: File?) {
        Timber.v("Entity download has been SUCCESSFUL")
        DebugUtil.assertMainThread()
        DebugUtil.assertTrue(file != null, "Entity file is null")
        downloadManager.success()
    }
}

/**
 * DownloadForwardingProperty
 */
data class DownloadForwardingProperty(val progress: Float? = null,
                                      val file: File? = null,
                                      val downloaded: Boolean = (file != null))

/**
 * DownloadForwardingSource
 */
class DownloadForwardingSource(source: Source,
                               private val totalBytesToRead: Long,
                               private val emitter: ObservableEmitter<DownloadForwardingProperty>): ForwardingSource(source) {
    var currentBytesRead: Long = 0L

    override fun read(sink: Buffer, byteCount: Long): Long {
        // Read the next chunk of data
        val bytesRead = super.read(sink, byteCount)
        // Must check that the bytes being read is not yet complete
        if (isReadExhausted(bytesRead)) {
            currentBytesRead += 0L
            emitter.onNext(DownloadForwardingProperty(progress = 1.0F))
        } else {
            currentBytesRead += bytesRead
            emitter.onNext(DownloadForwardingProperty(progress = bytesRead.toFloat() / totalBytesToRead.toFloat()))
        }
        return bytesRead
    }

    private fun isReadExhausted(bytesRead: Long): Boolean {
        return bytesRead == -1L
    }
}