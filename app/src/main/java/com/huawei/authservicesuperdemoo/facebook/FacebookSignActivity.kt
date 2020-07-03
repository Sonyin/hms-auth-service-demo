package com.huawei.authservicesuperdemoo.facebook

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.FacebookAuthProvider
import com.huawei.authservicesuperdemoo.R
import org.json.JSONException


class FacebookSignActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private var uId: String? = null

    private lateinit var signWithFacebookBtn : LoginButton

    var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facebook)

        initView()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, FacebookSignActivity::class.java))
            }
    }

    private fun initView(){

        signWithFacebookBtn = findViewById(R.id.signWithFacebookBtn)

        callbackManager = CallbackManager.Factory.create()
        signWithFacebookBtn.setPermissions(listOf("email", "public_profile"))

        signWithFacebookBtn.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {

                loginResult?.accessToken?.let { loadFacebookUserProfile(it) }

                val credential = FacebookAuthProvider.credentialWithToken(loginResult?.accessToken?.token)

                AGConnectAuth.getInstance().signIn(credential)
                    .addOnSuccessListener { signInResult -> // onSuccess
                        val user = signInResult.user
                        Toast.makeText(this@FacebookSignActivity, user.uid, Toast.LENGTH_LONG).show()
                        uId = user.uid

                    }
                    .addOnFailureListener {
                        // onFail
                    }
            }
            override fun onCancel() {}
            override fun onError(error: FacebookException) {}
        })

    }

    private fun loadFacebookUserProfile(newAccessToken: AccessToken) {
        val graphRequest =
            GraphRequest.newMeRequest(newAccessToken) { `object`, response ->
                try {

                    val first_name = `object`.getString("first_name")
                    val last_name = `object`.getString("last_name")
                    val email = `object`.getString("email")
                    val id = `object`.getString("id")
                    val image_url = "https://graph.facebook.com/$id/picture?type=normal"

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val parameters = Bundle()
        parameters.putString("fields", "first_name,last_name,email,id")
        graphRequest.parameters = parameters
        graphRequest.executeAsync()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
