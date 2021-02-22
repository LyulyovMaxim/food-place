package com.app.foodplace.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.foodplace.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val activityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)
    }

    //todo handle permission
}