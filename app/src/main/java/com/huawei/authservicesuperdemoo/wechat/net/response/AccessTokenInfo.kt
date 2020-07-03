package com.kongqw.wechathelper.net.response

import com.huawei.authservicesuperdemoo.wechat.net.response.WeChatBaseResponseInfo
import java.io.Serializable

class AccessTokenInfo : WeChatBaseResponseInfo(),Serializable {

    val access_token: String? = null
    val expires_in: Long = 0
    val refresh_token: String? = null
    val openid: String? = null
    val scope: String? = null
    val unionid: String? = null

    fun isSuccess(): Boolean {
        if (!access_token.isNullOrEmpty()) {
            return true
        }
        if (null != errcode || !errmsg.isNullOrEmpty()) {
            return false
        }
        return false
    }

    override fun toString(): String {
        return "AccessTokenInfo(access_token=$access_token, expires_in=$expires_in, refresh_token=$refresh_token, openid=$openid, scope=$scope, unionid=$unionid, errcode=$errcode, errmsg=$errmsg)"
    }

}
