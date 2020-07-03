package com.huawei.authservicesuperdemoo.qq

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.QQAuthProvider
import com.huawei.authservicesuperdemoo.QQ_APP_ID
import com.huawei.authservicesuperdemoo.R
import com.tencent.connect.common.Constants
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import org.json.JSONObject


class QQSignActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private lateinit var loginWithQQBtn : AppCompatButton

    private var uId: String? = null

    private lateinit var mUIListener: IUiListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qq)

        loginWithQQBtn = findViewById(R.id.loginWithQQBtn)

        initView()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, QQSignActivity::class.java))
            }
    }

    private fun initView(){

        loginWithQQBtn.setOnClickListener{
            doLoginWithQQ()
        }

    }

    private fun doLoginWithQQ() {
        mUIListener = object : IUiListener {
            override fun onComplete(p0: Any?) {
                try {
                    val jsonObject = p0 as JSONObject
                    val token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN)
                    val openId = jsonObject.getString(Constants.PARAM_OPEN_ID)
                    val expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN)

                    loginWithQQ(token, openId)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancel() {

            }

            override fun onError(p0: UiError?) {
                Toast.makeText(applicationContext, p0?.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
        val tencent = Tencent.createInstance(QQ_APP_ID, this.applicationContext) ?: return
        if (!tencent.isSessionValid) {
            tencent.login(this, "all", mUIListener)
        } else {
            tencent.logout(this)
        }
    }

    fun loginWithQQ(accessToken: String, openId: String) {

        val credential = QQAuthProvider.credentialWithToken(accessToken, openId)

        AGConnectAuth.getInstance().signIn(credential)
            .addOnSuccessListener { signInResult -> // onSuccess

                val user = signInResult.user
                Toast.makeText(this, user.uid, Toast.LENGTH_LONG).show()
                uId = user.uid

            }
            .addOnFailureListener {
                // onFail
            }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        Tencent.handleResultData(data, mUIListener)
    }
}
