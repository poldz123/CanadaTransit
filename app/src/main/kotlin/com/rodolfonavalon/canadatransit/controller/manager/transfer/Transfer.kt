package com.rodolfonavalon.canadatransit.controller.manager.transfer

/**
 * TODO: Transfer
 */
interface Transfer {

    /**
     * TODO: trackingId
     */
    fun trackingId(): String

    /**
     * TODO: onStart
     */
    fun onStart()

    /**
     * TODO: onCancel
     */
    fun onCancel()

    /**
     * TODO: onError
     */
    fun onError(error: Throwable)
}