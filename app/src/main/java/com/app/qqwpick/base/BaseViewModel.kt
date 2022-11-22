package com.app.qqwpick.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

open class BaseViewModel() : ViewModel() {

    var isDialogShow = MutableLiveData<Boolean>()


    /**
     * 用来控制dialog的
     */
    fun dialogState(activity: BaseActivity) {
        val weakActivity = WeakReference<BaseActivity>(activity)
        isDialogShow.observe(activity, Observer {
            if (it) weakActivity.get()?.showLoading() else weakActivity.get()?.dismissLoading()
        })
    }


    /**
     * 用来控制dialog的
     */
    fun dialogState(fragment: BaseFragment) {
        val weakFragment = WeakReference<BaseFragment>(fragment)
        isDialogShow.observe(fragment, Observer {
            if (it) weakFragment.get()?.showLoading() else weakFragment.get()?.dismissLoading()
        })
    }

    suspend fun toast(message: String?) {
        withContext(Dispatchers.Main) {
            if (message != null) {
                ToastUtils.show(message)
            }
        }
    }
}