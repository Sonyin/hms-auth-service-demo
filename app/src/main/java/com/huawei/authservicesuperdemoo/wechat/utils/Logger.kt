package com.huawei.authservicesuperdemoo.wechat.utils

import android.util.Log
import com.huawei.authservicesuperdemoo.wechat.WeChatHelper

object Logger {

    fun d(log: String?) {
        if (WeChatHelper.IS_LOGGABLE) {
            Log.d("Logger", log)
        }
    }
}