package com.learndm.androidlearnings.network

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.learndm.androidlearnings.R

import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NetworkAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network)
        val button = findViewById<Button>(R.id.button)
        val button3 = findViewById<Button>(R.id.button3)
        button3.setOnClickListener {
            lifecycleScope.cancel()
        }
        button.setOnClickListener {

            lifecycleScope.launch {
                NetworkTest.testDownloadSpeed().collect {
                    Log.d("lqighfqwdqw", "${Thread.currentThread()}== $it ===")
                }
            }
        }
    }
}