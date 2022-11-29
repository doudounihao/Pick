package com.app.qqwpick.ui.user

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.qqwpick.R
import com.app.qqwpick.adapter.FinishOrderListAdapter
import com.app.qqwpick.base.BaseVMActivity
import com.app.qqwpick.data.home.OrderListBean
import com.app.qqwpick.databinding.ActivityFinishOrderBinding
import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.ui.home.OrderDetailActivity
import com.app.qqwpick.util.DateUtils
import com.app.qqwpick.util.ORDER_FIRST_INDEX
import com.app.qqwpick.util.ORDER_PAGE_SIZE
import com.app.qqwpick.viewmodels.OrdeSendViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FinishOrderActivity : BaseVMActivity<ActivityFinishOrderBinding>() {

    private var mCurrentPosition = ORDER_FIRST_INDEX
    private var startTime = ""
    private var endTime = ""
    private var type = ""
    private val viewModel: OrdeSendViewModel by viewModels()
    private val beanList by lazy {
        mutableListOf<OrderListBean>()
    }

    private val mAdapter by lazy {
        FinishOrderListAdapter(beanList)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.title.tvLeft.setOnClickListener {
            finish()
        }

        mBinding.recyclerData.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerData.adapter = mAdapter

        mBinding.refreshLayout.setOnRefreshListener {
            mCurrentPosition = ORDER_FIRST_INDEX
            getData()
        }
        mAdapter.loadMoreModule.setOnLoadMoreListener {
            mCurrentPosition++
            getData()
        }

        mAdapter.setOnItemClickListener { adapter, view, position ->
            var intent = Intent(this, OrderDetailActivity::class.java)
            intent.putExtra("bean", mAdapter.getItem(position))
            startActivity(intent)
        }
    }

    override fun initData() {
        super.initData()
        type = intent.getStringExtra("type").toString()
        if (type.equals("today")) {
            startTime = DateUtils.getTodayZeroTime()
            endTime = DateUtils.getTodayEndTime()
            mBinding.title.tvCenter.text = "今日配送"
        } else {
            startTime = DateUtils.getMonthZeroTime()
            endTime = DateUtils.getMonthEndTime()
            mBinding.title.tvCenter.text = "本月配送"
        }
        getData()
    }

    private fun getData() {
        mAdapter.loadMoreModule.isEnableLoadMore = false
        viewModel.getFinishOrderList(mCurrentPosition, ORDER_PAGE_SIZE, startTime, endTime, "5")
    }

    override fun startObserver() {
        super.startObserver()
        viewModel.finishBeanList.observe(this, {
            when (it.dataStatus) {
                DataStatus.STATE_LOADING -> {
                    if (mCurrentPosition == ORDER_FIRST_INDEX && !mBinding.refreshLayout.isRefreshing) {
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
                                    mBinding.recyclerData
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
                                mBinding.recyclerData,
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
            if (!mBinding.refreshLayout.isRefreshing) {
                dismissLoading()
            } else {
                mBinding.refreshLayout.finishRefresh()
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_finish_order
    }
}