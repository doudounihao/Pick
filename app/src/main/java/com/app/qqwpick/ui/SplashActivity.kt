package com.app.qqwpick.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.qqwpick.R
import com.app.qqwpick.ui.home.HomeActivity
import com.app.qqwpick.ui.user.AccountActivity
import com.app.qqwpick.util.ROLE_GRAB
import com.app.qqwpick.util.ROLE_SEND
import com.app.qqwpick.util.ROLE_TYPE
import com.app.qqwpick.util.SpUtils
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //强制竖屏
        setContentView(R.layout.activity_splash)
        var per = arrayOf(
            Permission.ACCESS_FINE_LOCATION,
            Permission.ACCESS_COARSE_LOCATION,
            Permission.READ_EXTERNAL_STORAGE,
            Permission.WRITE_EXTERNAL_STORAGE
        )
        if (XXPermissions.isGranted(this, per)) {
            doThings()
        } else {
            XXPermissions.with(this)
                .permission(per)
                .request { permissions, all ->
                    doThings()
                }
        }
    }

    private fun doThings() {
        if (SpUtils.getString(ROLE_TYPE).isNullOrEmpty()) {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            finish()
        } else if (ROLE_SEND.equals(SpUtils.getString(ROLE_TYPE))) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else if (ROLE_GRAB.equals(SpUtils.getString(ROLE_TYPE))) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}