package com.learndm.androidlearnings.bitmap

import android.os.Environment
import java.io.File

object Scanner {
    fun scan(
        conditions: ArrayList<(absolutePath: String) -> Boolean>,
        results: ArrayList<ArrayList<File>>
    ) {
        if (conditions.size != results.size) error("conditions.size != results.size")
        val esd = Environment.getExternalStorageDirectory().listFiles()
        req(esd, conditions, results)
    }

    private fun req(
        arrays: Array<File>?,
        conditions: ArrayList<(absolutePath: String) -> Boolean>,
        results: ArrayList<ArrayList<File>>
    ) {
        arrays?.let {
            for (file in it) {
                if (file.isDirectory) {
                    req(file.listFiles(), conditions, results)
                    continue
                }
                for (i in 0 until conditions.size) {
                    if (conditions[i](file.absolutePath)) {
                        results[i].add(file)
                        break
                    }
                }
            }
        }
    }
}