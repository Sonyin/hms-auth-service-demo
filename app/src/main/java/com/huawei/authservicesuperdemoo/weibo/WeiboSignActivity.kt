package com.huawei.authservicesuperdemoo.weibo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.WeiboAuthProvider
import com.huawei.authservicesuperdemoo.R
import com.huawei.authservicesuperdemoo.SINA_APP_KEY
import com.huawei.authservicesuperdemoo.SINA_REDIRECT_URL
import com.huawei.authservicesuperdemoo.SINA_SCOPE
import com.sina.weibo.sdk.WbSdk
import com.sina.weibo.sdk.auth.*
import com.sina.weibo.sdk.auth.sso.SsoHandler


class WeiboSignActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private lateinit var loginWithWeiboBtn : AppCompatButton

    private var uId: String? = null

    private var mSsoHandler: SsoHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weibo)

        loginWithWeiboBtn = findViewById(R.id.loginWithWeiboBtn)

        WbSdk.install(this, AuthInfo(this, SINA_APP_KEY, SINA_REDIRECT_URL, SINA_SCOPE))
        mSsoHandler = SsoHandler(this@WeiboSignActivity)

        initView()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, WeiboSignActivity::class.java))
            }
    }

    private fun initView(){

        loginWithWeiboBtn.setOnClickListener{
            mSsoHandler!!.authorizeWeb(SelfWbAuthListener(this))
        }

    }

    private class SelfWbAuthListener(activity: Activity) : WbAuthListener {

        private val mContext = activity

        override fun onSuccess(token: Oauth2AccessToken) {

            mContext.runOnUiThread(Runnable {
                if (token.isSessionValid) {

                    // save Token to SharedPreferences
                    AccessTokenKeeper.writeAccessToken(mContext, token)
                    Toast.makeText(mContext, R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show()

                    val credential = WeiboAuthProvider.credentialWithToken(token.token, token.uid)
                    AGConnectAuth.getInstance().signIn(credential)
                        .addOnSuccessListener { signInResult -> // onSuccess

                            val user = signInResult.user
                            Toast.makeText(mContext, user.uid, Toast.LENGTH_LONG).show()

                        }
                        .addOnFailureListener {
                            // onFail
                        }

                }
            })
        }

        override fun cancel() {
            Toast.makeText(mContext, R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show()
        }

        override fun onFailure(errorMessage: WbConnectErrorMessage) {
            Toast.makeText(mContext, errorMessage.errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        mSsoHandler?.authorizeCallBack(requestCode, resultCode, data)
    }
}
