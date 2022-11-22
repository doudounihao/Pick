package com.app.qqwpick.viewmodels

import androidx.lifecycle.viewModelScope
import com.app.qqwpick.base.BaseViewModel
import com.app.qqwpick.base.StateLiveData
import com.app.qqwpick.data.PickRepository
import com.app.qqwpick.data.home.AuthLoginBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreListViewModel @Inject constructor(val pickRepo: PickRepository) : BaseViewModel() {

    var authLoginBean = StateLiveData<AuthLoginBean>()

    fun authLogin(jonNumber: String, storeId: String) {
        viewModelScope.launch {
            pickRepo.authLogin(jonNumber, storeId, authLoginBean)
        }
    }
}