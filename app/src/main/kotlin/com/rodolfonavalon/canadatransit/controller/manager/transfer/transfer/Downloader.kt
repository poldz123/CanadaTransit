package com.rodolfonavalon.canadatransit.controller.manager.transfer.transfer

interface Downloader {
    fun onStart()
    fun onCancel()
    fun onError(error: Throwable)
}