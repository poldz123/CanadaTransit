package com.rodolfonavalon.canadatransit.controller.manager.transfer

import com.rodolfonavalon.canadatransit.controller.manager.transfer.util.TransferForwardingProperty
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File

/**
 * TODO: Transfer
 */
interface Transfer {
    /**
     * TODO: onStart
     */
    fun onStart()

    /**
     * TODO: onProgress
     */
    fun onProgress(property: TransferForwardingProperty)

    /**
     * TODO: onCancel
     */
    fun onCancel()

    /**
     * TODO: onError
     */
    fun onError(error: Throwable)

    /**
     * TODO: DownloadTransfer
     */
    interface DownloadTransfer: Transfer {

        /**
         * TODO: willDownload
         */
        fun willDownload(responseBody: Response<ResponseBody>): Observable<TransferForwardingProperty>

        /**
         * TODO: didDownload
         */
        fun didDownload(file: File?)
    }
}

/**
 * TODO: Transferable
 */
interface Transferable {

    /**
     * Retrieves the download observable where it can be transferred. The Rx-Observable must
     * configure its background task to be executed within the io and the subscription must
     * be executed within the android's main thread.
     *
     * @return The observable response api where the entity must be transferred
     */
    fun transferObservable(): Observable<Response<ResponseBody>>

    /**
     * Retrieves the tracking id of which can be used as the file name of the transferred
     * file. This serves as the tracking id for the [TransferManager] which tracks
     * the transfer within the manager.
     *
     * @return The id of the entity
     */
    fun transferTrackingId(): String

    /**
     * Retrieves the directory path where the file will be transferred. This must
     * only contains string with no slash at the beginning and slash at the end:
     * e.g "sample1/sample2/"
     *
     * @return The directory path where the entiry will be transferred
     */
    fun transferDirectoryPath(): String

    /**
     * TODO: Downloadable
     */
    interface Downloadable: Transferable {
        /**
         * TODO: download
         */
        fun download()
    }
}