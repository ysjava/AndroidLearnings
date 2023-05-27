package com.learndm.admobpackaging

import com.google.android.gms.ads.interstitial.InterstitialAd

data class InterstitialAdPg(
    val id: String,
    val click: Int,
    val show: Int,
    var clicked: Int = 0,
    var showed: Int = 0,
    var interstitialAd: InterstitialAd? = null
) {
    fun check(): Boolean {
        return clicked >= click || showed >= show
    }
}
