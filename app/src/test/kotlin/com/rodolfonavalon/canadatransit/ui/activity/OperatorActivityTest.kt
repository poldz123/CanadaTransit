package com.rodolfonavalon.canadatransit.ui.activity

import android.os.Build
import android.view.View
import android.widget.TextView
import com.google.common.truth.Truth.assertThat
import com.rodolfonavalon.canadatransit.JvmCanadaTransitApplication
import com.rodolfonavalon.canadatransit.R
import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.activity.OperatorActivity
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.ui.BaseUITest
import com.rodolfonavalon.canadatransit.unit.util.TestResourceModel
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [ Build.VERSION_CODES.N ],
        manifest = Config.NONE,
        application = JvmCanadaTransitApplication::class)

class OperatorActivityTest : BaseUITest() {

    private val numOperators = 10
    private lateinit var operators: MutableList<Operator>

    override fun setup() {
        super.setup()
        operators = mutableListOf()
        for (i in 1..numOperators) {
            val testOperator = TestResourceModel.OperatorModel.createRandomModel(i)
            operators.add(testOperator)
        }
        CanadaTransitApplication.appDatabase.operatorDao().insert(operators).blockingGet()
    }

    @Test
    fun testOperator_list() {
        launchActivity<OperatorActivity> { activity ->
            val recyclerView = activity.binding.recyclerOperator
            assertThat(recyclerView.visibility).isEqualTo(View.VISIBLE)
            val adapter = recyclerView.adapter!!
            assertThat(adapter.itemCount).isEqualTo(numOperators)
            assertThat(adapter.hasStableIds()).isFalse()
        }
    }

    @Test
    fun testOperator_item() {
        launchActivity<OperatorActivity> { activity ->
            val recyclerView = activity.binding.recyclerOperator
            val operator = operators.first()
            // Check name and website
            val item = recyclerView.layoutManager?.findViewByPosition(0)!!
            val nameTextView = item.findViewById<TextView>(R.id.textview_operator_title)
            val websiteTextView = item.findViewById<TextView>(R.id.textview_operator_website)
            assertThat(nameTextView.text).isEqualTo(operator.name)
            assertThat(websiteTextView.text).isEqualTo(operator.website)
        }
    }
}
