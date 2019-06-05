package com.yyxnb.yyxarch.livedata

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import androidx.lifecycle.LiveData

/**
 * 监听网络变化
 */
class NetworkLiveData(context: Context) : LiveData<NetworkInfo>() {

    private val mContext: Context = context.applicationContext
    private val mNetworkReceiver: NetworkReceiver
    private val mIntentFilter: IntentFilter

    init {
        mNetworkReceiver = NetworkReceiver()
        mIntentFilter = IntentFilter()
        mIntentFilter.addAction("android.net.ethernet.ETHERNET_STATE_CHANGED")
        mIntentFilter.addAction("android.net.ethernet.STATE_CHANGE")
        mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        mIntentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED")
        mIntentFilter.addAction("android.net.wifi.STATE_CHANGE")
        mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)

    }

    override fun onActive() {
        super.onActive()
        mContext.registerReceiver(mNetworkReceiver, mIntentFilter)
    }

    override fun onInactive() {
        super.onInactive()
        mContext.unregisterReceiver(mNetworkReceiver)
    }

    private class NetworkReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val manager = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = manager.activeNetworkInfo
            getInstance(context).postValue(activeNetwork)

        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        internal var mNetworkLiveData: NetworkLiveData? = null

        fun getInstance(context: Context): NetworkLiveData {
            if (mNetworkLiveData == null) {
                mNetworkLiveData = NetworkLiveData(context)
            }
            return mNetworkLiveData!!
        }
    }

}
