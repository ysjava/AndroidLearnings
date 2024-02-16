package com.nnsman.yaz_pre

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

abstract class ActPermission : AppCompatActivity() {
    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            var result = true
            val grantedResults = mutableListOf<String>()
            val deniedResults = mutableListOf<String>()
            it.forEach { entry ->
                if (entry.value) {
                    grantedResults.add(entry.key)
                } else {
                    result = false
                    deniedResults.add(entry.key)
                }
            }
            if (deniedResults.isNotEmpty()) {
                val cannotPoppedPermissionList = mutableListOf<String>()
                deniedResults.forEach { pm ->
                    val b = ActivityCompat.shouldShowRequestPermissionRationale(this, pm)
                    if (!b) {
                        cannotPoppedPermissionList.add(pm)
                    }
                }
                if (cannotPoppedPermissionList.isNotEmpty()) {
                    cannotPopped.invoke(cannotPoppedPermissionList)
                }
            }
            result(result)
            gratedList?.invoke(grantedResults)
            deniedList?.invoke(deniedResults)
        }

    fun checkPermissions(permissions: Array<String>): Boolean {
        permissions.forEach {
            val i = ContextCompat.checkSelfPermission(this, it)
            if (i == PackageManager.PERMISSION_DENIED) return false
        }

        return true
    }

    private lateinit var result: (Boolean) -> Unit
    private lateinit var cannotPopped: (permissions: List<String>) -> Unit
    private var gratedList: ((List<String>) -> Unit)? = null
    private var deniedList: ((List<String>) -> Unit)? = null

    fun requestPermissions(
        permissions: Array<String>,
        gratedList: ((List<String>) -> Unit)? = null,
        deniedList: ((List<String>) -> Unit)? = null,
        cannotPopped: (permissions: List<String>) -> Unit,
        result: (Boolean) -> Unit,
    ) {
        this.result = result
        this.cannotPopped = cannotPopped
        this.gratedList = gratedList
        this.deniedList = deniedList

        requestPermissionsLauncher.launch(permissions)
    }
}