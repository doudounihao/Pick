package com.app.qqwpick.data

import com.app.qqwpick.BuildConfig
import com.app.qqwpick.base.BasePagingResult
import com.app.qqwpick.base.BaseRepository
import com.app.qqwpick.base.BaseResult
import com.app.qqwpick.base.StateLiveData
import com.app.qqwpick.data.home.*
import com.app.qqwpick.data.user.StoreBean
import com.app.qqwpick.data.user.UserBean
import com.app.qqwpick.net.NetApi
import com.app.qqwpick.util.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PickRepository @Inject constructor(private val api: NetApi) : BaseRepository() {

    companion object {
        private const val HOME_ARTICLE_PAGE_SIZE = 20
    }

    suspend fun verifyAccount(id: String, result: StateLiveData<MutableList<StoreBean>>) {
        executeRequest({ api.verifyAccount(id) }, result)
    }

    suspend fun authLogin(
        jobNumber: String,
        storeId: String,
        result: StateLiveData<AuthLoginBean>
    ) {
        val parm = JSONObject()
        parm.put("jobNumber", jobNumber)
        parm.put("storeId", storeId)
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            parm.toString()
        )
        executeRequest({ api.authLogin(requestBody) }, result)
    }

    suspend fun getPhoneCode(authLoginBean: AuthLoginBean, result: StateLiveData<String>) {
        val parm = JSONObject()
        parm.put("id", authLoginBean.id)
        parm.put("name", authLoginBean.name)
//        parm.put("contactTel", authLoginBean.phone)
        parm.put("storeNo", authLoginBean.storeNo)
        parm.put("jobNumber", SpUtils.getString(USER_JOB_NUMBER))
        parm.put("phone", authLoginBean.phone)
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            parm.toString()
        )
        executeRequest({ api.getPhoneCode(requestBody) }, result)
    }

    suspend fun loginByCode(
        authLoginBean: AuthLoginBean,
        userCode: String,
        result: StateLiveData<UserBean>
    ) {
        val parm = JSONObject()
        parm.put("id", authLoginBean.id)
        parm.put("storeNo", authLoginBean.storeNo)
        parm.put("jobNumber", SpUtils.getString(USER_JOB_NUMBER))
        parm.put("phone", authLoginBean.phone)
        parm.put("storeId", SpUtils.getString(STORE_ID))
        parm.put("userCode", userCode)
        parm.put("name", authLoginBean.name)
        parm.put("onTimeNum", authLoginBean.onTimeNum)
        parm.put("overTimeNum", authLoginBean.overTimeNum)
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            parm.toString()
        )
        executeRequest({ api.loginByCode(requestBody) }, result)
    }

    suspend fun getVersion(result: StateLiveData<VersionBean>) {
        executeRequest({ api.getVersion("1") }, result)
    }

    suspend fun getGrabNum(result: StateLiveData<Int>) {
        executeRequest({ api.getGrabNum() }, result)
    }

    suspend fun getGrabList(
        pageIndex: Int,
        pageSize: Int,
        result: StateLiveData<BasePagingResult<List<GrabListBean>>>
    ) {
        executeRequest({ api.getGrabList(pageIndex, pageSize) }, result)
    }

    suspend fun grabOrder(orderNo: String, result: StateLiveData<GrabDetailBean>) {
        val parm = JSONObject()
        if (!orderNo.isNullOrEmpty()) {
            parm.put("orderNo", orderNo)
        }
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            parm.toString()
        )
        executeRequest({ api.grabOrder(requestBody) }, result)
    }

    suspend fun getPersonDetail(result: StateLiveData<PersonDetailBean>) {
        executeRequest({ api.getPersonDetail() }, result)
    }

    suspend fun loginOut(result: StateLiveData<Any>) {
        executeRequest({ api.loginOut() }, result)
    }

    suspend fun startDelivery(orderNo: String, result: StateLiveData<Boolean>) {
        val parm = JSONObject()
        parm.put("orderNo", orderNo)
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            parm.toString()
        )
        executeRequest({ api.startDelivery(requestBody) }, result)
    }

    suspend fun completeDelivery(orderNo: String, result: StateLiveData<Any>) {
        executeRequest({ api.completeDelivery(orderNo) }, result)
    }

    suspend fun uploadAddress(lng: String, lat: String, result: StateLiveData<Boolean>) {
        val parm = JSONObject()
        parm.put("expressId", "")
        parm.put("lng", lng)
        parm.put("lat", lat)
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            parm.toString()
        )
        executeRequest({ api.uploadAddress(requestBody) }, result)
    }

    suspend fun getSendOrderList(
        pageIndex: Int,
        pageSize: Int,
        orderNo: String,
        outboundStatus: String, result: StateLiveData<BasePagingResult<List<OrderListBean>>>
    ) {
        executeRequest(
            { api.getSendOrderList(pageIndex, pageSize, orderNo, outboundStatus) },
            result
        )
    }

    suspend fun getLoadOrderList(
        pageIndex: Int,
        pageSize: Int,
        orderNo: String,
        result: StateLiveData<BasePagingResult<List<OrderLoadListBean>>>
    ) {
        executeRequest(
            { api.getLoadOrderList(pageIndex, pageSize, orderNo) },
            result
        )
    }

    suspend fun getFinishOrderList(
        pageIndex: Int,
        pageSize: Int,
        startTime: String,
        endTime: String,
        outboundStatus: String,
        result: StateLiveData<BasePagingResult<List<OrderListBean>>>
    ) {
        executeRequest(
            { api.getFinishOrderList(pageIndex, pageSize, startTime, endTime, outboundStatus) },
            result
        )
    }

    suspend fun getRemindOrderList(
        startTime: String,
        endTime: String,
        outboundStatus: String,
        result: StateLiveData<Int>
    ) {
        executeRequest(
            { api.getRemindOrderList(startTime, endTime, outboundStatus) },
            result
        )
    }

    suspend fun getThirdOrderList(
        pageIndex: Int,
        pageSize: Int,
        orderNo: String,
        channelOrderNo: String,
        result: StateLiveData<BasePagingResult<List<OrderThirdListBean>>>
    ) {
        val list = mutableListOf<Int>()
        list.add(4)
        executeRequest(
            {
                api.getThirdOrderList(
                    pageIndex,
                    pageSize,
                    list,
                    orderNo,
                    channelOrderNo
                )
            },
            result
        )
    }

    suspend fun startThirdDelivery(orderNo: String, result: StateLiveData<Boolean>) {
        val parm = JSONObject()
        parm.put("orderNo", orderNo)
        parm.put("courierPhone", SpUtils.getParcelable<UserBean>(USER_BEAN)?.phone.toString())
        parm.put("courierId", SpUtils.getParcelable<UserBean>(USER_BEAN)?.id.toString())
        parm.put("courierName", SpUtils.getParcelable<UserBean>(USER_BEAN)?.name.toString())
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            parm.toString()
        )
        executeRequest({ api.startThirdDelivery(requestBody) }, result)
    }

    suspend fun finishThirdDelivery(orderNo: String, result: StateLiveData<Boolean>) {
        val parm = JSONObject()
        parm.put("orderNo", orderNo)
        parm.put("courierPhone", SpUtils.getParcelable<UserBean>(USER_BEAN)?.phone.toString())
        parm.put("courierId", SpUtils.getParcelable<UserBean>(USER_BEAN)?.id.toString())
        parm.put("courierName", SpUtils.getParcelable<UserBean>(USER_BEAN)?.name.toString())
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            parm.toString()
        )
        executeRequest({ api.finishThirdDelivery(requestBody) }, result)
    }

    suspend fun getThirdDetail(orderNo: String, result: StateLiveData<ThirdDetailBean>) {
        executeRequest({ api.getThirdDetail(orderNo) }, result)
    }

    suspend fun thirdOrderRemind(sendSTime: String, sendETime: String, result: StateLiveData<Int>) {
        val parm = JSONObject()
        parm.put("storeNo", SpUtils.getString(STORE_NO))
        parm.put("startTime", sendSTime)
        parm.put("endTime", sendETime)
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            parm.toString()
        )
        executeRequest({ api.thirdOrderRemind(requestBody) }, result)
    }
}