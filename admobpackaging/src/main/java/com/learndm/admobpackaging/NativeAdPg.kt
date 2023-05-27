package com.learndm.admobpackaging

import com.google.android.gms.ads.nativead.NativeAd

data class NativeAdPg(
    val id: String,
    val click: Int,
    val show: Int,
    var clicked: Int = 0,
    var showed: Int = 0,
    var nativeAd: NativeAd? = null
) {
    fun check(): Boolean {
        return clicked >= click || showed >= show
    }
}
