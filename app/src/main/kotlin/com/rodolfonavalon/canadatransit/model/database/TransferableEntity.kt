package com.rodolfonavalon.canadatransit.model.database

import android.arch.persistence.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import com.rodolfonavalon.canadatransit.controller.manager.transfer.TransferManager
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response

abstract class TransferableEntity {

    /**
     * Tracking key when an entity is downloaded from the api server. This
     * serves as the unique id within the [TransferManager] which can be used
     * to cancel or track the progress of the entity.
     */
    @ColumnInfo(name = "tracking_key")
    @SerializedName("tracking_key") var trackingKey: String = TransferManager.generateTrackingKey()

    /**
     * Retrieves the download observable of the entity where it can be downloaded.
     * The Rx-Observable must configure its background task to be executed within the
     * io and the subscription must be executed within the android's main thread.
     *
     * @return The observable response api where the entity must be downloaded
     */
    abstract fun entityObservable(): Observable<Response<ResponseBody>>

    /**
     * Retrieves the id of the entity which can be used as the file name of the downloaded
     * file. Must use the id of the entity based on its primary key to distinguished it
     * properly from other entity.
     *
     * @return The id of the entity
     */
    abstract fun entityId(): String

    /**
     * Retrieves the directory path where the entity will be downloaded. This must
     * only contains string with no slash at the beginning and slash at the end:
     * e.g "sample1/sample2/"
     *
     * @return The directory path where the entiry will be downloaded
     */
    abstract fun entityDirectoryPath(): String
}