package com.huawei.authservicesuperdemoo.wechat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.WeixinAuthProvider
import com.huawei.authservicesuperdemoo.R
import com.huawei.authservicesuperdemoo.wechat.listener.OnWeChatAuthLoginListener
import com.huawei.authservicesuperdemoo.wechat.net.response.WeChatUserInfo
import com.huawei.authservicesuperdemoo.wechat.utils.Logger
import com.kongqw.wechathelper.net.response.AccessTokenInfo


class WeChatSignActivity : AppCompatActivity(), OnWeChatAuthLoginListener {

    private val TAG = javaClass.simpleName

    private lateinit var loginWithWeChatBtn : AppCompatButton

    private var uId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wechat)

        loginWithWeChatBtn = findViewById(R.id.loginWithWeChatBtn)

        initView()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, WeChatSignActivity::class.java))
            }
    }

    private fun initView(){

        loginWithWeChatBtn.setOnClickListener{
            loginWithWeChat()
        }

    }

    private fun loginWithWeChat() {
        WeChatHelper.getInstance(applicationContext).authLogin(this)
    }

    override fun onWeChatAuthLoginStart() {
        Toast.makeText(applicationContext, "Start applying for authorized login", Toast.LENGTH_SHORT).show()
    }

    override fun onWeChatAuthLoginSuccess(accessTokenInfo: AccessTokenInfo?, weChatUserInfo: WeChatUserInfo?) {
        Logger.d("accessTokenInfo = $accessTokenInfo")
        Logger.d("weChatUserInfo = $weChatUserInfo")

        val credential = WeixinAuthProvider.credentialWithToken(accessTokenInfo?.access_token, accessTokenInfo?.openid)
        AGConnectAuth.getInstance().signIn(credential)
            .addOnSuccessListener { signInResult -> // onSuccess

                val user = signInResult.user
                Toast.makeText(this, user.uid, Toast.LENGTH_LONG).show()
                uId = user.uid

            }
            .addOnFailureListener {
                // onFail
            }

        Toast.makeText(applicationContext, "Successful login : ${weChatUserInfo?.nickname}  access_token = ${accessTokenInfo?.access_token}", Toast.LENGTH_SHORT).show()
    }

    override fun onWeChatAuthLoginCancel() {
        Toast.makeText(applicationContext, "Cancel authorization login", Toast.LENGTH_SHORT).show()
    }

    override fun onWeChatAuthLoginAuthDenied() {
        Toast.makeText(applicationContext, "Deny login authorization", Toast.LENGTH_SHORT).show()
    }

    override fun onWeChatAuthLoginError(errorCode: Int?, errorMessage: String?) {
        Toast.makeText(applicationContext, "Abnormal login error code:$errorCode, Error message:$errorMessage", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}
