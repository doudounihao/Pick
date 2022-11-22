package com.app.qqwpick.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.os.Bundle
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import java.io.IOException
import java.util.*

class LocationUtils
private constructor(private val mContext: Context) {
    // 定位回调
    private var mLocationCallBack: LocationCallBack? = null

    // 定位管理实例
    var mLocationManager: LocationManager? = null

    //声明AMapLocationClient类对象
    var mLocationClient: AMapLocationClient? = null

    /**
     * 获取定位
     * @param mLocationCallBack 定位回调
     * @return
     */

    fun getLocation(mLocationCallBack: LocationCallBack?) {


        this.mLocationCallBack = mLocationCallBack
        if (mLocationCallBack == null) return
        // 定位管理初始化
        mLocationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // 通过GPS定位
        val gpsProvider = mLocationManager!!.getProvider(LocationManager.GPS_PROVIDER)
        // 通过网络定位
        val netProvider = mLocationManager!!.getProvider(LocationManager.NETWORK_PROVIDER)
        // 优先考虑GPS定位，其次网络定位。
        if (gpsProvider != null) {
            gpsLocation()
        } else {
            mLocationCallBack.setLocation(null)
        }
    }

    /**
     * GPS定位
     * @return
     */
    @SuppressLint("MissingPermission")
    private fun gpsLocation() {
        if (mLocationManager == null) mLocationManager =
            mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (mLocationClient == null) mLocationClient =
            AMapLocationClient(mContext)

        var mLocationOption = AMapLocationClientOption();
        mLocationClient!!.setLocationOption(mLocationOption);
        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        mLocationClient!!.stopLocation();
        mLocationClient!!.startLocation();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient!!.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient!!.startLocation()
        //设置定位回调监听
        mLocationClient!!.setLocationListener(mLocationListener)
    }

    // 定位监听
    private val mLocationListener: AMapLocationListener = object : AMapLocationListener {
        override fun onLocationChanged(p0: AMapLocation?) {
            if (mLocationCallBack != null) {
                mLocationCallBack!!.setLocation(p0)
            }
        }

    }


    /**
     * 获取地址周边信息
     * @param
     * @return
     */
    fun getAddressLine(address: Address): String {
        var result = ""
        var i = 0
        while (address.getAddressLine(i) != null) {
            val addressLine = address.getAddressLine(i)
            result = result + addressLine
            i++
        }
        return result
    }

    /**
     * @className: LocationCallBack
     * @classDescription: 定位回调
     */
    interface LocationCallBack {
        fun setLocation(location: AMapLocation?)
    }

    companion object {
        // GPS定位
        private const val GPS_LOCATION = LocationManager.GPS_PROVIDER

        // 网络定位
        private const val NETWORK_LOCATION = LocationManager.NETWORK_PROVIDER

        // 解码经纬度最大结果数目
        private const val MAX_RESULTS = 1

        // 时间更新间隔，单位：ms
        private const val MIN_TIME: Long = 1000

        // 位置刷新距离，单位：m
        private const val MIN_DISTANCE = 0.01.toFloat()

        // singleton
        private var instance: LocationUtils? = null

        /**
         * singleton
         * @param mContext 上下文
         * @return
         */
        fun getInstance(mContext: Context): LocationUtils? {
            if (instance == null) {
                instance = LocationUtils(mContext)
            }
            return instance
        }
    }
}