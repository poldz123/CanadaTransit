package com.rodolfonavalon.canadatransit.controller.manager.download.transfer

interface Downloader {
    fun onStart()
    fun onCancel()
    fun onError(error: Throwable)
}