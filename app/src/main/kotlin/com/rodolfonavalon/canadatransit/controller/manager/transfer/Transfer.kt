package com.rodolfonavalon.canadatransit.controller.manager.transfer

interface Transfer {
    fun onStart()
    fun onCancel()
    fun onError(error: Throwable)
}