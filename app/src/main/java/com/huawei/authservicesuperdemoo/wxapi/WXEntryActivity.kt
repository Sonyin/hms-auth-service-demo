package com.huawei.authservicesuperdemoo.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.huawei.authservicesuperdemoo.wechat.WeChatBaseHelper
import com.huawei.authservicesuperdemoo.wechat.utils.Logger
import com.huawei.authservicesuperdemoo.wechat.utils.MetaUtil
import com.kongqw.wechathelper.net.WeChatHelperRetrofitManager
import com.kongqw.wechathelper.net.response.AccessTokenInfo
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import io.reactivex.rxkotlin.subscribeBy

class WXEntryActivity : Activity(), IWXAPIEventHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WXAPIFactory.createWXAPI(this, MetaUtil.getWeChatAppId(applicationContext), true).handleIntent(intent, this)
        Logger.d("WXEntryActivity onCreate  WeChatAppId = ${MetaUtil.getWeChatAppId(applicationContext)}")
    }

    override fun onResume() {
        super.onResume()
        Logger.d("WXEntryActivity onCreate  onResume")
        finish()
    }

    override fun onResp(baseResp: BaseResp?) {
        Logger.d("WXEntryActivity onResp  baseResp = $baseResp")

        when (baseResp?.type) {
            // 授权登录
            ConstantsAPI.COMMAND_SENDAUTH -> {
                when (baseResp.errCode) {
                    // 用户同意了授权
                    BaseResp.ErrCode.ERR_OK -> {
                        val code = (baseResp as? SendAuth.Resp)?.code
                        var accessToken: AccessTokenInfo? = null
                        // 请求 Access Token
                        WeChatHelperRetrofitManager.getAccessToken(MetaUtil.getWeChatAppId(applicationContext), MetaUtil.getWeChatSecret(applicationContext), code)
                            .filter { accessTokenInfo ->
                                accessToken = accessTokenInfo
                                val isSuccess = accessTokenInfo.isSuccess()
                                if (!isSuccess) {
                                    // 请求AccessToken失败
                                    WeChatBaseHelper.mOnWeChatAuthLoginListener?.onWeChatAuthLoginError(accessTokenInfo.errcode, accessTokenInfo.errmsg)
                                    WeChatBaseHelper.mOnWeChatAuthLoginListener = null
                                }
                                return@filter isSuccess
                            }
                            // 请求用户信息
                            .flatMap { accessTokenInfo -> WeChatHelperRetrofitManager.getWeChatUserInfo(accessTokenInfo.access_token, accessTokenInfo.openid) }
                            .subscribeBy(
                                onNext = { weChatUserInfo ->
                                    if (weChatUserInfo.isSuccess()) {
                                        WeChatBaseHelper.mOnWeChatAuthLoginListener?.onWeChatAuthLoginSuccess(accessToken, weChatUserInfo)
                                    } else {
                                        WeChatBaseHelper.mOnWeChatAuthLoginListener?.onWeChatAuthLoginError(weChatUserInfo.errcode, weChatUserInfo.errmsg)
                                    }
                                },
                                onComplete = { WeChatBaseHelper.mOnWeChatAuthLoginListener = null },
                                onError = {
                                    WeChatBaseHelper.mOnWeChatAuthLoginListener?.onWeChatAuthLoginError(null, it.message)
                                    WeChatBaseHelper.mOnWeChatAuthLoginListener = null
                                    it.printStackTrace()
                                }
                            )
                    }

                    BaseResp.ErrCode.ERR_USER_CANCEL -> {
                        WeChatBaseHelper.mOnWeChatAuthLoginListener?.onWeChatAuthLoginCancel()
                        WeChatBaseHelper.mOnWeChatAuthLoginListener = null
                    }

                    BaseResp.ErrCode.ERR_AUTH_DENIED -> {
                        WeChatBaseHelper.mOnWeChatAuthLoginListener?.onWeChatAuthLoginAuthDenied()
                        WeChatBaseHelper.mOnWeChatAuthLoginListener = null
                    }
                    else -> {
                        WeChatBaseHelper.mOnWeChatAuthLoginListener?.onWeChatAuthLoginError(null, null)
                        WeChatBaseHelper.mOnWeChatAuthLoginListener = null
                    }
                }
            }
        }
    }

    override fun onReq(baseReq: BaseReq?) {
        Logger.d("WXEntryActivity onReq  baseReq = $baseReq")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Logger.d("WXEntryActivity requestCode = $requestCode  resultCode = $resultCode")
        super.onActivityResult(requestCode, resultCode, data)
    }
}