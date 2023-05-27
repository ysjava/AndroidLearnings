package com.learndm.admobpackaging.adkk

import com.google.gson.annotations.SerializedName

data class RemoteAdConfig(
    @SerializedName("ins_ads")
    val insAds: List<AdConfig>,
    @SerializedName("native_ads")
    val nativeAds: List<AdConfig>,
    @SerializedName("banner_ads")
    val bannerAds: List<AdConfig>,
    @SerializedName("total_click")
    val totalClickableNumber: Int,
    @SerializedName("total_show")
    val totalDisplayableNumber: Int
)
