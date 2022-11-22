package com.app.qqwpick.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.amap.api.location.AMapLocation
import com.app.qqwpick.MainApplication
import com.app.qqwpick.R
import com.app.qqwpick.base.BasePagingResult
import com.app.qqwpick.base.StateLiveData
import com.app.qqwpick.data.PickRepository
import com.app.qqwpick.data.home.OrderListBean
import com.app.qqwpick.data.user.UserBean
import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.util.*
import com.hjq.toast.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject


@AndroidEntryPoint
class LooperService : Service(), LifecycleOwner {

    @Inject
    lateinit var re: PickRepository
    private var isLooper = true
    private var mNavAudioAttrib: AudioAttributes? = null
    private var soundpool: SoundPool? = null

    private var startTime: String = ""
    private var endTime: String = ""
    private var soundMap = HashMap<Int, Int>()
    private var mAudioManager: AudioManager? = null
    private val threadPool = Executors.newFixedThreadPool(1)

    private val mLifecycleRegistry = LifecycleRegistry(this)
    override fun onBind(intent: Intent?): IBinder? {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        return LocalBinder()
    }

    class LocalBinder : Binder() {
        fun getService(): LooperService {
            return LooperService()
        }
    }

    override fun onCreate() {
        super.onCreate()
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        initLiveDataObserver()
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }


    override fun onUnbind(intent: Intent?): Boolean {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        grabNum.removeObservers(this)
        isLooper = false
        threadPool.shutdown()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            isLooper = true
            initSoundPool()
            startLooper()
            mAudioManager = getSystemService(
                AUDIO_SERVICE
            ) as AudioManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mNavAudioAttrib = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                    .build()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initSoundPool() {
        //当前系统的SDK版本大于等于21(Android 5.0)时
        soundpool = if (Build.VERSION.SDK_INT > 21) {
            val builder = SoundPool.Builder()
            //传入音频数量
            builder.setMaxStreams(10)
            //AudioAttributes是一个封装音频各种属性的方法
            val attrBuilder = AudioAttributes.Builder()
            //设置音频流的合适的属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_SYSTEM) //STREAM_MUSIC
            //加载一个AudioAttributes
            builder.setAudioAttributes(attrBuilder.build())
            builder.build()
        } else {
            SoundPool(10, AudioManager.STREAM_SYSTEM, 0)
        }
        soundMap.put(1, soundpool!!.load(MainApplication.getInstance(), R.raw.sendremind, 1))
    }

    private fun startLooper() {
        threadPool.execute {
            while (isLooper) {
                try {
                    if (!SpUtils.getParcelable<UserBean>(USER_BEAN)?.token.isNullOrEmpty()) {
                        getdata()
                        getLocation()
                        Thread.sleep(15000)
                        getRemind()
                        Thread.sleep(15000)
                    }
                } catch (e: InterruptedException) {
                }
            }
        }
    }

    var grabNum = StateLiveData<Int>()
    var isUpload = StateLiveData<Boolean>()
    var remindList = StateLiveData<BasePagingResult<List<OrderListBean>>>()

    fun getdata() {
        if (SpUtils.getBoolean(GRAB_ORDER_REMIND_SWITCH) == true) {
            GlobalScope.launch {
                re.getGrabNum(grabNum)
            }
        }
    }

    private fun getLocation() {
        if (SpUtils.getBoolean(MAP_OPEN) == true) {
            LocationUtils.getInstance(this)!!.getLocation(object : LocationUtils.LocationCallBack {
                override fun setLocation(location: AMapLocation?) {
                    if (location != null) {
                        GlobalScope.launch {
                            re.uploadAddress(
                                location.longitude.toString(),
                                location.latitude.toString(),
                                isUpload
                            )
                        }
                    }
                }
            })
        }
    }

    private fun getRemind() {
        if (SpUtils.getBoolean(NEW_ORDER_REMIND_SWITCH) == true) {
            startTime = DateUtils.getCurrentTime1()
            GlobalScope.launch {
                re.getRemindOrderList(startTime, endTime, "3", remindList)
            }
        }
    }

    private fun initLiveDataObserver() {
        grabNum.observe(this, {
            when (it.dataStatus) {
                DataStatus.STATE_ERROR -> {
                }
                DataStatus.STATE_SUCCESS -> {
                }
            }
        })

        remindList.observe(this, {
            when (it.dataStatus) {
                DataStatus.STATE_ERROR -> {
                    endTime = startTime
                }
                DataStatus.STATE_SUCCESS -> {
                    endTime = startTime
                }
            }
        })
    }

    //停止由AlarmManager启动的循环
    companion object {
        fun stop(mContext: Context) {
            val intent = Intent(mContext, LooperService::class.java)
            mContext.stopService(intent)
        }
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }

}