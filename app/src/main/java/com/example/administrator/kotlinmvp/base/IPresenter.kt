package com.example.administrator.kotlinmvp.base

/**
 * Created by Administrator on 2017/12/27 0027.
 */
interface IPresenter<in V : IBaseView> {
    fun attachView(mRootView: V)

    fun detachView()
}