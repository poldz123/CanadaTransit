package com.rodolfonavalon.canadatransit.controller.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rodolfonavalon.canadatransit.R
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.databinding.ActivityMainBinding
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.view.adapter.recycler.OperatorAdapter
import com.rodolfonavalon.canadatransit.view.adapter.recycler.decorator.MarginItemDecorator
import com.rodolfonavalon.canadatransit.viewmodel.MainViewModel
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    private lateinit var binding: ActivityMainBinding

    private lateinit var recyclerAdapter: OperatorAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setup()
        update()
    }

    private fun setup() {
        layoutManager = LinearLayoutManager(this)
        recyclerAdapter = OperatorAdapter(mainViewModel)
        binding.recyclerOperator.layoutManager = layoutManager
        binding.recyclerOperator.adapter = recyclerAdapter
        binding.recyclerOperator.addItemDecoration(MarginItemDecorator(resources.getDimensionPixelSize(R.dimen.spacing_small)))
        mainViewModel.operators.observe(this, Observer<List<Operator>> { operators ->
            recyclerAdapter.addAll(operators)
        })
    }

    private fun update() {
        // TODO Lets update the manager when application is started
        UpdateManager.updateOperators().subscribeBy(onSuccess = { operators ->
            Timber.d("Number of operators: ${operators.size}")
        }, onError = {
            Timber.e(it, "Error fetching operators")
        })
    }
}
