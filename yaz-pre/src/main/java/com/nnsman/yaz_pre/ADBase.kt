package com.nnsman.yaz_pre

import android.content.Context

abstract class ADBase {
    lateinit var context: Context
    private val open = false
    val adName = ""
    protected val adId = ""

    private var clickedNumber = 0
    private var showedNumber = 0

    private var clickableNumber = 0
    private var showableNumber = 0
    var loadTime = 0L
    var cacheOutTime = 50 * 60 * 1000L
    fun check(): Boolean {
        if (!open) return false
        if (adId.isEmpty()) return false
        if (!checkLimit()) return false

        val currentTime = System.currentTimeMillis()
        val isOutTime = currentTime - loadTime > cacheOutTime
        if (!isOutTime && isExistCache()) return false

        return true
    }

    abstract fun isExistCache():Boolean

    private fun checkLimit(): Boolean {
        return clickedNumber < clickableNumber && showedNumber < showableNumber && checkGlobalLimit()
    }

    fun addClickedNumber() {
        clickedNumber += 1
        totalClickedNumber += 1

    }

    fun addShowedNumber() {
        showedNumber += 1
        totalShowedNumber += 1
    }

    fun set(){
        
    }


    companion object {
        private var totalClickedNumber = 0
        private var totalShowedNumber = 0

        private var totalClickableNumber = 0
        private var totalShowableNumber = 0

        private fun checkGlobalLimit(): Boolean {
            return totalClickedNumber < totalClickableNumber && totalShowedNumber < totalShowableNumber
        }
    }

}