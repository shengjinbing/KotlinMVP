package com.example.administrator.kotlinmvp.mvp.modle

import com.example.administrator.kotlinmvp.mvp.modle.bean.HomeBean
import com.example.administrator.kotlinmvp.utils.RetrofitManage
import com.hazz.kotlinmvp.rx.scheduler.IoMainScheduler
import com.hazz.kotlinmvp.rx.scheduler.SchedulerUtils
import io.reactivex.Observable

/**
 * Created by Administrator on 2017/12/27 0027.
 */
class HomeModle {

    fun requestHomeData(num:Int): Observable<HomeBean> {
        return RetrofitManage.server.getFirstHomeData(num)
                .compose(SchedulerUtils.ioToMain())
    }

    /**
     * 加载更多
     */
    fun loadMoreData(url:String):Observable<HomeBean>{

        return RetrofitManage.server.getMoreHomeData(url)
                .compose(SchedulerUtils.ioToMain())
    }

}