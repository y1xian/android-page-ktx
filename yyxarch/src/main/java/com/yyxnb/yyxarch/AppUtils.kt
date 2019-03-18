package com.yyxnb.yyxarch


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.support.annotation.StringRes
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import com.tencent.mmkv.MMKV
import com.yyxnb.yyxarch.utils.ToastUtils
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType
import java.util.*

/**
 * 初始化相关
 */
object AppUtils {

    private lateinit var mWeakReferenceContext: WeakReference<Context>

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    fun init(application: Application) {
        mApp = application
        mWeakReferenceContext = WeakReference(application.applicationContext)
        MMKV.initialize(application.applicationContext)
    }

    var mApp: Application? = null

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    val context: Context
        get() {
            if (null != mWeakReferenceContext) {
                return mWeakReferenceContext.get()!!.getApplicationContext()
            }
            throw NullPointerException("u should init first")
        }

    fun getString(@StringRes resId: Int): String {
        return mWeakReferenceContext.get()!!.getString(resId)
    }

    //debug下吐司
    fun debugToast(str: String) {
        if (isDebug) {
            ToastUtils.normal(str)
        }
    }

    //正常吐司
    fun toast(str: String) {
        ToastUtils.normal(str)
    }

    //debug下打印
    fun debugLog(str: String) {
        if (isDebug) {
            Log.d("---", str)
        }
    }


    //跳转activity
    fun startActivity(toclass: Class<*>) {
        val intent = Intent(mWeakReferenceContext.get(), toclass)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        mWeakReferenceContext.get()!!.startActivity(intent)
    }

    /**
     * 判断App是否是Debug版本
     */
    val isDebug: Boolean
        get() {
            if (TextUtils.isEmpty(mWeakReferenceContext.get()!!.getPackageName())) {
                return false
            }
            try {
                val pm = mWeakReferenceContext.get()!!.getPackageManager()
                val ai = pm.getApplicationInfo(mWeakReferenceContext.get()!!.getPackageName(), 0)
                return ai != null && ai.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return false
            }

        }

    /**
     * 获取手机版本号
     *
     * @return 返回版本号
     */
    val appVersion: String
        get() {
            val pi: PackageInfo
            var versionNum: String
            try {
                val pm = mWeakReferenceContext!!.get()!!.getPackageManager()
                pi = pm.getPackageInfo(mWeakReferenceContext!!.get()!!.getPackageName(), PackageManager.GET_CONFIGURATIONS)
                versionNum = pi.versionName
            } catch (e: Exception) {
                versionNum = "0"
            }

            return versionNum
        }

    /**
     * 获取手机唯一标识码UUID
     *
     * @return 返回UUID
     *
     *
     * 记得添加相应权限
     * android.permission.READ_PHONE_STATE
     */
    val uuid: String
        get() {

            var uuid: String = MMKV.defaultMMKV().decodeString("PHONE_UUID", "")

            if (TextUtils.isEmpty(uuid)) {

                try {
                    val telephonyManager = mWeakReferenceContext.get()!!
                            .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    @SuppressLint("MissingPermission", "HardwareIds") val tmDevice = telephonyManager.deviceId
                    @SuppressLint("MissingPermission", "HardwareIds") val tmSerial = telephonyManager.simSerialNumber

                    @SuppressLint("HardwareIds") val androidId = Settings.Secure.getString(mWeakReferenceContext.get()!!.getContentResolver(), Settings.Secure.ANDROID_ID)
                    val deviceUuid = UUID(androidId.hashCode().toLong(), tmDevice.hashCode().toLong() shl 32 or tmSerial.hashCode().toLong())
                    val uniqueId = deviceUuid.toString()
                    uuid = uniqueId
                    MMKV.defaultMMKV().encode("PHONE_UUID", uuid)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            return uuid

        }

    /**
     * 获取手机唯一标识序列号
     *
     * @return
     */
    // Galaxy nexus 品牌类型
    //samsung 品牌
    val uniqueSerialNumber: String
        get() {
            val phoneName = Build.MODEL
            val manuFacturer = Build.MANUFACTURER
            Log.d("详细序列号", "$manuFacturer-$phoneName-$serialNumber")
            return "$manuFacturer-$phoneName-$serialNumber"
        }

    /**
     * IMEI （唯一标识序列号）
     *
     * 需与[.isPhone]一起使用
     *
     * 需添加权限 `<uses-permission android:name="android.permission.READ_PHONE_STATE"/>`
     *
     * @param context 上下文
     * @return IMEI
     */
    fun getIMEI(context: Context): String {
        val deviceId: String
        if (isPhone(context)) {
            deviceId = getDeviceIdIMEI(context)
        } else {
            deviceId = getAndroidId(context)
        }
        return deviceId
    }

    /**
     * 获取设备的IMSI
     *
     * @param context
     * @return
     */
    fun getIMSI(context: Context): String {
        return getSubscriberId(context)
    }

    /**
     * 获取设备的IMEI
     *
     * @param context
     * @return
     */
    @SuppressLint("HardwareIds")
    fun getDeviceIdIMEI(context: Context): String {
        val id: String
        //android.telephony.TelephonyManager
        val mTelephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (mTelephony.deviceId != null) {
            id = mTelephony.deviceId
        } else {
            //android.provider.Settings;
            id = Settings.Secure.getString(context.applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
        }
        return id
    }

    /**
     * 获取设备的软件版本号
     *
     * @param context
     * @return
     */
    fun getDeviceSoftwareVersion(context: Context): String {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.deviceSoftwareVersion
    }

    /**
     * 序列号
     *
     * @return
     */
    val serialNumber: String?
        get() {
            var serial: String? = null
            try {
                val c = Class.forName("android.os.SystemProperties")
                val get = c.getMethod("get", String::class.java)
                serial = get.invoke(c, "ro.serialno") as String
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return serial
        }

    /**
     * 获取ANDROID ID
     *
     * @param context
     * @return
     */
    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    /**
     * 获取唯一的用户ID
     *
     * @param context
     * @return
     */
    fun getSubscriberId(context: Context): String {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.subscriberId
    }

    /**
     * 判断设备是否是手机
     *
     * @param context 上下文
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isPhone(context: Context): Boolean {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.phoneType != TelephonyManager.PHONE_TYPE_NONE
    }

    fun <T> getNewInstance(`object`: Any?, i: Int): T? {
        if (`object` != null) {
            try {
                return ((`object`.javaClass
                        .genericSuperclass as ParameterizedType).actualTypeArguments[i] as Class<T>)
                        .newInstance()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: ClassCastException) {
                e.printStackTrace()
            }

        }
        return null

    }

    fun <T> getInstance(`object`: Any?, i: Int): T? {
        if (`object` != null) {
            try {
                return (`object`.javaClass
                        .genericSuperclass as ParameterizedType)
                        .actualTypeArguments[i] as T
            } catch (e: ClassCastException) {
                e.printStackTrace()
            }

        }
        return null

    }

    fun <T> checkNotNull(reference: T?): T {
        if (reference == null) {
            throw NullPointerException()
        }
        return reference
    }

}