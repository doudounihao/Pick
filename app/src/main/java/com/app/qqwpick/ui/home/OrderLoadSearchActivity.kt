package com.app.qqwpick.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.qqwpick.R
import com.app.qqwpick.adapter.OrderLoadListAdapter
import com.app.qqwpick.base.BaseVMActivity
import com.app.qqwpick.data.home.OrderLoadListBean
import com.app.qqwpick.databinding.ActivityOrderLoadSearchBinding
import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.util.ActivityUtil
import com.app.qqwpick.util.ORDER_FIRST_INDEX
import com.app.qqwpick.util.ORDER_PAGE_SIZE
import com.app.qqwpick.viewmodels.OrdeSendViewModel
import com.hjq.toast.ToastUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderLoadSearchActivity : BaseVMActivity<ActivityOrderLoadSearchBinding>() {

    private var mCurrentPosition = ORDER_FIRST_INDEX
    private var orderNo = ""
    private val viewModel: OrdeSendViewModel by viewModels()

    private val beanList by lazy {
        mutableListOf<OrderLoadListBean>()
    }

    private val mAdapter by lazy {
        OrderLoadListAdapter(beanList)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.ivBack.setOnClickListener {
            finish()
        }

        mBinding.tvSearch.setOnClickListener {
            if (mBinding.editSearchActivityContent.text.isNullOrEmpty()) {
                ToastUtils.show("请输入要搜索的内容")
            } else {
                getData()
            }
            hideSoftInput(mBinding.editSearchActivityContent.windowToken)
        }

        mBinding.common.recyclerData.layoutManager = LinearLayoutManager(this)
        mBinding.common.recyclerData.adapter = mAdapter

        mBinding.common.refreshLayout.setOnRefreshListener {
            mCurrentPosition = ORDER_FIRST_INDEX
            getData()
        }
        mAdapter.loadMoreModule.setOnLoadMoreListener {
            mCurrentPosition++
            getData()
        }

        mAdapter.addChildClickViewIds(R.id.tv_receive_address_tip)
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            var bean = mAdapter.getItem(position)
            when (view.id) {
                R.id.tv_receive_address_tip -> {
                    toMap(bean.receiverAddress, bean.lat, bean.lng)
                }
            }
        }

        mBinding.editSearchActivityContent.setOnEditorActionListener(OnEditorActionListener { textView: TextView?, actionId: Int, keyEvent: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (mBinding.editSearchActivityContent.text.isNullOrEmpty()) {
                    ToastUtils.show("请输入要搜索的内容")
                } else {
                    getData()
                }
                hideSoftInput(mBinding.editSearchActivityContent.windowToken)
                true
            }
            false
        })
    }

    private fun toMap(address: String, latitude: String, longtitude: String) {
        if (ActivityUtil.isInstallApk("com.autonavi.minimap")) {
            val intent = Intent.getIntent(
                "androidamap://navi?sourceApplication=&poiname=" + address + "&lat=" + latitude
                        + "&lon=" + longtitude + "&dev=0"
            )
            startActivity(intent)
        } else {
            ToastUtils.show("没有安装高德地图")
        }
    }

    private fun getData() {
        mAdapter.loadMoreModule.isEnableLoadMore = false
        orderNo = mBinding.editSearchActivityContent.text.toString()
        if (!orderNo.isNullOrEmpty()) {
            viewModel.getLoadOrderList(mCurrentPosition, ORDER_PAGE_SIZE, orderNo)
        }
    }

    override fun startObserver() {
        super.startObserver()
        viewModel.loadBeanList.observe(this, {
            when (it.dataStatus) {
                DataStatus.STATE_LOADING -> {
                    if (mCurrentPosition == ORDER_FIRST_INDEX && !mBinding.common.refreshLayout.isRefreshing) {
                        showLoading()
                    }
                }
                DataStatus.STATE_SUCCESS -> {
                    finishRefresh()
                    mAdapter.loadMoreModule.isEnableLoadMore = true
                    if (mCurrentPosition == ORDER_FIRST_INDEX) {
                        beanList.clear()
                        mAdapter.notifyDataSetChanged()
                        if (it.data?.list.isNullOrEmpty()) {
                            //如果网络错误了
                            mAdapter.setEmptyView(
                                getMsgEmptyDataView(
                                    mBinding.common.recyclerData
                                )
                            )
                            return@observe
                        }
                    }
                    beanList.addAll(it.data?.list!!)
                    mAdapter.notifyDataSetChanged()
                    //判断是否是最后一页
                    if (it.data?.list?.size ?: 0 < ORDER_PAGE_SIZE) {
                        mAdapter.loadMoreModule.loadMoreEnd()
                    } else {
                        mAdapter.loadMoreModule.loadMoreComplete()
                    }
                }
                DataStatus.STATE_ERROR -> {
                    finishRefresh()
                    if (mCurrentPosition == ORDER_FIRST_INDEX) {
                        beanList.clear()
                        mAdapter.notifyDataSetChanged()
                        //如果网络错误了
                        mAdapter.setEmptyView(
                            getMsgErrorView(
                                mBinding.common.recyclerData,
                                it.exception?.msg
                            )
                        )
                    }
                }
            }
        })
    }

    private fun finishRefresh() {
        if (mCurrentPosition == ORDER_FIRST_INDEX) {
            if (!mBinding.common.refreshLayout.isRefreshing) {
                dismissLoading()
            } else {
                mBinding.common.refreshLayout.finishRefresh()
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_order_load_search
    }
}