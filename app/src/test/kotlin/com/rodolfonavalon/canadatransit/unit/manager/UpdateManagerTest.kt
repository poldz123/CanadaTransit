package com.rodolfonavalon.canadatransit.unit.manager

import com.rodolfonavalon.canadatransit.util.BaseMockServerTest
import com.rodolfonavalon.canadatransit.JvmCanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.database.dao.transit.OperatorDao
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.controller.util.extension.dbQuery
import kotlin.test.assertTrue
import kotlin.test.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import timber.log.Timber

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE,
        application = JvmCanadaTransitApplication::class)
class UpdateManagerTest : BaseMockServerTest() {

    private lateinit var operatorDao: OperatorDao

    override fun setup() {
        super.setup()
        TransitLandApi.initializeRetrofit(server.url("/api/v1/").toString())
        operatorDao = CanadaTransitApplication.appDatabase.operatorDao()
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
        assertTrue(isSuccessCalled)

        operatorDao.dbQuery {
            operatorDao.load()
        }.subscribe({ operators ->
            Timber.d("sdfdfs")
        }, {
            fail("Failed to query operators: $it")
        })
    }
}
