package com.rodolfonavalon.canadatransit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.model.database.transit.Operator

class MainViewModel : ViewModel() {

    val operators: LiveData<List<Operator>> = CanadaTransitApplication.appDatabase.operatorDao().loadLive()
}