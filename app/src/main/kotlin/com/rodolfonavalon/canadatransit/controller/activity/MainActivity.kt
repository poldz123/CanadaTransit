package com.rodolfonavalon.canadatransit.controller.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rodolfonavalon.canadatransit.R
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO Lets update the manager when application is started
        UpdateManager.update()
    }
}
