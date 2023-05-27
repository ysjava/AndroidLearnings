//package com.learndm.androidlearnings.network
//
//import android.os.Bundle
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import okhttp3.*
//import okio.IOException
//
//class www:AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val okHttpClient = OkHttpClient()
//
//        val request: Request = Request.Builder()
//            .get() //Method GET
//            .url("www.baidu.com")
//            .build() //构造请求信息
//
//
//        okHttpClient.newCall(request)
//            .enqueue(object : Callback {
//                //发起异步请求
//
//                override fun onFailure(call: Call, e: java.io.IOException) {
//                    e.printStackTrace()
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    //成功拿到响应
//                    val code: Int = response.code
//                    val body: ResponseBody? = response.body
//                    val string = body?.string()?:"s"
//                }
//            })
//    }
//}