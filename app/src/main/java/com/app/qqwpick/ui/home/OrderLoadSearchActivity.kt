package com.app.qqwpick.ui.home

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

        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {

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
                        if (it.data?.list.isNullOrEmpty()) {
                            //必须要先把数组设置为空
                            mAdapter.setNewInstance(mutableListOf())
                            //如果网络错误了
                            mAdapter.setEmptyView(
                                getMsgEmptyDataView(
                                    mBinding.common.recyclerData
                                )
                            )
                            return@observe
                        }
                        beanList.clear()
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
                        //必须要先把数组设置为空
                        mAdapter.setNewInstance(mutableListOf())
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