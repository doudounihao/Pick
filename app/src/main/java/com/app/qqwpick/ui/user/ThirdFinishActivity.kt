package com.app.qqwpick.ui.user

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.qqwpick.R
import com.app.qqwpick.adapter.ThirdFinishOrderListAdapter
import com.app.qqwpick.base.BaseVMActivity
import com.app.qqwpick.data.home.OrderThirdListBean
import com.app.qqwpick.databinding.ActivityFinishOrderBinding
import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.ui.home.ThirdDetailActivity
import com.app.qqwpick.util.*
import com.app.qqwpick.util.THIRD_SEND_STATUS
import com.app.qqwpick.viewmodels.OrdeSendViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThirdFinishActivity : BaseVMActivity<ActivityFinishOrderBinding>() {

    private var mCurrentPosition = ORDER_FIRST_INDEX
    private var startTime = ""
    private var endTime = ""
    private var type = ""
    private val viewModel: OrdeSendViewModel by viewModels()
    private val beanList by lazy {
        mutableListOf<OrderThirdListBean>()
    }

    private val mAdapter by lazy {
        ThirdFinishOrderListAdapter(beanList)
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
            var intent = Intent(this, ThirdDetailActivity::class.java)
            intent.putExtra("orderNo", mAdapter.getItem(position).orderNo)
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
        viewModel.getThirdBeanList(
            mCurrentPosition,
            ORDER_PAGE_SIZE,
            "",
            THIRD_FINISH_STATUS,
            "",
            startTime,
            endTime
        )
    }

    override fun startObserver() {
        super.startObserver()
        viewModel.thirdBeanList.observe(this, {
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
                        beanList.clear()
                        mAdapter.notifyDataSetChanged()
                        if (it.data?.list.isNullOrEmpty()) {
                            //如果网络错误了
                            mAdapter.setEmptyView(
                                getMsgEmptyDataView(
                                    mBinding.recyclerData
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