package com.learndm.admobpackaging.adkk

import com.google.gson.annotations.SerializedName

data class AdConfig(val name:String,
                    @SerializedName("id")
                    val adId: String,
                    @SerializedName("click")
                    val displayNumber: Int,
                    @SerializedName("show")
                    val clickNumber: Int)
