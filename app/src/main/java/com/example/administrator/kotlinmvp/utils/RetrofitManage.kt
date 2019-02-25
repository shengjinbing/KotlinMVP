package com.example.administrator.kotlinmvp.utils

import com.example.administrator.kotlinmvp.api.ApiService
import com.example.administrator.kotlinmvp.api.UriConstant
import com.example.administrator.kotlinmvp.base.MyApplication
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by Administrator on 2018/3/16 0016.
 */
object RetrofitManage{

    private var mRetrofit : Retrofit? = null
    private val token: String by Preference("token","")
    private var client: OkHttpClient? = null


     val server : ApiService by lazy {
        getRetrofit()!!.create(ApiService :: class.java)
    }

    private fun getRetrofit(): Retrofit? {
       if (mRetrofit == null){
           synchronized(RetrofitManage :: class.java){
               if (mRetrofit == null){
                   //添加一个log拦截器,打印所有的log
                   val httpLoggingInterceptor = HttpLoggingInterceptor()
                   //可以设置请求过滤的水平,body,basic,headers
                   httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

                   //设置 请求的缓存的大小跟位置
                   val cacheFile = File(MyApplication.context.cacheDir, "cache")
                   val cache = Cache(cacheFile, 1024 * 1024 * 50) //50Mb 缓存的大小

                   client = OkHttpClient.Builder()
                           .addInterceptor(addQueryParameterInterceptor())  //参数添.addInterceptor(addHeaderInterceptor()) // token过滤
                   //        .addInterceptor(addCacheInterceptor())
                           .addInterceptor(addHeaderInterceptor())
                           .addInterceptor(httpLoggingInterceptor) //日志,所有的请求响应度看到
                           .cache(cache)  //添加缓存
                           .connectTimeout(60L, TimeUnit.SECONDS)
                           .readTimeout(60L, TimeUnit.SECONDS)
                           .writeTimeout(60L, TimeUnit.SECONDS)
                           .build()

                   mRetrofit = Retrofit.Builder()
                           .baseUrl(UriConstant.BASE_URL)
                           .client(client)
                           .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                           .addConverterFactory(GsonConverterFactory.create())
                           .build()
               }
           }
       }
        return mRetrofit;
    }

    /**
     * 设置公共参数
     * lambda表达式，只适合有一个抽象方法的
     */
    private fun addQueryParameterInterceptor(): Interceptor {
      return Interceptor {chain ->
          val originalRequest = chain.request()
          val modifiedUrl = originalRequest.url().newBuilder()
                  .addQueryParameter("phoneSystem", "")
                  .addQueryParameter("phoneModel", "")
                  .build()
          val request = originalRequest.newBuilder().url(modifiedUrl).build()
          chain.proceed(request)
      }
    }



    /**
     * 设置头
     */
    private fun addHeaderInterceptor(): Interceptor {
        return Interceptor {chain ->
            val originalRequest = chain.request()
            val modifiedUrl = originalRequest.newBuilder()
                    .header("token",token)
                    .build()
            val request = originalRequest.newBuilder().build()
            chain.proceed(request)
        }
    }

    /**
     * 设置缓存
     */
    private fun addCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (!NetworkUtil.isNetworkAvailable(MyApplication.context)) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build()
            }
            val response = chain.proceed(request)
            if (NetworkUtil.isNetworkAvailable(MyApplication.context)) {
                val maxAge = 0
                // 有网络时 设置缓存超时时间0个小时 ,意思就是不读取缓存数据,只对get有用,post没有缓冲
                response.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .removeHeader("Retrofit")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .build()
            } else {
                // 无网络时，设置超时为4周  只对get有用,post没有缓冲
                val maxStale = 60 * 60 * 24 * 28
                response.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .removeHeader("nyn")
                        .build()
            }
            response
        }
    }
}