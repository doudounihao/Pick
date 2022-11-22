package com.app.qqwpick.viewmodels

import androidx.lifecycle.viewModelScope
import com.app.qqwpick.base.BasePagingResult
import com.app.qqwpick.base.BaseViewModel
import com.app.qqwpick.base.StateLiveData
import com.app.qqwpick.data.PickRepository
import com.app.qqwpick.data.home.GrabDetailBean
import com.app.qqwpick.data.home.GrabListBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GrabViewModel @Inject constructor(val pickRepo: PickRepository) : BaseViewModel() {

    var grabNum = StateLiveData<Int>()

    fun getGrabNum() {
        viewModelScope.launch {
            pickRepo.getGrabNum(grabNum)
        }
    }

    var grabBeanList = StateLiveData<BasePagingResult<List<GrabListBean>>>()

    fun getGrabList(
        pageIndex: Int,
        pageSize: Int
    ) {
        viewModelScope.launch {
            pickRepo.getGrabList(pageIndex, pageSize, grabBeanList)
        }
    }

    var grabBean = StateLiveData<GrabDetailBean>()

    fun grabOrder(orderNo: String) {
        viewModelScope.launch {
            pickRepo.grabOrder(orderNo, grabBean)
        }
    }
}