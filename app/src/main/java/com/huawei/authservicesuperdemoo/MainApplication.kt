package com.huawei.authservicesuperdemoo

import android.app.Application
import com.huawei.authservicesuperdemoo.wechat.WeChatHelper
import com.huawei.hms.api.HuaweiMobileServicesUtil

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        WeChatHelper.getInstance(applicationContext).init(BuildConfig.DEBUG)
        HuaweiMobileServicesUtil.setApplication(this)

    }
}