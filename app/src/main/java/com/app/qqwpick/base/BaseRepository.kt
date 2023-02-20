package com.app.qqwpick.base

import android.content.Intent
import com.app.qqwpick.MainApplication
import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.net.ResultException
import com.app.qqwpick.net.ResultState
import com.app.qqwpick.ui.user.AccountActivity
import com.app.qqwpick.ui.user.LoginActivity
import com.app.qqwpick.util.ActivityUtil
import com.app.qqwpick.util.ROLE_TYPE
import com.app.qqwpick.util.SpUtils
import com.app.qqwpick.util.USER_BEAN
import com.bbq.net.exception.DealException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*

/**
 * 基础的 Repository
 */
open class BaseRepository {
    /**
     * 请求
     */
    suspend fun <T : Any> callRequest(
        call: suspend () -> ResultState<T>
    ): ResultState<T> {
        return try {
            call()
        } catch (e: Exception) {
            //这里统一处理异常
            e.printStackTrace()
            ResultState.Error(DealException.handlerException(e))
        }
    }

    /**
     * 处理返回结果
     */
    suspend fun <T : Any> handleResponse(
        response: BaseResult<T>,
        successBlock: (suspend CoroutineScope.() -> Unit)? = null,
        errorBlock: (suspend CoroutineScope.() -> Unit)? = null
    ): ResultState<T> {
        return coroutineScope {
            if (!response.code.equals("0")) {
                //返回的不成功
                errorBlock?.let { it() }
                //结果回调
                ResultState.Error(
                    ResultException(
                        response.code.toString(),
                        response.msg ?: response.message ?: ""
                    )
                )
            } else {
                //返回成功
                successBlock?.let { it() }
                //结果回调
                ResultState.Success(response.data)
            }
        }
    }

    suspend fun <T : Any> executeRequest(
        block: suspend () -> BaseResult<T>,
        stateLiveData: StateLiveData<T>
    ) {
        var baseResp = BaseResult<T>()
        try {
            baseResp.dataStatus = DataStatus.STATE_LOADING
            stateLiveData.postValue(baseResp)
            //将结果复制给baseResp
            baseResp = block.invoke()
            if (baseResp.code.equals("0")) {
                baseResp.dataStatus = DataStatus.STATE_SUCCESS
            } else {
                //服务器请求错误
                baseResp.dataStatus = DataStatus.STATE_ERROR
                baseResp.exception = ResultException(
                    baseResp.code.toString(),
                    baseResp.msg ?: baseResp.message ?: ""
                )
            }
        } catch (e: Exception) {
            //非后台返回错误，捕获到的异常
            baseResp.dataStatus = DataStatus.STATE_ERROR
            baseResp.exception = DealException.handlerException(e)
        } finally {
            stateLiveData.postValue(baseResp)
            if (baseResp.exception?.errCode.equals("401")) {
                ActivityUtil.finishAllActivity()
                SpUtils.put(ROLE_TYPE, "")
                SpUtils.removeKey(USER_BEAN)
                val intent = Intent(MainApplication.getInstance(), AccountActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                MainApplication.getInstance().startActivity(intent)
            }
        }
    }

    /**
     * 方式二：结合Flow请求数据。
     * 根据Flow的不同请求状态，如onStart、onEmpty、onCompletion等设置baseResp.dataState状态值，
     * 最后通过stateLiveData分发给UI层。
     *
     * @param block api的请求方法
     * @param stateLiveData 每个请求传入相应的LiveData，主要负责网络状态的监听
     */
    suspend fun <T : Any> executeReqWithFlow(
        block: suspend () -> BaseResult<T>,
        stateLiveData: StateLiveData<T>
    ) {
        var baseResp = BaseResult<T>()
        flow {
            val respResult = block.invoke()
            baseResp = respResult
            emit(respResult)
        }
            .flowOn(Dispatchers.IO)
            .onStart {
                baseResp.dataStatus = DataStatus.STATE_LOADING
                stateLiveData.postValue(baseResp)
            }
            .catch { exception ->
                run {
                    //非后台返回错误，捕获到的异常
                    baseResp.dataStatus = DataStatus.STATE_ERROR
                    baseResp.exception = DealException.handlerException(exception)
                    stateLiveData.postValue(baseResp)
                }
            }
            .collect {
                if (baseResp.code.equals("0")) {
                    baseResp.dataStatus = DataStatus.STATE_SUCCESS
                } else {
                    //服务器请求错误
                    baseResp.dataStatus = DataStatus.STATE_ERROR
                    baseResp.exception = ResultException(
                        baseResp.code.toString(),
                        baseResp.msg ?: baseResp.message ?: ""
                    )
                }
                stateLiveData.postValue(baseResp)
            }
    }

}