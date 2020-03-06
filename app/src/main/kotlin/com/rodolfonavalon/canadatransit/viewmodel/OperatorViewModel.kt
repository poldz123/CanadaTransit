package com.rodolfonavalon.canadatransit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.viewmodel.util.SingleLiveEvent

class OperatorViewModel : ViewModel() {

    val operators: LiveData<List<Operator>> = CanadaTransitApplication.appDatabase.operatorDao().loadLive()

    private val listenerNumSelectedOperators = SingleLiveEvent<Int>()
    fun getListenerNumSelectedOperators(): LiveData<Int> = listenerNumSelectedOperators
    fun didNumSelectedOperators(numSelectedOperators: Int) {
        listenerNumSelectedOperators.value = numSelectedOperators
    }
}
