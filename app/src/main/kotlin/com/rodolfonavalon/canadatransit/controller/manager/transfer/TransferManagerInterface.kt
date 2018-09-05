package com.rodolfonavalon.canadatransit.controller.manager.transfer

import com.rodolfonavalon.canadatransit.controller.manager.transfer.util.TransferForwardingProperty
import com.rodolfonavalon.canadatransit.controller.util.queue.Action
import com.rodolfonavalon.canadatransit.controller.util.queue.Task
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File

/**
 * TODO: Transfer
 */
interface TransferTask: Task {
    /**
     * TODO: onProgress
     */
    fun onProgress(property: TransferForwardingProperty)
    /**
     *
     * TODO: DownloadTransferTask
     */
    interface DownloadTransferTask: TransferTask {

        /**
         * TODO: onDownload
         */
        fun onDownload(file: File?)
    }
}

/**
 * TODO: Transferable
 */
interface Transferable: Action {

    /**
     * Retrieves the download observable where it can be transferred. The Rx-Observable must
     * configure its background task to be executed within the io and the subscription must
     * be executed within the android's main thread.
     *
     * @return The observable response api where the entity must be transferred
     */
    fun transferObservable(): Observable<Response<ResponseBody>>

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

