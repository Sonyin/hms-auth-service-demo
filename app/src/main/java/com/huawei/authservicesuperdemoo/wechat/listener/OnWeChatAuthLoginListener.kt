package com.huawei.authservicesuperdemoo.wechat.listener

import com.huawei.authservicesuperdemoo.wechat.net.response.WeChatUserInfo
import com.kongqw.wechathelper.net.response.AccessTokenInfo

interface OnWeChatAuthLoginListener {

    fun onWeChatAuthLoginStart()
    fun onWeChatAuthLoginSuccess(accessTokenInfo: AccessTokenInfo?, weChatUserInfo: WeChatUserInfo?)
    fun onWeChatAuthLoginCancel()
    fun onWeChatAuthLoginAuthDenied()
    fun onWeChatAuthLoginError(errorCode: Int?, errorMessage: String?)
}