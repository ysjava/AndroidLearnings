package com.learndm.admobpackaging

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.MobileAds
import com.learndm.admobpackaging.adkk.AdManager

class App : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        @SuppressLint("StaticFieldLeak")
        lateinit var instance: App
    }

    var processStopAt: Long = 0

    //热启动时,超过该时长就启动LauncherActivity
    private val timeOut = 5
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        instance = this
        MobileAds.initialize(this)
        AdManager.iniData()
        registerActivityLifecycleCallbacks(ActivityUtil.activityLifecycleCallbacks)
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                val topActivity = ActivityUtil.currentActivity!!
                val isAdActivity =
                    topActivity::class.java.name == "com.google.android.gms.ads.AdActivity"
                val isLauncherActivity =
                    topActivity::class.java.simpleName == "LauncherActivity"
                //如果栈顶是广告或者启动页,那么就不进行超时热启动了
                if (isNeedStartLaunch() && !isAdActivity && !isLauncherActivity)
                    startLaunchActivity()

                processStopAt = 0
            }

            override fun onStop(owner: LifecycleOwner) {
                processStopAt = System.currentTimeMillis()
            }
        })
    }

    private fun startLaunchActivity() {
        val bundle = Bundle()
        bundle.putBoolean("isHot", true)
        ActivityUtil.start(LauncherActivity::class.java, bundle)
    }

    /**
     * 是否需要启动LauncherActivity,
     * 根据应用进入后台时长判断
     *
     * */

    fun isNeedStartLaunch(): Boolean {
        val currentTime = System.currentTimeMillis()
        var need = false

        if (processStopAt != 0L) {
            need = (currentTime - processStopAt) / 1000 >= timeOut
        }

        return need
    }
}