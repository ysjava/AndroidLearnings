package com.learndm.admobpackaging

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import java.text.SimpleDateFormat
import java.util.*


/**
 * 预加载与正式加载分开，预加载仅仅去做广告请求，正式加载则直接从缓存中获取并展示。
 *
 * 1.增加banner广告
 * 2.增加插屏与原生的备份广告
 * 3.正式加载失败，转为展示备份广告
 * 4。预加载有成功失败回调
 * 5。正式加载有操作回调，比如点击广告出去后回来在继续执行后续操作，
 * */

object AdManagerPlus {

    /**
     * 检查是否去加载广告
     *
     * @return true:  加载  false： 不加载
     * */
    fun adLoadCheck(): Boolean {
        interstitialAdsCache.values.forEach {
            if (it.check()) {
                return false
            }
        }
        nativeAdsCache.values.forEach {
            if (it.check()) {
                return false
            }
        }
        val b: Boolean
        try {
            b = totalAdConfig.check()
        } catch (e: Exception) {
            //totalAdConfig未初始化, 不加载广告
            return false
        }
        return !b
    }

    //广告的lock,不能同时对同一个广告进行加载，
    private val adLock = hashMapOf(
        Pair(AdName.LAUNCH, false),
        Pair(AdName.CONNED, false),
        Pair(AdName.DISCONN, false),
        Pair(AdName.SERVERINTO, false),
        Pair(AdName.BACK, false),
        Pair(AdName.BACKUP, false),

        Pair(AdName.HOME, false),
        Pair(AdName.CONNEDRESULT, false),
        Pair(AdName.DISCONNRESULT, false),
        Pair(AdName.BACKUPNATIVE, false),

        Pair(AdName.SERVERS, false),
    )

    //执行cacheAd.show(receiver)展示插屏广告时，感觉应该是异步的，避免可能出现多次展示同一个广告，加个展示锁。
    private val adShowLock = hashMapOf(
        Pair(AdName.LAUNCH, false),
        Pair(AdName.CONNED, false),
        Pair(AdName.DISCONN, false),
        Pair(AdName.SERVERINTO, false),
        Pair(AdName.BACK, false),
        Pair(AdName.BACKUP, false),

        )

    enum class AdName {
        LAUNCH, CONNED, DISCONN, SERVERINTO, BACK, BACKUP, HOME, CONNEDRESULT, DISCONNRESULT, BACKUPNATIVE, SERVERS
    }

    private val interstitialAdsCache: MutableMap<String, InterstitialAdPg> = hashMapOf()
    private val nativeAdsCache: MutableMap<String, NativeAdPg> = hashMapOf()
    private val bannerAdsCache: MutableMap<String, BannerAdPg> = hashMapOf()
    private lateinit var totalAdConfig: TotalAdConfig
    private var insCompletes = mutableMapOf<AdName, (Boolean, Boolean) -> Unit>()

    fun loadInsAd(adName: AdName, callback: ((Boolean) -> Unit)? = null) {

        if (!adLoadCheck()) {
            "预加载：广告请求或展示次数已达上线，本次加载取消。".logd("LJWBNFfjqfncp")
            return
        }

        val cacheAdBg = interstitialAdsCache[adName.name.lowercase()]
        if (cacheAdBg == null) {
            "广告名: $adName   ||本次加载取消，该广告名错误。".logd("LJWBNFfjqfncp")
            return
        }
        if (cacheAdBg.interstitialAd != null) {
            "广告名: $adName   ||本次加载取消，已有缓存。".logd("LJWBNFfjqfncp")
            callback?.invoke(true)
            return
        }
        if (cacheAdBg.id.isBlank()) {
            "广告名: $adName   ||本次加载取消，广告id为空。".logd("LJWBNFfjqfncp")
            return
        }
        //lock
        val lock = adLock[adName]!!
        if (lock) {
            "广告名: $adName   ||本次加载取消，正在加载中。".logd("LJWBNFfjqfncp")
            return
        }
        adLock[adName] = true

        "广告名: $adName   ||开始加载广告信息。".logd("LJWBNFfjqfncp")
        val adRequest: AdRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            App.context,
            cacheAdBg.id,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    "广告名: $adName   ||插屏广告加载完成。".logd("LJWBNFfjqfncp")

                    //加载完成先缓存
                    interstitialAdsCache[adName.name.lowercase()]?.let {
                        it.interstitialAd = interstitialAd
                    }
                    callback?.invoke(true)

                    interstitialAd.setImmersiveMode(true)
                    interstitialAd.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                insCompletes[adName]?.invoke(true, false)
                                insCompletes.remove(adName)
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                "广告名: $adName   ||展示插屏广告失败。$adError".logd("LJWBNFfjqfncp")
                                interstitialAdsCache[adName.name.lowercase()]?.let {
                                    it.interstitialAd = null
                                }

                                insCompletes[adName]?.invoke(false, false)
                                insCompletes.remove(adName)

                                adShowLock[adName] = false

                                //如果显示的是备份广告，需要再次发起请求
                                if (adName == AdName.BACKUP)
                                    loadInsAd(AdName.BACKUP)
                            }

                            override fun onAdShowedFullScreenContent() {
                                "广告名: $adName   ||展示插屏广告成功。".logd("LJWBNFfjqfncp")
                                interstitialAdsCache[adName.name.lowercase()]?.let {
                                    it.interstitialAd = null
                                }

                                updateCount(adName, 1)
                                adShowLock[adName] = false

                                //如果显示的是备份广告，需要再次发起请求
                                if (adName == AdName.BACKUP)
                                    loadInsAd(AdName.BACKUP)
                            }

                            override fun onAdClicked() {
                                super.onAdClicked()
                                insCompletes[adName]?.invoke(true, true)
                                insCompletes.remove(adName)
//                                App.adActivity?.finish()
                                updateCount(adName, 2)
                            }
                        }

                    adLock[adName] = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    "广告名: $adName   ||插屏广告加载失败。".logd("LJWBNFfjqfn")
                    callback?.invoke(false)

                    adLock[adName] = false
                }
            })
    }

    /**
     * 显示插屏广告
     *
     * @param callback 参数 1：是否显示  2:是否点击广告
     * */
    fun showInsAd(
        adName: AdName,
        receiver: AppCompatActivity,
        callback: ((Boolean, Boolean) -> Unit)
    ) {
        if (!adLoadCheck()) {
            "广告请求或展示次数已达上线，本次加载取消。".logd("LJWBNFfjqfncp")
            callback(false, false)
            return
        }

        val showLock = adShowLock[adName]!!

        if (showLock) {
            "广告名: $adName   ||本次加载取消，该广告正在展示中。".logd("LJWBNFfjqfncp")
            //callback(false, false)
            return
        }

        val lock = adLock[adName]!!

        if (lock) {
            "广告名: $adName   ||该广告正在加载中，启动备用广告".logd("LJWBNFfjqfncp")
            if (adName == AdName.BACKUP) {
                "广告名: $adName   ||备用广告正在加载中，本次加载结束。".logd("LJWBNFfjqfnnt")
                callback(false, false)
                return
            }
            showInsAd(AdName.BACKUP, receiver, callback)
            return
        }

        val cacheAdBg = interstitialAdsCache[adName.name.lowercase()]
        if (cacheAdBg == null) {
            "广告名: $adName   ||本次加载取消，该广告名错误，服务端可能未配置该广告。".logd("LJWBNFfjqfncp")
            callback(false, false)
            return
        }

        if (cacheAdBg.id.isBlank()) {
            "广告名: $adName   ||本次加载取消，广告id为空。".logd("LJWBNFfjqfncp")
            callback(false, false)
            return
        }

        //没有缓存ad，就代表加载失败，加载失败就去展示备份广告；
        //有缓存ad，就代表加载成功；
        val cacheAd = cacheAdBg.interstitialAd

        when {
            cacheAd != null -> {
                adShowLock[adName] = true
                //保存广告操作回调，当显示广告或者点击广告后能得到操作回馈。
                insCompletes[adName] = callback
                //开始展示广告
                cacheAd.show(receiver)
            }
            adName != AdName.BACKUP -> {
                //当cacheAd为空且不是备份广告时，进行备份广告的展示
                showInsAd(AdName.BACKUP, receiver, callback)
            }
            else -> {
                //备份广告为空
                callback(false, false)
            }
        }
    }


    fun loadNativeAd(adName: AdName, callback: ((Boolean) -> Unit)? = null) {
        if (!adLoadCheck()) {
            "预加载：广告请求或展示次数已达上线，本次加载取消。".logd("LJWBNFfjqfnnt")
            return
        }

        val cacheAdBg = nativeAdsCache[adName.name.lowercase()]
        if (cacheAdBg == null) {
            "广告名: $adName   ||本次加载取消，该广告名错误。".logd("LJWBNFfjqfnnt")
            return
        }
        if (cacheAdBg.nativeAd != null) {
            "广告名: $adName   ||本次加载取消，已有缓存。".logd("LJWBNFfjqfnnt")
            return
        }
        if (cacheAdBg.id.isBlank()) {
            "广告名: $adName   ||本次加载取消，广告id为空。".logd("LJWBNFfjqfnnt")
            return
        }

        val lock = adLock[adName]!!
        if (lock) {
            "广告名: $adName   ||本次加载取消，正在加载中。".logd("LJWBNFfjqfnnt")
            return
        }

        //上锁
        adLock[adName] = true

        val adLoader = AdLoader.Builder(App.context, cacheAdBg.id)
            .forNativeAd { ad: NativeAd ->
                "广告名: $adName   ||原生广告加载成功。".logd("LJWBNFfjqfnnt")
                nativeAdsCache[adName.name.lowercase()]?.let {
                    it.nativeAd = ad
                }
                adLock[adName] = false
                callback?.invoke(true)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    "广告名: $adName   ||原生广告加载失败。".logd("LJWBNFfjqfnnt")
                    adLock[adName] = false
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    updateCount(adName, 2)

                    val receiver = nativeReceivers[adName] ?: return

//                    val lay = receiver.findViewById<FrameLayout>(R.id.lay_frame)
//                    lay.removeAllViews()
//                    lay.visibility = View.GONE
                    loadNativeAd(adName)
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        "开始加载原生广告： 名字: $adName".logd("LJWBNFfjqfnnt")
        adLoader.loadAds(AdRequest.Builder().build(), 1)
    }

    private val nativeReceivers = hashMapOf<AdName, AppCompatActivity>()
    fun showNativeAd(adName: AdName, receiver: AppCompatActivity, nativeCellId: Int) {
        if (!adLoadCheck()) {
            "广告请求或展示次数已达上线，本次加载取消。".logd("LJWBNFfjqfnnt")
            return
        }

        val lock = adLock[adName]!!
        if (lock) {
            "广告名: $adName   ||该广告正在加载中，启动备用广告".logd("LJWBNFfjqfnnt")
            if (adName == AdName.BACKUPNATIVE) {
                "广告名: $adName   ||备用广告正在加载中，本次加载结束。".logd("LJWBNFfjqfnnt")
                return
            }
            showNativeAd(AdName.BACKUPNATIVE, receiver, nativeCellId)
            return
        }

        val cacheAdBg = nativeAdsCache[adName.name.lowercase()]
        if (cacheAdBg == null) {
            "广告名: $adName   ||本次加载取消，该广告名错误，服务端可能未配置该广告".logd("LJWBNFfjqfnnt")
            return
        }
        if (cacheAdBg.id.isBlank()) {
            "广告名: $adName   ||本次加载取消，广告id为空。".logd("LJWBNFfjqfnnt")
            return
        }

        val cacheAd = cacheAdBg.nativeAd

        when {
            cacheAd != null -> {
                nativeReceivers[adName] = receiver
                "广告名: $adName   ||展示原生广告。".logd("LJWBNFfjqfnnt")
                //开始展示广告
                val adView =
                    LayoutInflater.from(receiver)
                        .inflate(nativeCellId, null) as NativeAdView
                populateNativeAdView(cacheAd, adView)
//                val adFrame = receiver.findViewById<FrameLayout>(R.id.lay_frame)
//                adFrame.removeAllViews()
//
//                adFrame.addView(adView)
//                adFrame.visibility = View.VISIBLE
                updateCount(adName, 1)

                cacheAdBg.nativeAd = null

            }
            adName != AdName.BACKUPNATIVE -> {
                showNativeAd(AdName.BACKUPNATIVE, receiver, nativeCellId)
            }
            else -> {
                "广告名: $adName   ||备份广告为空。".logd("LJWBNFfjqfnnt")
                //备份广告为空
            }
        }
    }

    fun loadBannerAd(adName: AdName, layBanner: FrameLayout) {

        if (!adLoadCheck()) {
            "广告请求或展示次数已达上线，本次加载取消。==Banner".logd("LJWBNFfjqfncp")
            return
        }

        val cacheAdBg = bannerAdsCache[adName.name.lowercase()]
        if (cacheAdBg == null) {
            "banner 广告名: $adName   ||本次加载取消，该广告名错误。".logd("LJWBNFfjqfncp")
            return
        }

        val lock = adLock[adName]!!
        if (lock) {
            "banner 广告名: $adName   ||本次加载取消，正在加载中。".logd("LJWBNFfjqfncp")
            return
        }

        adLock[adName] = true
        val adView = layBanner.getChildAt(0) as AdView

        adView.adListener = object : AdListener() {
            override fun onAdClicked() {
                updateCount(AdName.SERVERS, 2)
            }

            override fun onAdLoaded() {
                "banner 广告名: $adName   ||加载成功。".logd("LJWBNFfjqfncp")
                layBanner.visibility = View.VISIBLE
                adLock[adName] = false
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                layBanner.visibility = View.GONE
                "banner 广告名: $adName   ||加载失败。".logd("LJWBNFfjqfncp")
                adLock[adName] = false
            }
        }

        "banner 广告名: $adName   ||${cacheAdBg.id}。".logd("LJWBNFfjqfncp")

        try {
            adView.adUnitId.isBlank()
        }catch (e: Exception){
            adView.adUnitId = cacheAdBg.id
        }

//        if (adView.adUnitId.isEmpty())
//            adView.adUnitId = cacheAdBg.id
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun populateNativeAdView(ad: NativeAd, adView: NativeAdView) {
//        adView.findViewById<MediaView?>(R.id.media_view)?.let {
//            it.isEnabled = false
//            adView.mediaView = it
//        }
//        adView.headlineView = adView.findViewById(R.id.tv_head_line)
//        adView.bodyView = adView.findViewById(R.id.tv_body)
//        adView.imageView = adView.findViewById(R.id.iv_ad_icon)
//        adView.callToActionView = adView.findViewById(R.id.tv_call_action)
        adView.headlineView?.let {
            (it as TextView).text = ad.headline
        }
        adView.bodyView?.let {
            (it as TextView).text = ad.body
        }
        adView.imageView?.let {
            it.isEnabled = false
            (it as ImageView).setImageDrawable(ad.icon?.drawable)
        }
        adView.callToActionView?.let {
            (it as TextView).text = ad.callToAction
        }

        adView.setNativeAd(ad)
    }

    private fun updateCount(adName: AdName, type: Int) {
        interstitialAdsCache[adName.name.lowercase()]?.let {
            if (type == 1) it.showed++ else it.clicked++
        }
        nativeAdsCache[adName.name.lowercase()]?.let {
            if (type == 1) it.showed++ else it.clicked++
        }
        bannerAdsCache[adName.name.lowercase()]?.let {
            it.clicked++
        }

        if (type == 1) totalAdConfig.totalAdShowed++ else totalAdConfig.totalAdClicked++

        save()
    }

    private fun save() {
        val editor =
            App.context.getSharedPreferences("sp_name_ad", Context.MODE_PRIVATE)
                .edit()

        interstitialAdsCache.entries.forEach {
            editor.putInt(it.key + "clicked", it.value.clicked)
            editor.putInt(it.key + "showed", it.value.showed)
        }
        nativeAdsCache.entries.forEach {
            editor.putInt(it.key + "clicked", it.value.clicked)
            editor.putInt(it.key + "showed", it.value.showed)
        }

        editor.putInt("totalAdClicked", totalAdConfig.totalAdClicked)
        editor.putInt("totalAdShowed", totalAdConfig.totalAdShowed)
        editor.apply()
    }

    fun loadAdInfo() {
        //先加载本地的
//        var localAdInfo = get(SpKey.AD, SpFileName.AD_SP_FILE, "")
//        if (localAdInfo.isBlank()) {
//            localAdInfo =
//                "{\"interstitial_ads\":[{\"name\":\"launch\",\"id\":\"ca-app-pub-3940256099942544/1033173712\",\"click\":2,\"show\":10},{\"name\":\"conned\",\"id\":\"ca-app-pub-3940256099942544/1033173712\",\"click\":2,\"show\":10},{\"name\":\"disconn\",\"id\":\"ca-app-pub-3940256099942544/1033173712\",\"click\":2,\"show\":10},{\"name\":\"serverinto\",\"id\":\"ca-app-pub-3940256099942544/1033173712\",\"click\":2,\"show\":8},{\"name\":\"back\",\"id\":\"ca-app-pub-3940256099942544/1033173712\",\"click\":3,\"show\":15},{\"name\":\"backup\",\"id\":\"ca-app-pub-3940256099942544/1033173712\",\"click\":3,\"show\":15}],\"native_ads\":[{\"name\":\"home\",\"id\":\"ca-app-pub-3940256099942544/2247696110\",\"click\":2,\"show\":10},{\"name\":\"connedresult\",\"id\":\"ca-app-pub-3940256099942544/2247696110\",\"click\":2,\"show\":12},{\"name\":\"disconnresult\",\"id\":\"ca-app-pub-3940256099942544/2247696110\",\"click\":2,\"show\":12},{\"name\":\"backupnative\",\"id\":\"ca-app-pub-3940256099942544/2247696110\",\"click\":3,\"show\":15}],\"banner_ads\":[{\"name\":\"servers\",\"id\":\"ca-app-pub-3940256099942544/6300978111\",\"click\":3}],\"total_click\":6,\"total_show\":50}"
//            put(SpKey.AD, localAdInfo, SpFileName.AD_SP_FILE)
//        }
//        loadConfigInfo(localAdInfo)
//        initData()
//
//        //再从网络加载
//        App.instance.loadRemoteConfigInfo {
//            val adrc = it.getString("fbrc_ad")
//            put(SpKey.AD, adrc, SpFileName.AD_SP_FILE)
//            "remote fongi $adrc".logd("KJBWKBEKW")
//            //加载配置信息到AdManager中
//            loadConfigInfo(adrc)
//            initData()
//            "配置加载完成，开始发广播".logd("KFGVJWHBVMQVD")
//
//            //因为网络加载的过程成中，LauncherActivity已经发起了加载请求，
//            //所以当成功从网络获取到数据时，需要广播告知给LauncherActivity，让它再去发起加载请求
//            val intent = Intent()
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            intent.action = LaunchActivity.BROADCAST_ACTION_FIRST_LOADED
//            App.instance.sendBroadcast(intent)
//        }
    }

    private fun loadConfigInfo(adInfo: String) {
        if (adInfo.isBlank()) return

//        val entryInfo = Gson().fromJson(adInfo, RemoteAdConfig::class.java)
//        entryInfo.interstitialAds.forEach { ad ->
//            interstitialAdsCache[ad.name] = InterstitialAdPg(ad.id, ad.click, ad.show)
//        }
//        entryInfo.nativeAds.forEach { ad ->
//            nativeAdsCache[ad.name] = NativeAdPg(ad.id, ad.click, ad.show)
//        }
//        entryInfo.bannerAds.forEach { ad ->
//            bannerAdsCache[ad.name] = BannerAdPg(ad.id, ad.click)
//        }
//
//        totalAdConfig = TotalAdConfig(entryInfo.totalShow, entryInfo.totalClick)
    }

//    private fun initData() {
//        "执行 initData".logd("KJBWKBEKW")
//        val newToday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
//        val oldToday = get(SpKey.TODAY, SpFileName.TODAY_SP_FILE, "")
//        if (oldToday == "" || oldToday != newToday) {
//            "执行 initData1111".logd("KJBWKBEKW")
//            interstitialAdsCache.values.forEach {
//                it.clicked = 0
//                it.showed = 0
//            }
//
//            nativeAdsCache.values.forEach {
//                it.clicked = 0
//                it.showed = 0
//            }
//
//            bannerAdsCache.values.forEach {
//                it.clicked = 0
//            }
//
//            totalAdConfig.totalAdClicked = 0
//            totalAdConfig.totalAdShowed = 0
//
//            put(SpKey.TODAY, newToday, SpFileName.TODAY_SP_FILE)
//            //新的一天，更新安全弹窗
////            put(SpKey.SAFELYPOPUPISSHOW, false, SpFileName.TRIGGER_V_FILE)
//        } else {
//
//            interstitialAdsCache.entries.forEach {
//                it.value.clicked = getInt(it.key + "clicked")
//                it.value.showed = getInt(it.key + "showed")
//            }
//
//            nativeAdsCache.entries.forEach {
//                it.value.clicked = getInt(it.key + "clicked")
//                it.value.showed = getInt(it.key + "showed")
//            }
//
//            bannerAdsCache.entries.forEach {
//                it.value.clicked = getInt(it.key + "clicked")
//            }
//
//            totalAdConfig.totalAdClicked = getInt("totalAdClicked")
//            totalAdConfig.totalAdShowed = getInt("totalAdShowed")
//
//            "执行 initData2222 $totalAdConfig ".logd("KJBWKBEKW")
//        }
//    }
}