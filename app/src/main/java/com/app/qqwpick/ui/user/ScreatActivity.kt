package com.app.qqwpick.ui.user

import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import com.app.qqwpick.R
import com.app.qqwpick.base.BaseVMActivity
import com.app.qqwpick.databinding.ActivityScraetBinding
import com.app.qqwpick.util.SCREAT_URL
import com.just.agentweb.AgentWeb

class ScreatActivity : BaseVMActivity<ActivityScraetBinding>() {

    private lateinit var mAgentWeb: AgentWeb

    override fun initView(savedInstanceState: Bundle?) {
        webViewSetting()

        mBinding.titleBar.tvCenter.setText("隐私协议")

        mBinding.titleBar.tvLeft.setOnClickListener {
            finish()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_scraet
    }

    private fun webViewSetting() {
        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(
                (mBinding.webContainer as LinearLayout?)!!,
                ViewGroup.LayoutParams(-1, -1)
            )
            .useDefaultIndicator()
            .createAgentWeb()
            .ready()
            .go(SCREAT_URL)

        mAgentWeb.webCreator.webView.setOnTouchListener { v, event ->
            (v as WebView).requestDisallowInterceptTouchEvent(true)
            false
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onPause() {
        mAgentWeb.webLifeCycle.onPause()
        super.onPause()
    }

    override fun onResume() {
        mAgentWeb.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        mAgentWeb.webLifeCycle.onDestroy()
        super.onDestroy()
    }
}