package com.learndm.admobpackaging

import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.rewarded.RewardedAd


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private var rewardedAd: RewardedAd? = null
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var layMain: FrameLayout
    private lateinit var button1: Button
    private lateinit var button2: Button
    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = layMain.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layMain = findViewById(R.id.lay_2)
        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button1.setOnClickListener {
            loadInsAd()
        }
        button2.setOnClickListener {
            showInsAd()
        }
//        testBannerAd()

        val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad : NativeAd ->
                // Show the ad.
                "native ad was loaded(forNativeAd)".logd(TAG)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, and so on.
                    "native ad load fail: $adError".logd(TAG)
                }

                override fun onAdLoaded() {
                    "native ad was loaded(AdListener)".logd(TAG)
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                // Methods in the NativeAdOptions.Builder class can be
                // used here to specify individual options settings.
                .build())
            .build()

    }

    private fun testBannerAd() {
        val adView = AdView(this)
        layMain.addView(adView)
        adView.setAdSize(adSize)
        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                "onAdLoaded".logd(TAG)
            }

            override fun onAdOpened() {
                super.onAdOpened()
                "onAdOpened".logd(TAG)
            }

            override fun onAdClosed() {
                super.onAdClosed()
                "onAdClosed".logd(TAG)
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                "onAdFailedToLoad".logd(TAG)
            }

            override fun onAdClicked() {
                super.onAdClicked()
                "onAdClicked".logd(TAG)
            }

            override fun onAdImpression() {
                super.onAdImpression()
                "onAdImpression".logd(TAG)
            }

            override fun onAdSwipeGestureClicked() {
                super.onAdSwipeGestureClicked()
                "onAdSwipeGestureClicked".logd(TAG)
            }
        }
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun loadInsAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adError.toString().logd(TAG)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    "Ad was loaded.".logd(TAG)
                    mInterstitialAd = interstitialAd
                    addListener()
                }
            })
    }

    private fun addListener() {
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                "Ad was clicked.".logd(TAG)
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                "Ad dismissed fullscreen content.".logd(TAG)
                mInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                mInterstitialAd = null
                "Ad failed to show fullscreen content.".logd(TAG)
            }

            override fun onAdImpression() {
                "Ad recorded an impression.".logd(TAG)
                // Called when an impression is recorded for an ad.
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                "Ad showed fullscreen content.".logd(TAG)
            }
        }
    }

    private fun showInsAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            "The interstitial ad wasn't ready yet.".logd(TAG)
        }
    }

}