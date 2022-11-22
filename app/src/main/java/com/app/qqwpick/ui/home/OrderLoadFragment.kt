package com.app.qqwpick.ui.home

import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.qqwpick.R
import com.app.qqwpick.adapter.OrderLoadListAdapter
import com.app.qqwpick.base.BaseVMFragment
import com.app.qqwpick.data.home.OrderListBean
import com.app.qqwpick.data.home.OrderLoadListBean
import com.app.qqwpick.databinding.FragmentOrderLoadBinding
import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.util.ORDER_FIRST_INDEX
import com.app.qqwpick.util.ORDER_PAGE_SIZE
import com.app.qqwpick.viewmodels.OrdeSendViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderLoadFragment : BaseVMFragment<FragmentOrderLoadBinding>() {

    private var mCurrentPosition = ORDER_FIRST_INDEX
    private var orderNo = ""
    private val viewModel: OrdeSendViewModel by viewModels()
    private val beanList by lazy {
        mutableListOf<OrderLoadListBean>()
    }

    private val mAdapter by lazy {
        OrderLoadListAdapter(beanList)
    }

    override fun onResume() {
        super.onResume()
        (parentFragment as OrderFragment).setType(2)
    }

    override fun initView(view: View) {
        mBinding.common.recyclerData.layoutManager = LinearLayoutManager(requireContext())
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
    }

    override fun initData() {
        super.initData()
        getData()
    }

    private fun getData() {
        mAdapter.loadMoreModule.isEnableLoadMore = false
        viewModel.getLoadOrderList(mCurrentPosition, ORDER_PAGE_SIZE, orderNo)
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
        return R.layout.fragment_order_load
    }
}