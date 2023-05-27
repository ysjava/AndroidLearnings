package com.learndm.admobpackaging.adkk

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.learndm.admobpackaging.App
import com.learndm.admobpackaging.logd


abstract class InsAd private constructor() : Ad {

    interface LoadInsAdListener {
        fun loadSuccess() {}
        fun loadFail() {}
    }

    interface ShowInsAdListener {
        fun showSuccess() {}
        fun showFail(isShowBackupAd: Boolean) {}
        fun dismissed() {}
        fun click() {}
    }

    private val tag = "InsAd"

    //缓存
    private var cacheAd: InterstitialAd? = null

    //缓存时间
    private var cachedTime: Long = -1

    //已点击广告次数
    private var clickedNumber = 0

    //已展示次数
    private var displayedNumber = 0

    //可点击的次数
    private var clickableNumber = 0

    //可展示的次数
    private var displayableNumber = 0

    private var adUnitId: String = ""

    //是否正在加载广告
    private var isLoading = false

    //缓存广告超时时间大小,单位分钟
    var cacheAdTimeOut = 60

    //广告加载结果监听
    private var loadListener: LoadInsAdListener? = null

    //广告展示监听
    private var showListener: ShowInsAdListener? = null

    constructor(adUnitId: String, clickableNumber: Int, displayableNumber: Int) : this() {
        this.adUnitId = adUnitId
        this.clickableNumber = clickableNumber
        this.displayableNumber = displayableNumber
    }

    fun load(loadInsAdListener: LoadInsAdListener) {
        this.loadListener = loadInsAdListener
        load()
    }

    override fun load() {
        if (!isLoad() || !AdManager.adNumberCheck()) {
            logDebug("cannot load")
            cacheAd = null
            cachedTime = -1
            return
        }

        if (cacheAd != null && !adIsTimeOut()) {
            logDebug("existing cache")
            return
        }

        if (adUnitId.isBlank()) {
            logDebug("this adUnitId is wrong")
            return
        }

        if (isLoading) {
            logDebug("this ad is loading")
            return
        }

        isLoading = true
        //开始加载广告
        logDebug("start load ad...")
        val adRequest: AdRequest = AdRequest.Builder().build()
        InterstitialAd.load(App.context, adUnitId, adRequest, insLoadCallback)
    }

    fun show(activity: AppCompatActivity, listener: ShowInsAdListener) {
        this.showListener = listener
        show(activity)
    }

    override fun show(activity: AppCompatActivity) {
        if (!isShow() || !AdManager.adNumberCheck()) {
            logDebug("cannot show")
            cacheAd = null
            cachedTime = -1
            showListener?.showFail(false)
            return
        }

        if (isLoading || cacheAd == null) {
            logDebug("show fail, no cache ad or ad is loading")
            showListener?.showFail(true)
            return
        }

        cacheAd?.show(activity)
    }

    //加载广告回调
    private val insLoadCallback = object : InterstitialAdLoadCallback() {
        override fun onAdLoaded(interstitialAd: InterstitialAd) {
            logDebug("ad load success")
            cacheAd = interstitialAd
            cachedTime = System.currentTimeMillis()
            interstitialAd.setImmersiveMode(true)
            interstitialAd.fullScreenContentCallback = fullScreenContentCallback
            isLoading = false
            loadListener?.loadSuccess()
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            logDebug("ad load failed")
            cacheAd = null
            cachedTime = 1
            isLoading = false
            loadListener?.loadFail()
        }
    }

    //全屏内容回调
    private val fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdClicked() {
            clickedNumber += 1
            AdManager.totalClickedNumber += 1
            showListener?.click()
            logDebug("ins ad clicked")
        }

        override fun onAdDismissedFullScreenContent() {
            showListener?.dismissed()
            logDebug("dismiss")
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            cacheAd = null
            showListener?.showFail(false)
            logDebug("show fail")
        }

        override fun onAdShowedFullScreenContent() {
            cacheAd = null
            displayedNumber += 1
            AdManager.totalDisplayedNumber += 1
            showListener?.showSuccess()
            logDebug("show success")
        }
    }

    /**
     * 是否可以加载广告
     * 当点击次数大于等于可点击的次数或者显示次数大于等于可显示次数时,
     * 不可加载广告,反之可以加载广告
     *
     * @return true: 可加载广告  false: 不能加载广告
     * */
    private fun isLoad(): Boolean {
        return !(clickedNumber >= clickableNumber || displayedNumber >= displayableNumber)
    }

    /**
     * 是否可以展示广告
     *
     * @return true: 可展示广告  false: 不能展示广告
     * */
    private fun isShow(): Boolean {
        return !(clickedNumber >= clickableNumber || displayedNumber >= displayableNumber)
    }

    /**
     * 广告是否超时
     *
     * @return true: 已超时,需要重新加载广告  false: 未超时
     * */
    private fun adIsTimeOut(): Boolean {
        val now = System.currentTimeMillis()
        //间隔时间,单位分钟
        val intervalTime = (now - cachedTime) / 1000 / 60
        return intervalTime >= cacheAdTimeOut
    }

    fun update(adConfig: AdConfig) {
        adUnitId = adConfig.adId
        clickableNumber = adConfig.clickNumber
        displayableNumber = adConfig.displayNumber
    }

    fun logDebug(string: String) = "$string : ${this::class.java.simpleName}".logd(tag)

}