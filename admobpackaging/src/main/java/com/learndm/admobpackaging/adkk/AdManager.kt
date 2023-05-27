package com.learndm.admobpackaging.adkk

import android.content.Context
import com.google.gson.Gson
import com.learndm.admobpackaging.App

object AdManager {
    lateinit var launcherInsAd: LauncherInsAd
    lateinit var backupInsAd: InsAd
//    var asd: InsAd = object : InsAd("",10,10){}.apply {
//        cacheAdTimeOut = 60
//    }

    var totalClickedNumber = 0
    var totalDisplayedNumber = 0
    private var totalClickableNumber = 0
    private var totalDisplayableNumber = 0
    fun iniData() {
        launcherInsAd = LauncherInsAd("", 0, 0)
        backupInsAd = LauncherInsAd("", 0, 0)

        //从本地获取配置信息
        val sp = App.context.getSharedPreferences("ad_local", Context.MODE_PRIVATE)
        var config = sp.getString("ad_ids", null)
        if (config == null) {
            config = getDefaultAdConfig()
            sp.edit().putString("ad_ids", config).apply()
        }
        //更新配置到缓存
        updateConfig(config)
        //网络获取配置
        loadRemoteConfig()
    }

    private fun getDefaultAdConfig(): String {
        return "{\"ins_ads\":[{\"name\":\"launch\",\"id\":\"ca-app-pub-3940256099942544/1033173712\",\"click\":2,\"show\":10},{\"name\":\"conned\",\"id\":\"ca-app-pub-3940256099942544/1033173712\",\"click\":2,\"show\":10},{\"name\":\"disconn\",\"id\":\"ca-app-pub-3940256099942544/1033173712\",\"click\":2,\"show\":10},{\"name\":\"serverinto\",\"id\":\"ca-app-pub-3940256099942544/1033173712\",\"click\":2,\"show\":8},{\"name\":\"back\",\"id\":\"ca-app-pub-3940256099942544/1033173712\",\"click\":3,\"show\":15},{\"name\":\"backup\",\"id\":\"ca-app-pub-3940256099942544/1033173712\",\"click\":3,\"show\":15}],\"native_ads\":[{\"name\":\"home\",\"id\":\"ca-app-pub-3940256099942544/2247696110\",\"click\":2,\"show\":10},{\"name\":\"connedresult\",\"id\":\"ca-app-pub-3940256099942544/2247696110\",\"click\":2,\"show\":12},{\"name\":\"disconnresult\",\"id\":\"ca-app-pub-3940256099942544/2247696110\",\"click\":2,\"show\":12},{\"name\":\"backupnative\",\"id\":\"ca-app-pub-3940256099942544/2247696110\",\"click\":3,\"show\":15}],\"banner_ads\":[{\"name\":\"servers\",\"id\":\"ca-app-pub-3940256099942544/6300978111\",\"click\":3}],\"total_click\":6,\"total_show\":50}"
    }

    private fun loadRemoteConfig() {
        // from remote load data ...
        val data = getDefaultAdConfig()
        App.context.getSharedPreferences("ad_local", Context.MODE_PRIVATE)
            .edit()
            .putString("ad_ids", data)
            .apply()

        updateConfig(data)
    }

    private fun updateConfig(config: String) {

        val entry = Gson().fromJson(config, RemoteAdConfig::class.java)
        this.totalClickableNumber = entry.totalClickableNumber
        this.totalDisplayableNumber = entry.totalDisplayableNumber

        val adConfig = entry.insAds[0]
        launcherInsAd.update(adConfig)
    }

    /**
     *
     * @return true:广告还可以进行展示或加载
     * */
    fun adNumberCheck(): Boolean {
        return totalClickedNumber < totalClickableNumber && totalDisplayedNumber < totalDisplayableNumber
    }
}