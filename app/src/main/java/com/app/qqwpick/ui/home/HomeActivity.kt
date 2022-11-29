package com.app.qqwpick.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.fragment.NavHostFragment
import com.app.qqwpick.R
import com.app.qqwpick.base.BaseVMActivity
import com.app.qqwpick.databinding.ActivityHomeBinding
import com.app.qqwpick.ui.user.UserFragment
import com.app.qqwpick.util.FixFragmentNavigator
import com.app.qqwpick.util.MessageEvent
import com.app.qqwpick.util.MessageType
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import update.UpdateAppUtils


@AndroidEntryPoint
class HomeActivity : BaseVMActivity<ActivityHomeBinding>() {

    private var navController: NavController? = null

    override fun initView(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)//注册，重复注册会导致崩溃
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)//解绑
    }

    //接收消息
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(event: MessageEvent) {
        when (event.type) {
            MessageType.ShowGrab -> {
                val badge = mBinding.bottomNav.getOrCreateBadge(R.id.navigation_grab)
                badge.number = event.getInt()
                badge.backgroundColor = Color.RED
            }
        }
    }

    private fun setupBottomNavigationBar() {
        var navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_nav) as NavHostFragment
        navController = navHostFragment.navController
        //创建自定义的Fragment导航器
        //创建自定义的Fragment导航器
        val fragmentNavigator =
            FixFragmentNavigator(this, navHostFragment.childFragmentManager, navHostFragment.id)
        //获取导航器提供者
        val provider = navController!!.navigatorProvider
        //把自定义的Fragment导航器添加进去
        provider.addNavigator(fragmentNavigator)
        //手动创建导航图
        val navGraph = initNavGraph(provider, fragmentNavigator)
        //设置导航图
        navController!!.setGraph(navGraph)

//        NavigationUI.setupWithNavController(mBinding.bottomNav, navController)
        //底部导航设置点击事件
        //底部导航设置点击事件
        mBinding.bottomNav.setOnNavigationItemSelectedListener { item ->
            navController!!.navigate(item.getItemId())
            true
        }
        mBinding.bottomNav.setItemIconTintList(null)
        mBinding.bottomNav.itemTextAppearanceActive = R.style.bottom_selected_text;
        mBinding.bottomNav.itemTextAppearanceInactive = R.style.bottom_normal_text;

        val bottombarView: View = mBinding.bottomNav.getChildAt(0)
        bottombarView.findViewById<View>(R.id.navigation_home).setOnLongClickListener { v -> true }

        val bottombarView1: View = mBinding.bottomNav.getChildAt(0)
        bottombarView1.findViewById<View>(R.id.navigation_grab).setOnLongClickListener { v -> true }

        val bottombarView2: View = mBinding.bottomNav.getChildAt(0)
        bottombarView2.findViewById<View>(R.id.navigation_mine).setOnLongClickListener { v -> true }
//        val badge = mBinding.bottomNav.getOrCreateBadge(R.id.navigation_grab)
//        badge.number = 10
//        badge.backgroundColor = Color.RED
    }

    //手动创建导航图，把3个目的地添加进来
    private fun initNavGraph(
        provider: NavigatorProvider,
        fragmentNavigator: FixFragmentNavigator
    ): NavGraph {
        val navGraph = NavGraph(NavGraphNavigator(provider))

        //用自定义的导航器来创建目的地
        val destination1 = fragmentNavigator.createDestination()
        destination1.id = R.id.navigation_home
        destination1.className = HomeFragment::class.java.canonicalName
        destination1.label = resources.getString(R.string.HomeFragment)
        navGraph.addDestination(destination1)

        val destination2 = fragmentNavigator.createDestination()
        destination2.id = R.id.navigation_grab
        destination2.className = GrabFragment::class.java.canonicalName
        destination2.label = resources.getString(R.string.GrabFragment)
        navGraph.addDestination(destination2)

        val destination3 = fragmentNavigator.createDestination()
        destination3.id = R.id.navigation_mine
        destination3.className = UserFragment::class.java.canonicalName
        destination3.label = resources.getString(R.string.UserFragment)
        navGraph.addDestination(destination3)

        navGraph.startDestination = R.id.navigation_home
        return navGraph
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    override fun onBackPressed() {
        val currentId: Int = navController?.currentDestination!!.getId()
        val startId: Int = navController?.graph!!.getStartDestination()
        //如果当前目的地不是HomeFragment，则先回到HomeFragment
        if (currentId != startId) {
            mBinding.bottomNav.selectedItemId = startId
        } else {
            finish()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_home
    }

    override fun initData() {
        super.initData()
    }

    private fun updateApp(apkUrl: String, updateContent: String, updateTitle: String) {
        UpdateAppUtils
            .getInstance()
            .apkUrl(apkUrl)
            .updateTitle(updateTitle)
            .updateContent(updateContent)
            .update()
    }

}