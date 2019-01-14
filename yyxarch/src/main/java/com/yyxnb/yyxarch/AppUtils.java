package com.yyxnb.yyxarch;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.yyxnb.yyxarch.utils.PreferencesUtils;
import com.yyxnb.yyxarch.utils.ToastUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.UUID;

/**
 * 初始化相关
 */
public class AppUtils {

    private static WeakReference<Context> mWeakReferenceContext;

    private AppUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        mWeakReferenceContext = new WeakReference<>(context);
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (mWeakReferenceContext != null) {
            return mWeakReferenceContext.get().getApplicationContext();
        }
        throw new NullPointerException("u should init first");
    }

    public static String getString(@StringRes int resId) {
        return mWeakReferenceContext.get().getString(resId);
    }

    //debug下吐司
    public static void debugToast(String str) {
        if (isDebug()) {
            ToastUtils.normal(str);
        }
    }

    //正常吐司
    public static void toast(String str) {
        ToastUtils.normal(str);
    }

    //debug下打印
    public static void debugLog(String str) {
        if (isDebug()) {
            Log.i("---", str);
        }
    }


    //跳转activity
    public static void startActivity(Class toclass) {
        Intent intent = new Intent(mWeakReferenceContext.get(), toclass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        mWeakReferenceContext.get().startActivity(intent);
    }

    /**
     * 判断App是否是Debug版本
     */
    public static boolean isDebug() {
        if (TextUtils.isEmpty(mWeakReferenceContext.get().getPackageName())) {
            return false;
        }
        try {
            PackageManager pm = mWeakReferenceContext.get().getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(mWeakReferenceContext.get().getPackageName(), 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取手机版本号
     *
     * @return 返回版本号
     */
    public static String getAppVersion() {
        PackageInfo pi;
        String versionNum;
        try {
            PackageManager pm = mWeakReferenceContext.get().getPackageManager();
            pi = pm.getPackageInfo(mWeakReferenceContext.get().getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionNum = pi.versionName;
        } catch (Exception e) {
            versionNum = "0";
        }
        return versionNum;
    }

    /**
     * 获取手机唯一标识码UUID
     *
     * @return 返回UUID
     * <p>
     * 记得添加相应权限
     * android.permission.READ_PHONE_STATE
     */
    public static String getUUID() {

        String uuid = (String) PreferencesUtils.get("PHONE_UUID", "");

        if (TextUtils.isEmpty(uuid)) {

            try {
                TelephonyManager telephonyManager = (TelephonyManager) mWeakReferenceContext.get()
                        .getSystemService(Context.TELEPHONY_SERVICE);
                @SuppressLint({"MissingPermission", "HardwareIds"}) String tmDevice = telephonyManager.getDeviceId();
                @SuppressLint({"MissingPermission", "HardwareIds"}) String tmSerial = telephonyManager.getSimSerialNumber();

                @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(mWeakReferenceContext.get().getContentResolver(), Settings.Secure.ANDROID_ID);
                UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
                String uniqueId = deviceUuid.toString();
                uuid = uniqueId;
                PreferencesUtils.put("PHONE_UUID", uuid);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return uuid;

    }

    /**
     * 获取手机唯一标识序列号
     *
     * @return
     */
    public static String getUniqueSerialNumber() {
        String phoneName = Build.MODEL;// Galaxy nexus 品牌类型
        String manuFacturer = Build.MANUFACTURER;//samsung 品牌
        Log.d("详细序列号", manuFacturer + "-" + phoneName + "-" + getSerialNumber());
        return manuFacturer + "-" + phoneName + "-" + getSerialNumber();
    }

    /**
     * IMEI （唯一标识序列号）
     * <p>需与{@link #isPhone(Context)}一起使用</p>
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.READ_PHONE_STATE"/>}</p>
     *
     * @param context 上下文
     * @return IMEI
     */
    public static String getIMEI(Context context) {
        String deviceId;
        if (isPhone(context)) {
            deviceId = getDeviceIdIMEI(context);
        } else {
            deviceId = getAndroidId(context);
        }
        return deviceId;
    }

    /**
     * 获取设备的IMSI
     *
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        return getSubscriberId(context);
    }

    /**
     * 获取设备的IMEI
     *
     * @param context
     * @return
     */
    @SuppressLint("HardwareIds")
    public static String getDeviceIdIMEI(Context context) {
        String id;
        //android.telephony.TelephonyManager
        TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null) {
            id = mTelephony.getDeviceId();
        } else {
            //android.provider.Settings;
            id = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return id;
    }

    /**
     * 获取设备的软件版本号
     *
     * @param context
     * @return
     */
    public static String getDeviceSoftwareVersion(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceSoftwareVersion();
    }

    /**
     * 序列号
     *
     * @return
     */
    public static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

    /**
     * 获取ANDROID ID
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取唯一的用户ID
     *
     * @param context
     * @return
     */
    public static String getSubscriberId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSubscriberId();
    }

    /**
     * 判断设备是否是手机
     *
     * @param context 上下文
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isPhone(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    public static <T> T getNewInstance(Object object, int i) {
        if (object != null) {
            try {
                return ((Class<T>) ((ParameterizedType) (object.getClass()
                        .getGenericSuperclass())).getActualTypeArguments()[i])
                        .newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }

        }
        return null;

    }

    public static <T> T getInstance(Object object, int i) {
        if (object != null) {
            try {
                return (T) ((ParameterizedType) object.getClass()
                        .getGenericSuperclass())
                        .getActualTypeArguments()[i];
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    public static @NonNull
    <T> T checkNotNull(final T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }
}