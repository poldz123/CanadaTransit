package com.rodolfonavalon.canadatransit.controller.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rodolfonavalon.canadatransit.R
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.util.extension.toast
import com.rodolfonavalon.canadatransit.databinding.ActivityMainBinding
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.view.CustomSearchActionMode
import com.rodolfonavalon.canadatransit.view.adapter.recycler.OperatorAdapter
import com.rodolfonavalon.canadatransit.view.adapter.recycler.decorator.MarginItemDecorator
import com.rodolfonavalon.canadatransit.viewmodel.MainViewModel
import io.reactivex.rxkotlin.subscribeBy
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    private lateinit var binding: ActivityMainBinding

    private lateinit var recyclerAdapter: OperatorAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private lateinit var actionMode: CustomSearchActionMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
        update()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_operator, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_operator_search -> {
                actionMode.start(binding.root) {
                    actionMode.onQueryListener = { queryText ->
                        mainViewModel.operators.value?.also { operators ->
                            val filteredOperators = operators.filter { operator ->
                                val name = operator.name.toLowerCase(Locale.getDefault())
                                val shortName = operator.shortName?.toLowerCase(Locale.getDefault()) ?: ""
                                name.contains(queryText.trim().toLowerCase(Locale.getDefault())) || shortName.contains(queryText.trim().toLowerCase(Locale.getDefault()))
                            }
                            recyclerAdapter.addAll(filteredOperators)
                        }
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setup() {
        title = "Select Transits"
        // Setup recycler view list
        layoutManager = LinearLayoutManager(this)
        recyclerAdapter = OperatorAdapter(mainViewModel)
        binding.recyclerOperator.layoutManager = layoutManager
        binding.recyclerOperator.adapter = recyclerAdapter
        binding.recyclerOperator.addItemDecoration(MarginItemDecorator(resources.getDimensionPixelSize(R.dimen.spacing_small_medium)))
        mainViewModel.operators.observe(this, Observer<List<Operator>> { operators ->
            onOperatorsChanged(operators)
        })
        // Setup action mode
        actionMode = CustomSearchActionMode()
        actionMode.onDestroyedListener = {
            mainViewModel.operators.value?.also { operators ->
                recyclerAdapter.addAll(operators)
            }
        }
        // Setup done selecting operators
        binding.fabOperatorDone.hide()
        binding.fabOperatorDone.setOnClickListener {
        }
        // Setup viewmodel listeners
        mainViewModel.getListenerNumSelectedOperators().observe(this, Observer { numSelected ->
            onNumSelectedOperatorsChanged(numSelected)
        })
    }

    private fun onOperatorsChanged(operators: List<Operator>) {
        if (actionMode.isShowing) {
            actionMode.update()
            return
        }
        recyclerAdapter.addAll(operators)
    }

    private fun onNumSelectedOperatorsChanged(numSelected: Int) {
        if (numSelected == 0) {
            binding.fabOperatorDone.hide()
        } else {
            binding.fabOperatorDone.show()
        }
    }

    private fun update() {
        // TODO Lets update the manager when application is started
        UpdateManager.updateOperators().subscribeBy(onSuccess = { operators ->
            Timber.d("Number of operators: ${operators.size}")
            toast("Successfully updated operators")
        }, onError = {
            Timber.e(it, "Error fetching operators")
            toast("Failed to update operators")
        })
    }
}
