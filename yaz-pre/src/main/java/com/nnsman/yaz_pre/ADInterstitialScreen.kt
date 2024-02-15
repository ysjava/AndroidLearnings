package com.nnsman.yaz_pre

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.delay

class ADInterstitialScreen : ADBase() {
    companion object {
        private var cache: InterstitialAd? = null
    }

    fun startLoadAD() {
        val adRequest: AdRequest = AdRequest.Builder().build()
        val loadCallback = object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(p0: InterstitialAd) {
                super.onAdLoaded(p0)
                cache = p0
                cacheOutTime = System.currentTimeMillis()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                cache = null
            }
        }
        InterstitialAd.load(context, adId, adRequest, loadCallback)
    }

    fun startShowAD(act: AppCompatActivity, minTime: Long, maxTime: Long, completed: () -> Unit) {
        act.lifecycleScope.launchWhenResumed {
            delay(minTime)

            waitForCache(maxTime)

            if (cache == null) {
                completed()
                return@launchWhenResumed
            }

            cache?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    super.onAdClicked()
                    addClickedNumber()
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    addShowedNumber()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    cache = null
                    completed()
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    cache = null
                    completed()
                }
            }

            cache?.show(act)
        }

    }

    private suspend fun waitForCache(maxTime: Long) {
        var time = 0L
        while (cache == null && time < maxTime) {
            delay(100)
            time += 100
        }
    }

    override fun isExistCache(): Boolean {
        return cache != null
    }

}