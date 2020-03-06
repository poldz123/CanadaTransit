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
import com.rodolfonavalon.canadatransit.util.BaseRobolectricTest
import com.rodolfonavalon.canadatransit.util.generator.TestResourceModel
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.fakes.RoboMenuItem

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [ Build.VERSION_CODES.N ],
        manifest = Config.NONE,
        application = JvmCanadaTransitApplication::class)
class OperatorActivityTest : BaseRobolectricTest() {

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
            val adapter = recyclerView.adapter!!
            assertThat(adapter.itemCount).isEqualTo(numOperators)
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

    @Test
    fun testOperator_itemClick() {
        launchActivity<OperatorActivity> { activity ->
            val recyclerView = activity.binding.recyclerOperator
            val operator = operators.first()
            val item = recyclerView.layoutManager?.findViewByPosition(0)!!
            val container = item.findViewById<View>(R.id.cardview_operator)
            container.callOnClick()
            // TODO Check Checkbox is checked
            // TODO Check the next activity
        }
    }

    @Test
    fun testOperator_search() {
        launchActivity<OperatorActivity> { activity ->
            val operator = operators[1]
            // Trigger a search to the first operator
            val searchMenuItem = RoboMenuItem(R.id.menu_operator_search)
            activity.onOptionsItemSelected(searchMenuItem)
            activity.actionMode.onQueryListener?.invoke(operator.name)
            // Check that there is only one in the recycler view after the search filter
            val recyclerView = activity.binding.recyclerOperator
            val adapter = recyclerView.adapter!!
            assertThat(adapter.itemCount).isEqualTo(1)
            // Close the search
            activity.actionMode.finish()
            // Check that all of the operators are back
            assertThat(adapter.itemCount).isEqualTo(numOperators)
        }
    }
}
