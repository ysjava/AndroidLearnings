package com.learndm.androidlearnings.network

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

object NetworkTest {
    const val downloadFileUrl =
        "https://software.download.prss.microsoft.com/dbazure/Win10_22H2_English_x64v1.iso?t=c43339fa-33d4-4b9f-bd71-0e9784b2101a&e=1688961242&h=20df1c0315867aef4235d2bf3a9fc930dd5735f5cf87831fe2591abfd372cf3b"

    suspend fun testDownloadSpeed(): Flow<Long> {
        var oldDownloadedLen = 0L
        return flow {
            val client =
                OkHttpClient.Builder().connectTimeout(10000L, TimeUnit.MILLISECONDS).build()
            val request = Request.Builder().get().url(downloadFileUrl).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val ins = response.body!!.byteStream()
                val buffer = ByteArray(1024)
                var len: Int
                var curDownloadedLen = 0L
                do {
                    len =
                        try {
                            ins.read(buffer)
                        } catch (e: Exception) {
                            -1
                        }
                    curDownloadedLen += len
                    emit(curDownloadedLen)
                } while (len > -1)
                ins.close()
            }
        }.flowOn(Dispatchers.IO).sample(1000).map {
            val temp = it - oldDownloadedLen
            oldDownloadedLen = it
            temp
        }
    }
}