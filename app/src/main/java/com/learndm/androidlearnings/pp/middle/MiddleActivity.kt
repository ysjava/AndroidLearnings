package com.learndm.androidlearnings.pp.middle

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.learndm.androidlearnings.R
import com.tamsiree.rxpay.wechat.pay.MD5
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager
import kotlin.concurrent.thread

class MiddleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val client = OkHttpClient.Builder()
            .sslSocketFactory(ee.getSSLSocketFactory(), object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {

                }

                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {

                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf<X509Certificate>()
                }

            })
            .hostnameVerifier { hostname, session -> true }
            .build()
        val ts = System.currentTimeMillis().toString()
        val rrr1 = ee.getMD5String("83070874com.nnsman.voicetranslator977E1FLE4B4VU09K$ts")
        val rrr2 =
            MD5.getMessageDigest("83070874com.nnsman.voicetranslator977E1FLE4B4VU09K$ts".toByteArray())
        Log.d("jhvqfwfd", "12md5 : $rrr1")
        Log.d("jhvqfwfd", "34md5 : $rrr2")
        Log.d("jhvqfwfd", "ts : $ts")
        val request = Request.Builder()
            .url("https://ioengj.nnsvoicetranslator.com/middleground/paas/mobile/edition1.0/appsinfo?appsId=83070874&apparatusId=x&country=en&unitNum=1")
            .header("pkg", "com.nnsman.voicetranslator")
            .header("dynKeyFlag", "101")
            .header("timestamp", ts)
            .header("Authorization", rrr1)
            .get()
            .build()


        thread {

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("jhvqfwfd", "onFailure : $e")
                }

                override fun onResponse(call: Call, response: Response) {
//                    Log.d("jhvqfwfd", "onResponse : ${response.body!!.string()}")
                    Log.d("jhvqfwfd", "onResponse==> 开始解析...")

                    val r = ee.decryptFromBase64(
                        "F1pI63RyP8J8qVys3VAbf9CASseJ4jQksPFKQ5nzQ6HCe1O6sJvrkiFmjlvu7Ou8hkbozQG05lJEDLtyqW1qvw==",
                        "977E1FLE4B4VU09K"
                    )

                    Log.d("jhvqfwfd", "<==onResponse 解析结果: $r")

                }

            })
        }


    }

}

