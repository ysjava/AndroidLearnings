package com.learndm.admobpackaging

data class BannerAdPg(
    val id: String,
    val click: Int,
    var clicked: Int = 0
) {
    fun check(): Boolean {
        return clicked >= click
    }
}
