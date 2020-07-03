package com.kongqw.wechathelper.net

import com.huawei.authservicesuperdemoo.wechat.net.WeChatHelperNetworkConfig
import com.huawei.authservicesuperdemoo.wechat.net.WeChatRetrofitMethods
import com.huawei.authservicesuperdemoo.wechat.net.converter.CustomGsonConverterFactory
import com.huawei.authservicesuperdemoo.wechat.net.response.WeChatUserInfo
import com.kongqw.wechathelper.net.response.AccessTokenInfo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

object WeChatHelperRetrofitManager {

    @Volatile
    private var instance: Retrofit? = null

    @JvmStatic
    fun getRetrofitInstance() = instance ?: synchronized(this) {
        instance ?: createRetrofit().also { instance = it }
    }

    private fun createRetrofit(): Retrofit {
        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder.retryOnConnectionFailure(true)
        okHttpBuilder.connectTimeout(WeChatHelperNetworkConfig.TIME_OUT_CONNECT, TimeUnit.SECONDS)
        okHttpBuilder.writeTimeout(WeChatHelperNetworkConfig.TIME_OUT_WRITE, TimeUnit.SECONDS)
        okHttpBuilder.readTimeout(WeChatHelperNetworkConfig.TIME_OUT_READ, TimeUnit.SECONDS)

        val retrofitBuilder = Retrofit.Builder()
        retrofitBuilder.baseUrl(WeChatHelperNetworkConfig.WECHAT_API_HOST)
        retrofitBuilder.client(okHttpBuilder.build())
        retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        retrofitBuilder.addConverterFactory(CustomGsonConverterFactory.create())
        return retrofitBuilder.build()
    }

    fun getAccessToken(appId: String?, secret: String?, code: String?): Observable<AccessTokenInfo> {
        val params = HashMap<String, String>()
        params["appid"] = appId ?: ""
        params["secret"] = secret ?: ""
        params["code"] = code ?: ""
        params["grant_type"] = "authorization_code"
        return Observable.just(params)
            .subscribeOn(Schedulers.io())
            .flatMap { getRetrofitInstance().create(WeChatRetrofitMethods::class.java).getAccessToken(it) }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getWeChatUserInfo(accessToken: String?, openId: String?): Observable<WeChatUserInfo> {
        val params = HashMap<String, String>()
        params["access_token"] = accessToken ?: ""
        params["openid"] = openId ?: ""
        return Observable.just(params)
            .subscribeOn(Schedulers.io())
            .flatMap { getRetrofitInstance().create(WeChatRetrofitMethods::class.java).getWeChatUserInfo(it) }
            .observeOn(AndroidSchedulers.mainThread())
    }

}