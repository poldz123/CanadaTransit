package com.rodolfonavalon.canadatransit.controller.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rodolfonavalon.canadatransit.R
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO Lets update the manager when application is started
        UpdateManager.update().subscribeBy(onSuccess = { result ->
            Timber.d("Number of results: $result")
        })
    }
}
