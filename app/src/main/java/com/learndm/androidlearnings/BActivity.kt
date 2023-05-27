package com.learndm.androidlearnings

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class BActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("akmhvfjhq", "onCreate: ${this::class.java.simpleName}")
        setContentView(R.layout.activity_file)
    }

    override fun onStart() {
        super.onStart()
        Log.d("akmhvfjhq", "onStart: ${this::class.java.simpleName}")
    }

    override fun onResume() {
        super.onResume()
        Log.d("akmhvfjhq", "onResume: ${this::class.java.simpleName}")
    }

    override fun onPause() {
        super.onPause()
        Log.d("akmhvfjhq", "onPause: ${this::class.java.simpleName}")
    }

    override fun onStop() {
        super.onStop()
        Log.d("akmhvfjhq", "onStop: ${this::class.java.simpleName}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("akmhvfjhq", "onDestroy: ${this::class.java.simpleName}")
    }
}