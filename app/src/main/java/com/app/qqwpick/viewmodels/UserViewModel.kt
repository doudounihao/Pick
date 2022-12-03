package com.app.qqwpick.viewmodels

import androidx.lifecycle.viewModelScope
import com.app.qqwpick.base.BaseViewModel
import com.app.qqwpick.base.StateLiveData
import com.app.qqwpick.data.PickRepository
import com.app.qqwpick.data.home.PersonDetailBean
import com.app.qqwpick.data.home.VersionBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(val pickRepo: PickRepository) :
    BaseViewModel() {

    var userBean = StateLiveData<PersonDetailBean>()
    var loginOut = StateLiveData<Any>()
    var versionBean = StateLiveData<VersionBean>()

    fun getPersonDetail() {
        viewModelScope.launch {
            pickRepo.getPersonDetail(userBean)
        }
    }

    fun loginOut() {
        viewModelScope.launch {
            pickRepo.loginOut(loginOut)
        }
    }

    fun getVersion() {
        viewModelScope.launch {
            pickRepo.getVersion(versionBean)
        }
    }
}