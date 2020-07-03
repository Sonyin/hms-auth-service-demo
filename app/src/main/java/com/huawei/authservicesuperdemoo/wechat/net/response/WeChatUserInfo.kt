package com.huawei.authservicesuperdemoo.wechat.net.response

import java.io.Serializable

class WeChatUserInfo : WeChatBaseResponseInfo(), Serializable {

    val openid: String? = null
    val nickname: String? = null
    val sex: String? = null
    val language: String? = null
    val city: String? = null
    val province: String? = null
    val country: String? = null
    val headimgurl: String? = null
    val privilege: Any? = null
    val unionid: String? = null

    fun isSuccess(): Boolean {

        if (null != errcode || !errmsg.isNullOrEmpty()) {
            return false
        }
        return true
    }

    override fun toString(): String {
        return "WeChatUserInfo(openid=$openid, nickname=$nickname, sex=$sex, language=$language, city=$city, province=$province, country=$country, headimgurl=$headimgurl, privilege=$privilege, unionid=$unionid)"
    }

}
