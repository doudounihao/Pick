package com.app.qqwpick.viewmodels

import androidx.lifecycle.viewModelScope
import com.app.qqwpick.base.BaseViewModel
import com.app.qqwpick.base.StateLiveData
import com.app.qqwpick.data.PickRepository
import com.app.qqwpick.data.home.AuthLoginBean
import com.app.qqwpick.data.user.UserBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val pickRepo: PickRepository) :
    BaseViewModel() {

    var userBean = StateLiveData<UserBean>()
    var userCode = StateLiveData<String>()

    fun loginByCode(authLoginBean: AuthLoginBean, userCode: String) {
        viewModelScope.launch {
            pickRepo.loginByCode(authLoginBean, userCode, userBean)
        }
    }

    fun getPhoneCode(authLoginBean: AuthLoginBean) {
        viewModelScope.launch {
            pickRepo.getPhoneCode(authLoginBean, userCode)
        }
    }
}