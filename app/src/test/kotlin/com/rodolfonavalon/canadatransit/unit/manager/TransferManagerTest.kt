package com.rodolfonavalon.canadatransit.unit.manager

import com.google.common.truth.Truth.assertThat
import com.rodolfonavalon.canadatransit.JvmCanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.transfer.TransferManager
import com.rodolfonavalon.canadatransit.controller.util.FileUtil
import com.rodolfonavalon.canadatransit.model.database.transit.FeedVersion
import com.rodolfonavalon.canadatransit.util.BaseMockServerTest
import com.rodolfonavalon.canadatransit.util.generator.TestResourceModel
import okio.Buffer
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE,
        application = JvmCanadaTransitApplication::class)
class TransferManagerTest : BaseMockServerTest() {

    @Test
    fun testDownloadFeedVersion_success() {
        val downloadServerPath = "$serverPath/download/download.zip"
        // Create the data
        val data = ByteArray(5 * 1024) { 0x11.toByte() }
        // Send the data to be downloaded as file in feed version
        server.addResponseBody(downloadServerPath, Buffer().write(data))
        val downloadPath = server.url(downloadServerPath).toString()
        val feedVersion = TestResourceModel.FeedVersionModel.createRandomModel(downloadUrl = downloadPath)
        feedVersion.download()
        TransferManager.manager().start()
        // Check that the file is downloaded in the internal storage
        val downloadFeedVersionFile = FileUtil.createFile(CanadaTransitApplication.appContext, feedVersion)
        assertThat(downloadFeedVersionFile.exists()).isTrue()
        assertThat(downloadFeedVersionFile.length()).isEqualTo(data.size)
    }

    @Test
    fun testDownloadFeedVersion_failure() {
        val downloadServerPath = "$serverPath/download/download.zip"
        // Create the data
        val data = ByteArray(5 * 1024) { 0x11.toByte() }
        // Send the data to be downloaded as file in feed version
        server.addResponseBody(downloadServerPath, Buffer().write(data), responseCode = 500)
        val downloadPath = server.url(downloadServerPath).toString()
        val feedVersion = TestResourceModel.FeedVersionModel.createRandomModel(downloadUrl = downloadPath)
        feedVersion.download()
        TransferManager.manager().start()
        // Check that the file is downloaded in the internal storage
        val downloadFeedVersionFile = FileUtil.createFile(CanadaTransitApplication.appContext, feedVersion)
        assertThat(downloadFeedVersionFile.exists()).isFalse()
    }

    @Test
    fun testeDownloadFeedVersions_multipleSuccess() {
        val testData = mutableListOf<FeedVersion>()
        (0..10).forEach { index ->
            val downloadServerPath = "$serverPath/download/download$index.zip"
            // Create the data
            val data = ByteArray(5 * 1024 + index) { 0x11.toByte() }
            // Send the data to be downloaded as file in feed version
            server.addResponseBody(downloadServerPath, Buffer().write(data))
            val downloadPath = server.url(downloadServerPath).toString()
            val feedVersion = TestResourceModel.FeedVersionModel.createRandomModel(id = "$index", downloadUrl = downloadPath)
            feedVersion.download()
            testData.add(feedVersion)
        }
        TransferManager.manager().start()
        // Check that the files are downloaded in the internal storage
        for (i in 0 until testData.count()) {
            val feedVersion = testData[i]
            val dataSize = 5 * 1024 + i
            val downloadFeedVersionFile = FileUtil.createFile(CanadaTransitApplication.appContext, feedVersion)
            assertThat(downloadFeedVersionFile.exists()).isTrue()
            assertThat(downloadFeedVersionFile.length()).isEqualTo(dataSize)
        }
    }

    @Test
    fun testeDownloadFeedVersions_multipleFailure() {
        val testData = mutableListOf<FeedVersion>()
        (0..10).forEach { index ->
            val downloadServerPath = "$serverPath/download/download$index.zip"
            // Create the data
            val data = ByteArray(5 * 1024 + index) { 0x11.toByte() }
            // Send the data to be downloaded as file in feed version
            server.addResponseBody(downloadServerPath, Buffer().write(data), responseCode = 500)
            val downloadPath = server.url(downloadServerPath).toString()
            val feedVersion = TestResourceModel.FeedVersionModel.createRandomModel(id = "$index", downloadUrl = downloadPath)
            feedVersion.download()
            testData.add(feedVersion)
        }
        TransferManager.manager().start()
        // Check that the files are downloaded in the internal storage
        for (i in 0 until testData.count()) {
            val feedVersion = testData[i]
            val downloadFeedVersionFile = FileUtil.createFile(CanadaTransitApplication.appContext, feedVersion)
            assertThat(downloadFeedVersionFile.exists()).isFalse()
        }
    }
}
