package com.huawei.authservicesuperdemoo.wechat

import android.content.Context
import com.huawei.authservicesuperdemoo.wechat.utils.Logger

class WeChatHelper private constructor(context: Context) : WeChatBaseHelper(context) {

    companion object {
        var IS_LOGGABLE: Boolean = false

        @Volatile
        private var instance: WeChatHelper? = null

        @JvmStatic
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: WeChatHelper(context).also { instance = it }
        }

    }

    fun init(isLoggable: Boolean): Boolean {

        IS_LOGGABLE = isLoggable

        val isInitWeChat = api.registerApp(mWeChatAppId)

        Logger.d("isInitWeChat = $isInitWeChat  mWeChatAppId = $mWeChatAppId")
        return true
    }

}