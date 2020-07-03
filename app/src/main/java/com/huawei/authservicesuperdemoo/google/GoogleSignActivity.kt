package com.huawei.authservicesuperdemoo.google

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.GoogleAuthProvider
import com.huawei.agconnect.auth.GoogleGameAuthProvider
import com.huawei.authservicesuperdemoo.GOOGLE_PLAY_GAME_REQUEST_SIGNIN
import com.huawei.authservicesuperdemoo.GOOGLE_REQUEST_SIGNIN
import com.huawei.authservicesuperdemoo.R


class GoogleSignActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private lateinit var loginWithGoogleBtn : AppCompatButton
    private lateinit var loginWithGoogleGameBtn : AppCompatButton

    private var uId: String? = null

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mGoogleGameSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google)

        loginWithGoogleBtn = findViewById(R.id.loginWithGoogleBtn)
        loginWithGoogleGameBtn = findViewById(R.id.loginWithGoogleGameBtn)

        initGoogleSign()
        initGoogleGameSign()

        initView()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, GoogleSignActivity::class.java))
            }
    }

    private fun initView(){

        loginWithGoogleBtn.setOnClickListener{
            startLoginWithGoogle(this)
        }

        loginWithGoogleGameBtn.setOnClickListener{
            startLoginWithGooglePlayGame(this)
        }
    }

    private fun initGoogleSign() {
        val gsoBuilder: GoogleSignInOptions.Builder = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN)
        gsoBuilder.requestIdToken(getString(R.string.google_app_id))
        val signInOptions = gsoBuilder.requestProfile().build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions)
    }

    private fun initGoogleGameSign() {
        val gsoBuilder: GoogleSignInOptions.Builder = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
        gsoBuilder.requestIdToken(getString(R.string.google_app_id))
        val signInOptions = gsoBuilder.requestProfile().build()

        mGoogleGameSignInClient = GoogleSignIn.getClient(this, signInOptions)
    }

    private fun startLoginWithGoogle(activity: Activity) {
        val lastSignedInAccount: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        if (lastSignedInAccount != null) {

            mGoogleSignInClient!!.signOut().addOnCompleteListener {

                if (it.isSuccessful){
                    signInWithGoogle(activity)
                }
            }

        } else {
            signInWithGoogle(activity)
        }
    }

    private fun startLoginWithGooglePlayGame(activity: Activity) {
        val lastSignedInAccount: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        if (lastSignedInAccount != null) {

            mGoogleGameSignInClient!!.signOut().addOnCompleteListener {

                if (it.isSuccessful){
                    signInWithGooglePlayGame(activity)
                }
            }

        } else {
            signInWithGooglePlayGame(activity)
        }
    }

    private fun signInWithGoogle(activity: Activity) {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        activity.startActivityForResult(signInIntent, GOOGLE_REQUEST_SIGNIN)
    }

    private fun signInWithGooglePlayGame(activity: Activity) {
        val signInIntent = mGoogleGameSignInClient!!.signInIntent
        activity.startActivityForResult(signInIntent, GOOGLE_PLAY_GAME_REQUEST_SIGNIN)
    }

    private fun handleGoogleSignInResult(data: Intent) {
        try {
            val accountTask: com.google.android.gms.tasks.Task<GoogleSignInAccount>? = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = accountTask?.getResult(ApiException::class.java)
            if (account != null) {

                val idToken = account.idToken
                if (idToken != null) {
                    loginWithGoogle(idToken)
                }

            }
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    private fun handleGoogleGameSignInResult(data: Intent) {
        try {
            val accountTask: com.google.android.gms.tasks.Task<GoogleSignInAccount>? = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = accountTask?.getResult(ApiException::class.java)
            if (account != null) {

                val serverAuthCode = account.serverAuthCode
                if (serverAuthCode != null) {
                    loginWithGoogleGame(serverAuthCode)
                }

            }
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    private fun loginWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.credentialWithToken(idToken)
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

    private fun loginWithGoogleGame(serverAuthCode: String){
        val credential = GoogleGameAuthProvider.credentialWithToken(serverAuthCode)
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

        if (requestCode == GOOGLE_REQUEST_SIGNIN) {
            if (data != null) {
                handleGoogleSignInResult(data)
            }
        }else if (requestCode == GOOGLE_PLAY_GAME_REQUEST_SIGNIN) {
            if (data != null) {
                handleGoogleGameSignInResult(data)
            }
        }
    }
}
