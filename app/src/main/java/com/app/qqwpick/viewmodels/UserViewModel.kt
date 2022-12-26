package com.app.qqwpick.viewmodels

import androidx.lifecycle.viewModelScope
import com.app.qqwpick.base.BaseViewModel
import com.app.qqwpick.base.StateLiveData
import com.app.qqwpick.data.PickRepository
import com.app.qqwpick.data.home.PersonDetailBean
import com.app.qqwpick.data.home.VersionBean
import com.app.qqwpick.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(val pickRepo: PickRepository) :
    BaseViewModel() {

    var userBean = StateLiveData<PersonDetailBean>()
    var loginOut = StateLiveData<Any>()
    var versionBean = StateLiveData<VersionBean>()

    var todayThird = StateLiveData<Int>()
    var monthThird = StateLiveData<Int>()

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

    fun getTodayThird() {
        var startTime = DateUtils.getTodayZeroTime()
        var endTime = DateUtils.getTodayEndTime()
        viewModelScope.launch {
            pickRepo.thirdOrderFinish(startTime, endTime, todayThird)
        }
    }

    fun getMonthThird() {
        var startTime = DateUtils.getMonthZeroTime()
        var endTime = DateUtils.getMonthEndTime()
        viewModelScope.launch {
            pickRepo.thirdOrderFinish(startTime, endTime, monthThird)
        }
    }
}