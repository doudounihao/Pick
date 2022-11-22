package com.app.qqwpick.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import com.app.qqwpick.MainApplication

object ActivityUtil {

    var mActivities = arrayListOf<Activity>()

    fun addActivity(activity: Activity) {
        if (!mActivities.contains(activity)) {
            mActivities.add(activity)
        }
    }

    fun removeActivity(activity: Activity) {
        if (mActivities.contains(activity)) {
            mActivities.remove(activity)
        }
    }

    fun getStackTopAct(): Activity {
        if (mActivities.isEmpty()) {
            throw  IllegalArgumentException("can't get Activity ")
        }
        return mActivities[mActivities.size - 1]
    }

    fun finishAllActivity() {
        mActivities.forEach {
            if (!it.isFinishing) {
                it.finish()
            }
        }
    }

    fun isInstallApk(packageName: String): Boolean {
        val pm = MainApplication.getInstance().packageManager
        var installed = false
        installed = try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
        return installed
    }

}