package com.learndm.androidlearnings

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() {

    private val TAG: String = "qkoygcqwjk"
    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button2).setOnClickListener {
            //startActivity(Intent(this,BActivity::class.java))
        }
    }


    suspend fun test() = suspendCoroutine<Unit> {

    }

    override fun onResume() {
        super.onResume()
        Log.d("akmhvfjhq", "onResume: ${this::class.java.simpleName}")
    }

    override fun onStart() {
        super.onStart()
        Log.d("akmhvfjhq", "onStart: ${this::class.java.simpleName}")
    }

    override fun onPause() {
        super.onPause()
        Log.d("akmhvfjhq", "onPause: ${this::class.java.simpleName}")
    }

    override fun onStop() {
        super.onStop()
        Log.d("akmhvfjhq", "onStop: ${this::class.java.simpleName}")
    }

}