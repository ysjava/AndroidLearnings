package com.nnsman.yaz_pre

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

abstract class ActPermission : AppCompatActivity() {
    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            var result = true
            it.forEach { entry ->
                if (!entry.value) {
                    result = false
                    return@forEach
                }
            }
            callback.invoke(result, it)
        }

    fun checkPermissions(permissions: Array<String>): Boolean {
        permissions.forEach {
            val i = ContextCompat.checkSelfPermission(this, it)
            if (i == PackageManager.PERMISSION_DENIED) return false
        }

        return true
    }

    private lateinit var callback: (result: Boolean, specific: Map<String, Boolean>) -> Unit
    fun requestPermissions(
        permissions: Array<String>,
        callback: (result: Boolean, specific: Map<String, Boolean>) -> Unit
    ) {
        this.callback = callback
        requestPermissionsLauncher.launch(permissions)
    }

}