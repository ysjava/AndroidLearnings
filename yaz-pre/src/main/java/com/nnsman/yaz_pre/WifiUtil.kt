@file:Suppress("DEPRECATION")

package com.nnsman.yaz_pre

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.nnsman.yaz_pre.App.Companion.context

object WifiUtil {

    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    fun connectWifi(
        activity: Activity,
        ssid: String,
        password: String,
        wifiCapability: WifiCapability
    ) {
        if (!isOpenWifi()) {
            Toast.makeText(context, "请先打开wifi", Toast.LENGTH_SHORT).show()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connectBySug(activity, ssid, password)
        } else {
            connectByConfig(ssid, password, wifiCapability)
        }
    }

    private fun isOpenWifi(): Boolean = wifiManager.isWifiEnabled

    //Android8以下 通过Config连接Wifi
    private fun connectByConfig(ssid: String, password: String, wifiCapability: WifiCapability) {
        val isSuccess: Boolean
        val padWifiNetwork = createWifiConfig(ssid, password, wifiCapability)
        val netId = wifiManager.addNetwork(padWifiNetwork)
        isSuccess = wifiManager.enableNetwork(netId, true)

        if (isSuccess) {
            Toast.makeText(context, context.getString(R.string.connect_success), Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(context, context.getString(R.string.connect_fail), Toast.LENGTH_SHORT)
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectBySug(activity: Activity, ssid: String, password: String) {
        val suggestion = WifiNetworkSuggestion.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(password)
            .setIsAppInteractionRequired(true) // Optional (Needs location permission)
            .build()
        val suggestionsList = listOf(suggestion)
        //wifiManager.removeNetworkSuggestions(suggestionsList)
        val status = wifiManager.addNetworkSuggestions(suggestionsList)
        if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            val intent = Intent().apply {
                action = "android.net.wifi.PICK_WIFI_NETWORK"
            }
            activity.startActivity(intent)
        }
        val intentFilter = IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (!intent.action.equals(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
                    return
                }
            }
        }
        context.registerReceiver(broadcastReceiver, intentFilter)

    }


    private fun createWifiConfig(
        ssid: String,
        password: String,
        type: WifiCapability
    ): WifiConfiguration {
        //初始化WifiConfiguration
        val config = WifiConfiguration()
        config.allowedAuthAlgorithms.clear()
        config.allowedGroupCiphers.clear()
        config.allowedKeyManagement.clear()
        config.allowedPairwiseCiphers.clear()
        config.allowedProtocols.clear()

        //指定对应的SSID
        config.SSID = "\"" + ssid + "\""

        //不需要密码的场景
        when (type) {
            WifiCapability.WIFI_CIPHER_NO_PASS -> {
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                //以WEP加密的场景
            }

            WifiCapability.WIFI_CIPHER_WEP -> {
                config.hiddenSSID = true
                config.wepKeys[0] = "\"" + password + "\""
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                config.wepTxKeyIndex = 0
            }

            WifiCapability.WIFI_CIPHER_WPA -> {
                config.preSharedKey = "\"" + password + "\""
                config.hiddenSSID = true
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                config.status = WifiConfiguration.Status.ENABLED
            }
        }

        return config
    }

    enum class WifiCapability {
        WIFI_CIPHER_WEP, WIFI_CIPHER_WPA, WIFI_CIPHER_NO_PASS
    }
}

