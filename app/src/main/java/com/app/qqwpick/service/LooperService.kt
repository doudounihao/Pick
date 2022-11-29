package com.app.qqwpick.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.*
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.*
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.observe
import com.amap.api.location.AMapLocation
import com.app.qqwpick.MainApplication
import com.app.qqwpick.R
import com.app.qqwpick.base.StateLiveData
import com.app.qqwpick.data.PickRepository
import com.app.qqwpick.data.user.UserBean
import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
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
        handler.removeCallbacksAndMessages(null)
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
        soundMap.put(2, soundpool!!.load(MainApplication.getInstance(), R.raw.qd_message, 1))
        soundMap.put(3, soundpool!!.load(MainApplication.getInstance(), R.raw.cs_message, 1))
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
    var remindOrder = StateLiveData<Int>()

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
                re.getRemindOrderList(startTime, endTime, "3", remindOrder)
            }
        }
    }

    private fun initLiveDataObserver() {
        grabNum.observe(this, {
            when (it.dataStatus) {
                DataStatus.STATE_ERROR -> {
                }
                DataStatus.STATE_SUCCESS -> {
                    if (it.data!! > 0) {
                        val message = Message.obtain()
                        message.what = 2
                        handler.sendMessageDelayed(message, 15000)
                        EventBus.getDefault()
                            .postSticky(MessageEvent(MessageType.ShowGrab).put(it.data!!))
                    }
                }
            }
        })

        remindOrder.observe(this, {
            when (it.dataStatus) {
                DataStatus.STATE_ERROR -> {
                    endTime = startTime
                }
                DataStatus.STATE_SUCCESS -> {
                    if (it.data!! > 0) {
                        val message = Message.obtain()
                        message.what = 1
                        handler.sendMessageDelayed(message, 15000)
                    }
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

    @SuppressLint("HandlerLeak")
    private var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            try {
                if (msg.what == 1) { //订单提醒
                    var status = SpUtils.getString(NEW_ORDER_REMIND_SWITCH_TYPE) ?: ""
                    if (REMIND_TYPE_ONE.equals(status)) { //系统提醒
                        val uri =
                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        val rt = RingtoneManager.getRingtone(MainApplication.getInstance(), uri)
                        rt.play()
                    } else if (REMIND_TYPE_TWO.equals(status)) { //人工语音
                        if (soundpool != null) {
                            handleNavStart()
                            soundpool!!.play(soundMap[1]!!, 1f, 1f, 1, 0, 1f)
                            this.postDelayed({ handleNavEnd() }, 5000)
                        }
                    }
                } else if (msg.what == 2) { //抢单订单
                    var status = SpUtils.getString(GRAB_ORDER_REMIND_SWITCH_TYPE) ?: ""
                    if (REMIND_TYPE_ONE.equals(status)) { //系统提醒
                        val uri =
                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        val rt = RingtoneManager.getRingtone(MainApplication.getInstance(), uri)
                        rt.play()
                    } else if (REMIND_TYPE_TWO.equals(status)) { //人工语音
                        if (soundpool != null) {
                            handleNavStart()
                            soundpool!!.play(soundMap[2]!!, 1f, 1f, 1, 0, 1f)
                            this.postDelayed({ handleNavEnd() }, 5000)
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun handleNavStart() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mAudioManager != null) {
                val naviFocusRequest =
                    AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setOnAudioFocusChangeListener(mNavFocusListener)
                        .setAudioAttributes(mNavAudioAttrib!!)
                        .setFocusGain(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK).build()
                mAudioManager!!.requestAudioFocus(naviFocusRequest)
            }
        } catch (e: java.lang.Exception) {
            Log.e("looperservice", "Failed to set active focus", e)
        }
    }

    private fun handleNavEnd() {
        if (mAudioManager != null) {
            mAudioManager!!.abandonAudioFocus(mNavFocusListener)
        }
    }

    private val mNavFocusListener =
        OnAudioFocusChangeListener { focusChange ->
            Log.i(
                "looperservice",
                "Nav focus change:$focusChange"
            )
        }
}