package com.learndm.admobpackaging

import android.util.Log

const val LOG_TAG = "LogUtil"
fun String.logd(tag: String = LOG_TAG) {
    Log.d(tag, this)
}