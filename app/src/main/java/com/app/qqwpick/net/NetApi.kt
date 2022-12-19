package com.app.qqwpick.net

import com.app.qqwpick.base.BasePagingResult
import com.app.qqwpick.base.BaseResult
import com.app.qqwpick.data.home.*
import com.app.qqwpick.data.user.StoreBean
import com.app.qqwpick.data.user.UserBean
import com.google.gson.JsonArray
import okhttp3.RequestBody
import org.json.JSONArray
import retrofit2.http.*

interface NetApi {

    /**
     * 验证配送员账号
     */
    @GET("app/api/stores/{id}")
    suspend fun verifyAccount(
        @Path("id") id: String
    ): BaseResult<MutableList<StoreBean>>

    /**
     * 验证工号
     */
    @POST("app/api/auth/login")
    suspend fun authLogin(
        @Body route: RequestBody
    ): BaseResult<AuthLoginBean>

    /**
     * 退出登录
     */
    @POST("app/api/auth/logout")
    suspend fun loginOut(): BaseResult<Any>

    /**
     * 根据手机号获取验证码
     */
    @POST("app/api/auth/login/phone")
    suspend fun getPhoneCode(
        @Body route: RequestBody
    ): BaseResult<String>

    /**
     * 验证码登录
     */
    @POST("app/api/auth/login/code")
    suspend fun loginByCode(
        @Body route: RequestBody
    ): BaseResult<UserBean>

    /**
     * 获取抢单数量
     */
    @GET("app/api/delivery/grab-order/num")
    suspend fun getGrabNum(): BaseResult<Int>

    /**
     * 获取抢单池数据
     */
    @GET("app/api/delivery/grab-order/list")
    suspend fun getGrabList(
        @Query("pageIndex") pageSize: Int,
        @Query("pageSize") pageIndex: Int
    ): BaseResult<BasePagingResult<List<GrabListBean>>>

    /**
     * 一键抢单
     */
    @POST("app/api/delivery/grab-order")
    suspend fun grabOrder(@Body route: RequestBody): BaseResult<GrabDetailBean>

    /**
     * 获取最新版本号
     */
    @GET("app/api/new/express/version/{type}")
    suspend fun getVersion(
        @Path("type") version: String
    ): BaseResult<VersionBean>

    /**
     * 获取个人信息
     */
    @GET("app/api/expresses")
    suspend fun getPersonDetail(
    ): BaseResult<PersonDetailBean>

    /**
     * 上报经纬度
     */
    @POST("app/api/expresses/postion")
    suspend fun uploadAddress(
        @Body route: RequestBody
    ): BaseResult<Boolean>

    /**
     * 开始配送
     */
    @POST("app/api/start-delivery")
    suspend fun startDelivery(
        @Body route: RequestBody
    ): BaseResult<Boolean>

    /**
     * 完成配送
     */
    @PUT("app/api/orderSplits/{orderNo}")
    suspend fun completeDelivery(
        @Path("orderNo") orderNo: String
    ): BaseResult<Any>


    /**
     * 三方订单开始配送
     */
    @POST("app/api/optimus/self/start-delivery/order")
    suspend fun startThirdDelivery(
        @Body route: RequestBody
    ): BaseResult<Boolean>

    /**
     * 三方订单确认送达
     */
    @POST("app/api/optimus/finsh/order")
    suspend fun finishThirdDelivery(
        @Body route: RequestBody
    ): BaseResult<Boolean>

    /**
     * 获取三方订单详情
     */
    @GET("app/api/optimus/get-order_detail/{orderNo}")
    suspend fun getThirdDetail(
        @Path("orderNo") orderNo: String
    ): BaseResult<ThirdDetailBean>

    /**
     * 查询提醒的订单
     */
    @GET("app/api/remind/orders")
    suspend fun getRemindOrderList(
        @Query("startTime") startTime: String,
        @Query("endTime") endTime: String,
        @Query("outboundStatus") outboundStatus: String
    ): BaseResult<Int>

    /**
     * 查询配送订单
     */
    @GET("app/api/orderSplits")
    suspend fun getSendOrderList(
        @Query("pageIndex") pageSize: Int,
        @Query("pageSize") pageIndex: Int,
        @Query("orderNo") orderNo: String,
        @Query("outboundStatus") outboundStatus: String
    ): BaseResult<BasePagingResult<List<OrderListBean>>>

    /**
     * 查询待配送订单
     */
    @GET("app/api/delivery/order/list")
    suspend fun getLoadOrderList(
        @Query("pageIndex") pageSize: Int,
        @Query("pageSize") pageIndex: Int,
        @Query("orderNo") orderNo: String
    ): BaseResult<BasePagingResult<List<OrderLoadListBean>>>


    /**
     * 查询已完成订单
     */
    @GET("app/api/orderSplits/finsh")
    suspend fun getFinishOrderList(
        @Query("pageIndex") pageSize: Int,
        @Query("pageSize") pageIndex: Int,
        @Query("startTime") startTime: String,
        @Query("endTime") endTime: String,
        @Query("outboundStatus") outboundStatus: String,
    ): BaseResult<BasePagingResult<List<OrderListBean>>>

    /**
     * 查询三方订单
     */
    @GET("app/api/optimus/delivery/order/list")
    suspend fun getThirdOrderList(
        @Query("pageIndex") pageSize: Int,
        @Query("pageSize") pageIndex: Int,
        @Query("orderStatusList") orderStatusList: List<Int>,
//        @Query("status") status: String,//UNCONFIRMED("商家未接单", 1)CONFIRMED("商家已接单", 2)("备货完成", 3)("已发货", 4), ("已完成", 5)("已取消", 6),
        @Query("orderNo") orderNo: String,//路路通单号
        @Query("channelOrderNo") channelOrderNo: String,//中台单号
        @Query("finishSTime") finishSTime: String,
        @Query("finishETime") finishETime: String
    ): BaseResult<BasePagingResult<List<OrderThirdListBean>>>

    /**
     * 三方配送订单查询
     */
    @POST("app/api/optimus/store-self-delivery/count")
    suspend fun thirdOrderRemind(
        @Body route: RequestBody
    ): BaseResult<Int>
}
