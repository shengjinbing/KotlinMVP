package com.example.administrator.kotlinmvp.mvp.modle

import com.example.administrator.kotlinmvp.mvp.modle.bean.HomeBean
import com.example.administrator.kotlinmvp.utils.RetrofitManage
import com.hazz.kotlinmvp.rx.scheduler.SchedulerUtils
import io.reactivex.Observable

/**
 * Created by Administrator on 2018/3/27 0027.
 */
class VideoDetailModle {
    fun requestRelatedData(id: Long): Observable<HomeBean.Issue> {

        return RetrofitManage.server.getRelatedData(id)
                .compose(SchedulerUtils.ioToMain())
    }
}