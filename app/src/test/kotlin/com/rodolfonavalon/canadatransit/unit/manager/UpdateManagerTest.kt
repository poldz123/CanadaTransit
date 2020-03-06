package com.rodolfonavalon.canadatransit.unit.manager

import com.google.common.truth.Truth.assertThat
import com.rodolfonavalon.canadatransit.JvmCanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.database.dao.transit.FeedDao
import com.rodolfonavalon.canadatransit.controller.database.dao.transit.FeedVersionDao
import com.rodolfonavalon.canadatransit.controller.database.dao.transit.OperatorDao
import com.rodolfonavalon.canadatransit.controller.database.dao.user.UserTransitDao
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.model.database.user.UserTransit
import com.rodolfonavalon.canadatransit.util.BaseMockServerTest
import kotlin.test.fail
import org.joda.time.DateTime
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE,
        application = JvmCanadaTransitApplication::class)
class UpdateManagerTest : BaseMockServerTest() {

    private lateinit var userTransitDao: UserTransitDao

    private lateinit var operatorDao: OperatorDao
    private lateinit var feedDao: FeedDao
    private lateinit var feedVersionDao: FeedVersionDao

    override fun setup() {
        super.setup()
        TransitLandApi.initializeRetrofit(server.url("/api/v1/").toString())
        userTransitDao = CanadaTransitApplication.appDatabase.userTransitDao()
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
        server.addResponsePath("/api/v1/feeds", "/transitland/feed-(f-f24-octranspo)")

        // Select OC Transpo id
        val selectedOperatorId = "o-f24-octranspo"

        var isSuccessCalled = false
        UpdateManager.updateOperators().flatMap { _ ->
            // Select a transit for the the user to save the feeds
            userTransitDao.insert(UserTransit(selectedOperatorId, DateTime.now())).blockingGet()
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
        assertThat(feeds.count()).isEqualTo(1)
    }

    @Test
    fun testUpdateFeedVersions_dataConsistency() {
        server.addResponsePath("/api/v1/operators", "/transitland/operators-page1")
        server.addResponsePath("/api/v1/operators", "/transitland/operators-page2")
        server.addResponsePath("/api/v1/feeds", "/transitland/feed-(f-f24-octranspo)")
        server.addResponsePath("/api/v1/feed_versions", "/transitland/feed-version-(f-f24-octranspo)")

        // Select OC Transpo id
        val selectedOperatorId = "o-f24-octranspo"

        var isSuccessCalled = false
        UpdateManager.updateOperators()
                .flatMap { _ ->
                    // Select a transit for the the user to save the feeds
                    userTransitDao.insert(UserTransit(selectedOperatorId, DateTime.now())).blockingGet()
                    UpdateManager.updateFeeds()
                }
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
        assertThat(feeds.count()).isEqualTo(1)
        val feedVersions = feedVersionDao.load().blockingGet()
        assertThat(feedVersions.count()).isEqualTo(1)
    }
}
