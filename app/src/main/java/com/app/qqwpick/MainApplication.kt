package com.app.qqwpick

import android.app.Application
import android.content.Context
import android.content.Intent
import com.app.qqwpick.service.LooperService
import com.app.qqwpick.util.*
import com.fanjun.keeplive.KeepLive
import com.fanjun.keeplive.config.ForegroundNotification
import com.fanjun.keeplive.config.ForegroundNotificationClickListener
import com.fanjun.keeplive.config.KeepLiveService
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.style.KongzueStyle
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import update.UpdateAppUtils

@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        MMKV.initialize(this)
        // 当前项目是否已经适配了分区存储的特性
        XXPermissions.setScopedStorage(true)

        ToastUtils.init(this)
        UpdateAppUtils.init(this)
        DialogX.init(this)
        DialogX.globalStyle = KongzueStyle.style();
        initUser()
        startKeepLive()
    }

    fun initUser() {
        if (SpUtils.getBooleanTrue(NEW_ORDER_REMIND_SWITCH) ?: true) {
            SpUtils.put(NEW_ORDER_REMIND_SWITCH, true)
        }
        if (SpUtils.getBooleanTrue(GRAB_ORDER_REMIND_SWITCH) ?: true) {
            SpUtils.put(GRAB_ORDER_REMIND_SWITCH, true)
        }
        if ((SpUtils.getString(NEW_ORDER_REMIND_SWITCH_TYPE) ?: "").isNullOrEmpty()) {
            SpUtils.put(NEW_ORDER_REMIND_SWITCH_TYPE, REMIND_TYPE_TWO)
        }
        if ((SpUtils.getString(GRAB_ORDER_REMIND_SWITCH_TYPE) ?: "").isNullOrEmpty()) {
            SpUtils.put(GRAB_ORDER_REMIND_SWITCH_TYPE, REMIND_TYPE_TWO)
        }
        if (SpUtils.getBooleanTrue(MAP_OPEN) ?: true) {
            SpUtils.put(MAP_OPEN, true)
        }
    }

    fun startKeepLive() {
        //定义前台服务的默认样式。即标题、描述和图标
        val foregroundNotification = ForegroundNotification("数智拣配正在运行",
            "实时接收订单数据，请注意及时查看",
            R.mipmap.ic_launcher,  //定义前台服务的通知点击事件
            object : ForegroundNotificationClickListener {
                override fun foregroundNotificationClick(context: Context?, intent: Intent?) {
                }
            })
        //启动保活服务
        KeepLive.startWork(this,
            KeepLive.RunMode.ENERGY,
            foregroundNotification,
            object : KeepLiveService {
                /**
                 * 运行中
                 * 由于服务可能会多次自动启动，该方法可能重复调用
                 */
                override fun onWorking() {
                    LooperService.stop(this@MainApplication)
                    val intent = Intent(
                        this@MainApplication,
                        LooperService::class.java
                    )
                    startService(intent)
                }

                /**
                 * 服务终止
                 * 由于服务可能会被多次终止，该方法可能重复调用，需同onWorking配套使用，如注册和注销broadcast
                 */
                override fun onStop() {
                    LooperService.stop(this@MainApplication)
                }
            }
        )
    }

    companion object {
        private lateinit var instance: Application
        fun getInstance(): Application {
            return instance
        }
    }
}