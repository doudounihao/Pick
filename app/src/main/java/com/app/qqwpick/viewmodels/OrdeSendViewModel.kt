package com.app.qqwpick.viewmodels

import androidx.lifecycle.viewModelScope
import com.app.qqwpick.base.BasePagingResult
import com.app.qqwpick.base.BaseViewModel
import com.app.qqwpick.base.StateLiveData
import com.app.qqwpick.data.PickRepository
import com.app.qqwpick.data.home.OrderListBean
import com.app.qqwpick.data.home.OrderLoadListBean
import com.app.qqwpick.data.home.OrderThirdListBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdeSendViewModel @Inject constructor(val pickRepo: PickRepository) :
    BaseViewModel() {

    var beanList = StateLiveData<BasePagingResult<List<OrderListBean>>>()

    fun getBeanList(
        pageIndex: Int,
        pageSize: Int,
        orderNo: String,
        outboundStatus: String
    ) {
        viewModelScope.launch {
            pickRepo.getSendOrderList(pageIndex, pageSize, orderNo, outboundStatus, beanList)
        }
    }

    var loadBeanList = StateLiveData<BasePagingResult<List<OrderLoadListBean>>>()

    fun getLoadOrderList(
        pageIndex: Int,
        pageSize: Int,
        orderNo: String
    ) {
        viewModelScope.launch {
            pickRepo.getLoadOrderList(pageIndex, pageSize, orderNo, loadBeanList)
        }
    }

    var finishBeanList = StateLiveData<BasePagingResult<List<OrderListBean>>>()

    fun getFinishOrderList(
        pageIndex: Int,
        pageSize: Int,
        startTime: String,
        endTime: String,
        outboundStatus: String
    ) {
        viewModelScope.launch {
            pickRepo.getFinishOrderList(
                pageIndex,
                pageSize,
                startTime,
                endTime,
                outboundStatus,
                finishBeanList
            )
        }
    }

    var startResult = StateLiveData<Boolean>()

    fun startDelivery(orderNo: String) {
        viewModelScope.launch {
            pickRepo.startDelivery(orderNo, startResult)
        }
    }

    var completeResult = StateLiveData<Any>()

    fun completeDelivery(orderNo: String) {
        viewModelScope.launch {
            pickRepo.completeDelivery(orderNo, completeResult)
        }
    }


    var thirdBeanList = StateLiveData<BasePagingResult<List<OrderThirdListBean>>>()

    fun getThirdBeanList(
        pageIndex: Int,
        pageSize: Int,
        orderNo: String,
        channelOrderNo: String
    ) {
        viewModelScope.launch {
            pickRepo.getThirdOrderList(pageIndex, pageSize, orderNo, channelOrderNo, thirdBeanList)
        }
    }
}