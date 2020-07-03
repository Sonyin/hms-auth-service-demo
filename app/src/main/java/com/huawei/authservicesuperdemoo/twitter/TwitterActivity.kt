package com.huawei.authservicesuperdemoo.twitter

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.TwitterAuthProvider
import com.huawei.authservicesuperdemoo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import twitter4j.conf.ConfigurationBuilder


class TwitterActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private lateinit var loginWithTwitterBtn : Button

    private var uId: String? = null

    lateinit var twitterDialog: Dialog
    lateinit var twitter: Twitter
    var accToken: AccessToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitter)

        loginWithTwitterBtn = findViewById(R.id.loginWithTwitterBtn)

        initView()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, TwitterActivity::class.java))
            }
    }

    private fun initView(){

        loginWithTwitterBtn.setOnClickListener{
            getRequestToken()
        }

    }

    private fun getRequestToken() {
        GlobalScope.launch(Dispatchers.Default) {
            val builder = ConfigurationBuilder()
                .setDebugEnabled(true)
                .setOAuthConsumerKey(getString(R.string.twitter_consumer_key))
                .setOAuthConsumerSecret(getString(R.string.twitter_consumer_secret))
                .setIncludeEmailEnabled(true)
            val config = builder.build()
            val factory = TwitterFactory(config)
            twitter = factory.instance
            try {
                val requestToken = twitter.oAuthRequestToken
                withContext(Dispatchers.Main) {
                    setupTwitterWebviewDialog(requestToken.authorizationURL)
                }
            } catch (e: IllegalStateException) {
                Log.e("ERROR: ", e.toString())
            }
        }
    }

    // Show twitter login page in a dialog
    @SuppressLint("SetJavaScriptEnabled")
    fun setupTwitterWebviewDialog(url: String) {
        twitterDialog = Dialog(this)
        val webView = WebView(this)
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = TwitterWebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
        twitterDialog.setContentView(webView)
        twitterDialog.show()
    }

    // A client to know about WebView navigations
    // For API 21 and above
    @Suppress("OverridingDeprecatedMember")
    inner class TwitterWebViewClient : WebViewClient() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            if (request?.url.toString().startsWith(getString(R.string.twitter_callback_url))) {
                Log.d("Authorization URL: ", request?.url.toString())
                handleUrl(request?.url.toString())

                // Close the dialog after getting the oauth_verifier
                if (request?.url.toString().contains(getString(R.string.twitter_callback_url))) {
                    twitterDialog.dismiss()
                }
                return true
            }
            return false
        }

        // For API 19 and below
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith(getString(R.string.twitter_callback_url))) {
                Log.d("Authorization URL: ", url)
                handleUrl(url)

                // Close the dialog after getting the oauth_verifier
                if (url.contains(getString(R.string.twitter_callback_url))) {
                    twitterDialog.dismiss()
                }
                return true
            }
            return false
        }

        // Get the oauth_verifier
        private fun handleUrl(url: String) {
            val uri = Uri.parse(url)
            val oauthVerifier = uri.getQueryParameter("oauth_verifier") ?: ""
            GlobalScope.launch(Dispatchers.Main) {
                accToken = withContext(Dispatchers.IO) { twitter.getOAuthAccessToken(oauthVerifier) }

                val usr = withContext(Dispatchers.IO) { twitter.verifyCredentials() }

                registerWithTwitter()
            }
        }
    }

    suspend fun isLoggedIn(): Boolean {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val accessToken = sharedPref.getString("oauth_token", "")
        val accessTokenSecret = sharedPref.getString("oauth_token_secret", "")

        val builder = ConfigurationBuilder()
        val config = builder.build()
        builder.setOAuthConsumerKey(getString(R.string.twitter_consumer_key))
            .setOAuthConsumerSecret(getString(R.string.twitter_consumer_secret))
            .setOAuthAccessToken(accessToken)
            .setOAuthAccessTokenSecret(accessTokenSecret)
        val factory = TwitterFactory(config)
        val twitter = factory.instance

        try {
            withContext(Dispatchers.IO) { twitter.verifyCredentials() }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun registerWithTwitter() {

        val token = accToken?.token
        val secret = accToken?.tokenSecret

        val credential = TwitterAuthProvider.credentialWithToken(token, secret)
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
}
