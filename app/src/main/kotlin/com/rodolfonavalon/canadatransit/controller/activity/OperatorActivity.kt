package com.rodolfonavalon.canadatransit.controller.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rodolfonavalon.canadatransit.R
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.util.extension.toast
import com.rodolfonavalon.canadatransit.databinding.ActivityOperatorBinding
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.view.CustomSearchActionMode
import com.rodolfonavalon.canadatransit.view.adapter.recycler.OperatorAdapter
import com.rodolfonavalon.canadatransit.view.adapter.recycler.decorator.MarginItemDecorator
import com.rodolfonavalon.canadatransit.viewmodel.OperatorViewModel
import io.reactivex.rxkotlin.subscribeBy
import java.util.*
import timber.log.Timber

class OperatorActivity : AppCompatActivity() {

    private val operatorViewModel by viewModels<OperatorViewModel>()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var binding: ActivityOperatorBinding

    private lateinit var recyclerAdapter: OperatorAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var actionMode: CustomSearchActionMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOperatorBinding.inflate(layoutInflater)
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
                        operatorViewModel.operators.value?.also { operators ->
                            val filteredOperators = operators.filter { operator ->
                                val name = operator.name.toLowerCase(Locale.getDefault())
                                val website = operator.website?.toLowerCase(Locale.getDefault()) ?: ""
                                val query = queryText.trim().toLowerCase(Locale.getDefault())
                                val shortName = operator.shortName?.toLowerCase(Locale.getDefault()) ?: ""
                                name.contains(query) || shortName.contains(query) || website.contains(query.replace("/+s".toRegex(), ""))
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
        setSupportActionBar(binding.containerToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_close_white_24dp)
        title = "Transits"
        // Setup recycler view list
        layoutManager = LinearLayoutManager(this)
        recyclerAdapter = OperatorAdapter(operatorViewModel)
        binding.recyclerOperator.layoutManager = layoutManager
        binding.recyclerOperator.adapter = recyclerAdapter
        binding.recyclerOperator.addItemDecoration(MarginItemDecorator(resources.getDimensionPixelSize(R.dimen.spacing_small_medium)))
        operatorViewModel.operators.observe(this, Observer<List<Operator>> { operators ->
            onOperatorsChanged(operators)
        })
        // Setup action mode
        actionMode = CustomSearchActionMode()
        actionMode.onDestroyedListener = {
            operatorViewModel.operators.value?.also { operators ->
                recyclerAdapter.addAll(operators)
            }
        }
        // Setup done selecting operators
        binding.fabOperatorDone.hide()
        binding.fabOperatorDone.setOnClickListener {
        }
        // Setup viewmodel listeners
        operatorViewModel.getListenerNumSelectedOperators().observe(this, Observer { numSelected ->
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
//            UpdateManager.updateFeeds().subscribeBy(onSuccess = {
//                toast("Successfully updated feeds")
//                UpdateManager.updateFeedVersions().subscribeBy(onSuccess = {
//                    toast("Successfully updated feed version")
//                }, onError = {
//                })
//            }, onError = {
//            })
//            toast("Successfully updated operators")
        }, onError = {
            Timber.e(it, "Error fetching operators")
            toast("Failed to update operators")
        })
    }
}
