package com.huawei.authservicesuperdemoo.wechat

import android.content.Context
import com.huawei.authservicesuperdemoo.wechat.listener.OnWeChatAuthLoginListener
import com.huawei.authservicesuperdemoo.wechat.utils.Logger
import com.huawei.authservicesuperdemoo.wechat.utils.MetaUtil
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory

open class WeChatBaseHelper(context: Context) {

    protected val mWeChatAppId =  MetaUtil.getWeChatAppId(context)
    protected val api: IWXAPI = WXAPIFactory.createWXAPI(context, mWeChatAppId, true)

    companion object {

        private const val SCOPE = "snsapi_userinfo"
        private const val STATE = "lls_engzo_wechat_login"

        var mOnWeChatAuthLoginListener: OnWeChatAuthLoginListener? = null
    }

    fun authLogin(listener: OnWeChatAuthLoginListener) {
        mOnWeChatAuthLoginListener = listener
        val req = SendAuth.Req()
        req.scope = SCOPE
        req.state = STATE
        mOnWeChatAuthLoginListener?.onWeChatAuthLoginStart()
        val sendReq = api.sendReq(req)
        Logger.d("authLogin sendReq = $sendReq")
    }
}