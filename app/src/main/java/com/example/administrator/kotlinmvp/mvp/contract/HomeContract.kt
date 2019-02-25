package com.example.administrator.kotlinmvp.mvp.contract

import com.example.administrator.kotlinmvp.base.IBaseView
import com.example.administrator.kotlinmvp.base.IPresenter
import com.example.administrator.kotlinmvp.mvp.modle.bean.HomeBean

/**
 * Created by Administrator on 2017/12/27 0027.
 */
interface HomeContract {
    interface View : IBaseView {
        /**
         * 设置第一次请求的数据
         */
        fun setHomeData(homeBean: HomeBean)


        /**
         * 设置加载更多的数据
         */
        fun setMoreData(itemList:ArrayList<HomeBean.Issue.Item>)

        /**
         * 显示错误信息
         */
        fun showError(msg: String,errorCode:Int)
    }

    interface Presenter : IPresenter<View> {

        /**
         * 获取首页精选数据
         */
        fun requestHomeData(num: Int)

        /**
         * 加载更多数据
         */
        fun LoadMoreData()
    }

}