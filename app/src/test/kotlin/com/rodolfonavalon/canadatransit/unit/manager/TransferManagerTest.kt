package com.rodolfonavalon.canadatransit.unit.manager

import com.rodolfonavalon.canadatransit.JvmCanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.transfer.TransferManager
import com.rodolfonavalon.canadatransit.util.BaseMockServerTest
import com.rodolfonavalon.canadatransit.util.generator.TestResourceModel
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE,
        application = JvmCanadaTransitApplication::class)
class TransferManagerTest : BaseMockServerTest() {

    val downloadServerPath = "$serverPath/download/download.zip"

    @Test
    fun testDownloadFeedVersion_success() {
        server.addResponsePath(downloadServerPath, "/transitland/operators-page1")
        val downloadPath = server.url(downloadServerPath).toString()
        val feedVersion = TestResourceModel.FeedVersionModel.createRandomModel(downloadUrl = downloadPath)
        feedVersion.download()
        TransferManager.download()
    }
}
