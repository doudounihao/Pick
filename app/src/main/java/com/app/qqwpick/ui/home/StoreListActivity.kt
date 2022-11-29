package com.app.qqwpick.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.qqwpick.R
import com.app.qqwpick.adapter.StoreListAdapter
import com.app.qqwpick.base.BaseResult
import com.app.qqwpick.base.BaseVMActivity
import com.app.qqwpick.data.home.AuthLoginBean
import com.app.qqwpick.data.user.StoreBean
import com.app.qqwpick.databinding.ActivityStoreListBinding
import com.app.qqwpick.net.DataStatus
import com.app.qqwpick.ui.user.LoginActivity
import com.app.qqwpick.util.*
import com.app.qqwpick.viewmodels.StoreListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreListActivity : BaseVMActivity<ActivityStoreListBinding>() {

    val viewModel: StoreListViewModel by viewModels()
    lateinit var mAdapter: StoreListAdapter
    lateinit var mList: MutableList<StoreBean>
    lateinit var mBean: StoreBean

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_store_list
    }

    override fun initData() {
        super.initData()
        val a: Array<StoreBean> = intent.getSerializableExtra("list") as Array<StoreBean>
        mList = a.toCollection(ArrayList())
        mBinding.title.tvCenter.setText("选择门店")
        mBinding.title.tvLeft.setOnClickListener {
            finish()
        }
        mBinding.tvSure.setOnClickListener {
            if (it.isSelected) {
                viewModel.authLogin(SpUtils.getString(USER_JOB_NUMBER) ?: "", mBean.id.toString())
            }
        }
        initAdapter()
    }

    private fun initAdapter() {
        mAdapter = StoreListAdapter(mList)
        mBinding.recyclerView.layoutManager = LinearLayoutManager(baseContext)
        mBinding.recyclerView.adapter = mAdapter

        mAdapter.setOnItemClickListener { adapter, view, position ->
            mAdapter.data.map { it.isSelect = false }
            mAdapter.data[position].isSelect = true
            mBean = mAdapter.getItem(position)
            SpUtils.put(STORE_ID, mAdapter.data[position].id.toString())
            SpUtils.put(STORE_NAME, mAdapter.data[position].name.toString())
            SpUtils.put(STORE_NO, mAdapter.data[position].storeNo.toString())
            SpUtils.put(STORE_TEL, mAdapter.data[position].contactTel.toString())
            mAdapter.notifyDataSetChanged()
            mBinding.tvSure.isSelected = true
        }
    }

    override fun startObserver() {
        super.startObserver()
        viewModel.authLoginBean.observe(this, {
            handleResult(it)
        })
    }

    fun handleResult(it: BaseResult<AuthLoginBean>) {
        when (it.dataStatus) {
            DataStatus.STATE_LOADING ->
                showLoading()
            DataStatus.STATE_ERROR -> {
                dismissLoading()
                toast(it.exception!!.msg)
            }
            DataStatus.STATE_SUCCESS -> {
                dismissLoading()
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("bean", it.data)
                startActivity(intent)
            }
        }
    }
}