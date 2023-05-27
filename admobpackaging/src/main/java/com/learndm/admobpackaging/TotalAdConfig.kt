package com.learndm.admobpackaging

data class TotalAdConfig(
    val totalAdShow: Int,
    val totalAdClick: Int,
    var totalAdShowed: Int = 0,
    var totalAdClicked: Int = 0
) {
    fun check(): Boolean {
        return totalAdClicked >= totalAdClick || totalAdShowed >= totalAdShow
    }
}
