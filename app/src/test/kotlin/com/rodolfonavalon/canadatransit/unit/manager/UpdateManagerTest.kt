package com.rodolfonavalon.canadatransit.unit.manager

import com.google.common.truth.Truth.assertThat
import com.rodolfonavalon.canadatransit.JvmCanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.database.dao.transit.FeedDao
import com.rodolfonavalon.canadatransit.controller.database.dao.transit.FeedVersionDao
import com.rodolfonavalon.canadatransit.controller.database.dao.transit.OperatorDao
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.util.BaseMockServerTest
import kotlin.test.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE,
        application = JvmCanadaTransitApplication::class)
class UpdateManagerTest : BaseMockServerTest() {

    private lateinit var operatorDao: OperatorDao
    private lateinit var feedDao: FeedDao
    private lateinit var feedVersionDao: FeedVersionDao

    override fun setup() {
        super.setup()
        TransitLandApi.initializeRetrofit(server.url("/api/v1/").toString())
        operatorDao = CanadaTransitApplication.appDatabase.operatorDao()
        feedDao = CanadaTransitApplication.appDatabase.feedDao()
        feedVersionDao = CanadaTransitApplication.appDatabase.feedVersionDao()
    }

    @Test
    fun testUpdateOperators_dataConsistency() {
        server.addResponsePath("/api/v1/operators", "/transitland/operators-page1")
        server.addResponsePath("/api/v1/operators", "/transitland/operators-page2")

        var isSuccessCalled = false
        UpdateManager.updateOperators().subscribe({
            isSuccessCalled = true
        }, {
            fail("Failed to update operators: $it")
        })

        UpdateManager.manager().start()
        assertThat(isSuccessCalled).isTrue()

        val operators = operatorDao.load().blockingGet()
        assertThat(operators).isNotEmpty()
    }

    @Test
    fun testUpdateFeeds_dataConsistency() {
        server.addResponsePath("/api/v1/operators", "/transitland/operators-page1")
        server.addResponsePath("/api/v1/operators", "/transitland/operators-page2")
        server.addResponsePath("/api/v1/feeds", "/transitland/feeds-page1")
        server.addResponsePath("/api/v1/feeds", "/transitland/feeds-page2")

        var isSuccessCalled = false
        UpdateManager.updateOperators().flatMap {
            UpdateManager.updateFeeds()
        }.subscribe({
            isSuccessCalled = true
        }, {
            fail("Failed to update feed version: $it")
        })

        UpdateManager.manager().start()
        assertThat(isSuccessCalled).isTrue()

        val operators = operatorDao.load().blockingGet()
        assertThat(operators).isNotEmpty()
        val feeds = feedDao.load().blockingGet()
        assertThat(feeds).isNotEmpty()
    }

    @Test
    fun testUpdateFeedVersions_dataConsistency() {
        server.addResponsePath("/api/v1/operators", "/transitland/operators-page1")
        server.addResponsePath("/api/v1/operators", "/transitland/operators-page2")
        server.addResponsePath("/api/v1/feeds", "/transitland/feeds-page1")
        server.addResponsePath("/api/v1/feeds", "/transitland/feeds-page2")
        server.addResponsePath("/api/v1/feed_versions", "/transitland/feed-versions-page1")
        server.addResponsePath("/api/v1/feed_versions", "/transitland/feed-versions-page2")

        var isSuccessCalled = false
        UpdateManager.updateOperators()
                .flatMap { UpdateManager.updateFeeds() }
                .flatMap { UpdateManager.updateFeedVersions() }.subscribe({
            isSuccessCalled = true
        }, {
            fail("Failed to update feed version: $it")
        })

        UpdateManager.manager().start()
        assertThat(isSuccessCalled).isTrue()

        val operators = operatorDao.load().blockingGet()
        assertThat(operators).isNotEmpty()
        val feeds = feedDao.load().blockingGet()
        assertThat(feeds).isNotEmpty()
        val feedVersions = feedVersionDao.load().blockingGet()
        assertThat(feedVersions).isNotEmpty()
    }
}
