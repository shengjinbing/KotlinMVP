package com.example.administrator.kotlinmvp.base

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.example.administrator.kotlinmvp.BuildConfig
import com.example.administrator.kotlinmvp.Constants
import com.example.administrator.kotlinmvp.utils.DisplayManager
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.commonsdk.UMConfigure
import kotlin.properties.Delegates
import android.content.pm.PackageManager
import android.os.Build
import android.net.wifi.WifiInfo
import android.Manifest.permission
import android.Manifest.permission.ACCESS_WIFI_STATE
import android.net.wifi.WifiManager
import java.net.NetworkInterface.getNetworkInterfaces
import android.annotation.TargetApi
import android.text.TextUtils
import android.Manifest.permission.READ_PHONE_STATE
import java.net.NetworkInterface
import java.util.*


/**
 * Created by Administrator on 2017/12/26 0026.
 */
class MyApplication : Application() {
    private var refWatcher: RefWatcher? = null;

    companion object {
        private val TAG = "MyApplication"

        var context: Context by Delegates.notNull()
            private set

        fun getRefWatcher(context: Context): RefWatcher? {
            val myApplication = context.applicationContext as MyApplication
            return myApplication.refWatcher
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        refWatcher = setupLeakCanary()
        initConfig()
        initUmeng()
        getDeviceInfo(this)
        Log.d("BBBBB", getDeviceInfo(this))
        DisplayManager.init(this)
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
    }

    private fun initUmeng() {
        UMConfigure.init(this, "5ab851b3f43e480d56000042", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);

        /*设置组件化的Log开关
        * 参数: boolean 默认为false，如需查看LOG设置为true
        */
        UMConfigure.setLogEnabled(true);
    }

    private fun setupLeakCanary(): RefWatcher {
        return if (LeakCanary.isInAnalyzerProcess(this)) {
            RefWatcher.DISABLED
        } else LeakCanary.install(this)
    }


    fun getDeviceInfo(context: Context): String? {
        try {
            val json = org.json.JSONObject()
            val tm = context
                    .getSystemService(Context.TELEPHONY_SERVICE) as android.telephony.TelephonyManager
            var device_id: String? = null
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                device_id = tm.deviceId
            }
            val mac = getMac(context)

            json.put("mac", mac)
            if (TextUtils.isEmpty(device_id)) {
                device_id = mac
            }
            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.contentResolver,
                        android.provider.Settings.Secure.ANDROID_ID)
            }
            json.put("device_id", device_id)
            return json.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun getMac(context: Context?): String {
        var mac: String? = ""
        if (context == null) {
            return mac!!
        }
        if (Build.VERSION.SDK_INT < 23) {
            mac = getMacBySystemInterface(context)
        } else {
            mac = getMacByJavaAPI()
            if (TextUtils.isEmpty(mac)) {
                mac = getMacBySystemInterface(context)
            }
        }
        return mac!!

    }

    @TargetApi(9)
    private fun getMacByJavaAPI(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val netInterface = interfaces.nextElement()
                if ("wlan0" == netInterface.getName() || "eth0" == netInterface.getName()) {
                    val addr = netInterface.getHardwareAddress()
                    if (addr == null || addr!!.size == 0) {
                        return null
                    }
                    val buf = StringBuilder()
                    for (b in addr!!) {
                        buf.append(String.format("%02X:", b))
                    }
                    if (buf.length > 0) {
                        buf.deleteCharAt(buf.length - 1)
                    }
                    return buf.toString().toLowerCase(Locale.getDefault())
                }
            }
        } catch (e: Throwable) {
        }

        return null
    }

    private fun getMacBySystemInterface(context: Context?): String {
        if (context == null) {
            return ""
        }
        try {
            val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (checkPermission(context, Manifest.permission.ACCESS_WIFI_STATE)) {
                val info = wifi.connectionInfo
                return info.macAddress
            } else {
                return ""
            }
        } catch (e: Throwable) {
            return ""
        }

    }

    fun checkPermission(context: Context?, permission: String): Boolean {
        var result = false
        if (context == null) {
            return result
        }
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                val clazz = Class.forName("android.content.Context")
                val method = clazz.getMethod("checkSelfPermission", String::class.java)
                val rest = method.invoke(context, permission) as Int
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true
                } else {
                    result = false
                }
            } catch (e: Throwable) {
                result = false
            }

        } else {
            val pm = context.packageManager
            if (pm.checkPermission(permission, context.packageName) == PackageManager.PERMISSION_GRANTED) {
                result = true
            }
        }
        return result
    }

    /**
     * 初始化配置
     */
    private fun initConfig() {

        val formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // 隐藏线程信息 默认：显示
                .methodCount(0)         // 决定打印多少行（每一行代表一个方法）默认：2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag("hao_zz")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
        //测试阶段建议设置成true，发布时设置为false。
        CrashReport.initCrashReport(applicationContext, Constants.BUGLY_APPID, false)
    }


    private val mActivityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            Log.d(TAG, "onCreated: " + activity.componentName.className)
        }

        override fun onActivityStarted(activity: Activity) {
            Log.d(TAG, "onStart: " + activity.componentName.className)
        }

        override fun onActivityResumed(activity: Activity) {

        }

        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityDestroyed(activity: Activity) {
            Log.d(TAG, "onDestroy: " + activity.componentName.className)
        }
    }

}