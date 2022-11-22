package com.app.qqwpick.viewmodels

import androidx.lifecycle.viewModelScope
import com.app.qqwpick.base.BasePagingResult
import com.app.qqwpick.base.BaseViewModel
import com.app.qqwpick.base.StateLiveData
import com.app.qqwpick.data.PickRepository
import com.app.qqwpick.data.home.OrderListBean
import com.app.qqwpick.data.home.OrderLoadListBean
import com.app.qqwpick.data.user.StoreBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(val pickRepo: PickRepository) :
    BaseViewModel() {

    var mStoreBean = StateLiveData<MutableList<StoreBean>>()

    fun verifyAccount(id: String) {
        viewModelScope.launch {
            pickRepo.verifyAccount(id, mStoreBean)
        }
    }
}