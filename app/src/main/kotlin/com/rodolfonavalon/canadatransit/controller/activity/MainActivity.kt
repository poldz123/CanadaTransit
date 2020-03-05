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
import com.rodolfonavalon.canadatransit.databinding.ActivityMainBinding
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.view.CustomSearchActionMode
import com.rodolfonavalon.canadatransit.view.adapter.recycler.OperatorAdapter
import com.rodolfonavalon.canadatransit.view.adapter.recycler.decorator.MarginItemDecorator
import com.rodolfonavalon.canadatransit.viewmodel.MainViewModel
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*

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
                            val filteredOperators = operators.filter {
                                it.name.toLowerCase(Locale.getDefault()).contains(queryText.trim().toLowerCase(Locale.getDefault()))
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
            onOperatorChanged(operators)
        })
        // Setup action mode
        actionMode = CustomSearchActionMode()
        actionMode.onDestroyedListener = {
            mainViewModel.operators.value?.also { operators ->
                recyclerAdapter.addAll(operators)
            }
        }
    }

    private fun onOperatorChanged(operators: List<Operator>) {
        if (actionMode.isShowing) {
            actionMode.update()
            return
        }
        recyclerAdapter.addAll(operators)
    }

//    private fun update() {
//        // TODO Lets update the manager when application is started
//        UpdateManager.updateOperators().subscribeBy(onSuccess = { operators ->
//            Timber.d("Number of operators: ${operators.size}")
//            Toast.makeText(this, "Successfully updated operators", Toast.LENGTH_LONG).show()
//        }, onError = {
//            Timber.e(it, "Error fetching operators")
//            Toast.makeText(this, "Failed to update operators", Toast.LENGTH_LONG).show()
//        })
//    }
}
