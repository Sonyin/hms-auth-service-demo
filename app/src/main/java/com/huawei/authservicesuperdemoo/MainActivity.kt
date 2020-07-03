package com.huawei.authservicesuperdemoo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.huawei.agconnect.auth.*
import com.huawei.authservicesuperdemoo.email.EmailActivity
import com.huawei.authservicesuperdemoo.facebook.FacebookSignActivity
import com.huawei.authservicesuperdemoo.google.GoogleSignActivity
import com.huawei.authservicesuperdemoo.phone.PhoneActivity
import com.huawei.authservicesuperdemoo.qq.QQSignActivity
import com.huawei.authservicesuperdemoo.twitter.TwitterActivity
import com.huawei.authservicesuperdemoo.wechat.WeChatSignActivity
import com.huawei.authservicesuperdemoo.weibo.WeiboSignActivity
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hmf.tasks.Task
import com.huawei.hms.jos.JosApps
import com.huawei.hms.jos.games.Games
import com.huawei.hms.jos.games.PlayersClient
import com.huawei.hms.jos.games.player.Player
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.huawei.hms.support.hwid.result.AuthHuaweiId


class MainActivity : AppCompatActivity() {

    private val HUAWEIID_SIGNIN = 8000
    private val HUAWEIGAME_SIGNIN = 7000
    val PERMISSIONS_REQUEST_STORAGE = 122

    private lateinit var signAnonymousTv : TextView
    private lateinit var signWithEmailTv : TextView
    private lateinit var signWithMobilePhoneTv : TextView
    private lateinit var signWithHuaweiTv : TextView
    private lateinit var signWithHuaweiGameTv : TextView
    private lateinit var signWithSelfOwnedAccountTv : TextView
    private lateinit var signWithWeChatTv : TextView
    private lateinit var signWithFacebookTv : TextView
    private lateinit var signWithTwitterTv : TextView
    private lateinit var signWithWeiboTv : TextView
    private lateinit var signWithQQTv : TextView
    private lateinit var signWithGoogleTv : TextView
    private lateinit var signOutTv : TextView

    private var uId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //checkPermission()

        signAnonymousTv = findViewById(R.id.signAnonymousTv)
        signWithEmailTv = findViewById(R.id.signWithEmailTv)
        signWithMobilePhoneTv = findViewById(R.id.signWithMobilePhoneTv)
        signWithHuaweiTv = findViewById(R.id.signWithHuaweiTv)
        signWithHuaweiGameTv = findViewById(R.id.signWithHuaweiGameTv)
        signWithSelfOwnedAccountTv = findViewById(R.id.signWithSelfOwnedAccountTv)
        signWithWeChatTv = findViewById(R.id.signWithWeChatTv)
        signWithFacebookTv = findViewById(R.id.signWithFacebookTv)
        signWithTwitterTv = findViewById(R.id.signWithTwitterTv)
        signWithWeiboTv = findViewById(R.id.signWithWeiboTv)
        signWithQQTv = findViewById(R.id.signWithQQTv)
        signWithGoogleTv = findViewById(R.id.signWithGoogleTv)
        signOutTv = findViewById(R.id.signOutTv)

        val appsClient = JosApps.getJosAppsClient(this, null)
        appsClient.init()

        initView()
    }

    private fun initView(){

        signAnonymousTv.setOnClickListener(View.OnClickListener {

            AGConnectAuth.getInstance().signInAnonymously()
                .addOnSuccessListener { signInResult: SignInResult ->
                    val user = signInResult.user
                    Toast.makeText(this, user.uid, Toast.LENGTH_LONG).show()
                    uId = user.uid
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Anonymous SignIn Failed", Toast.LENGTH_LONG).show()
                }
        })

        signWithEmailTv.setOnClickListener{
            EmailActivity.launch(this)
        }

        signWithMobilePhoneTv.setOnClickListener{
            PhoneActivity.launch(this)
        }

        signWithHuaweiTv.setOnClickListener(View.OnClickListener {
            val authParams = HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAccessToken().createParams()
            val service = HuaweiIdAuthManager.getService(this, authParams)
            startActivityForResult(service.signInIntent, HUAWEIID_SIGNIN)
        })

        signWithHuaweiGameTv.setOnClickListener(View.OnClickListener {

            val authParams = HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME).createParams()
            val service = HuaweiIdAuthManager.getService(this, authParams)
            startActivityForResult(service.signInIntent, HUAWEIGAME_SIGNIN)
        })

        signWithSelfOwnedAccountTv.setOnClickListener{

            val token = "jwt token"

            val credential = SelfBuildProvider.credentialWithToken(token)
            AGConnectAuth.getInstance().signIn(credential)
                .addOnSuccessListener { signInResult -> // onSuccess

                    val user = signInResult.user
                    Toast.makeText(this, user.uid, Toast.LENGTH_LONG).show()
                    uId = user.uid

                }
                .addOnFailureListener {
                    // onFail
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }

        }

        signWithWeChatTv.setOnClickListener{
            WeChatSignActivity.launch(this)
        }

        signWithFacebookTv.setOnClickListener{
            FacebookSignActivity.launch(this)
        }

        signWithTwitterTv.setOnClickListener{
            TwitterActivity.launch(this)
        }

        signWithWeiboTv.setOnClickListener{
            WeiboSignActivity.launch(this)
        }

        signWithQQTv.setOnClickListener{
           QQSignActivity.launch(this)
        }

        signWithGoogleTv.setOnClickListener{
            GoogleSignActivity.launch(this)
        }

        signOutTv.setOnClickListener{
            AGConnectAuth.getInstance().signOut()
        }

    }

    private fun checkPermission() {
        val permissionCheck: Int = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_STORAGE)
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == HUAWEIID_SIGNIN) {
            val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)

            if (authHuaweiIdTask.isSuccessful) {

                val huaweiAccount = authHuaweiIdTask.result
                val accessToken = huaweiAccount.accessToken
                val credential = HwIdAuthProvider.credentialWithToken(accessToken)

                AGConnectAuth.getInstance().signIn(credential)
                    .addOnSuccessListener { signInResult -> // onSuccess

                        val user = signInResult.user
                        Toast.makeText(this, user.uid, Toast.LENGTH_LONG).show()
                        uId = user.uid

                    }.addOnFailureListener {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }

            } else {
                Toast.makeText(this, "HwID signIn failed: " + authHuaweiIdTask.exception.message, Toast.LENGTH_LONG).show()
            }

        } else if (requestCode == HUAWEIGAME_SIGNIN) {

            if (data == null) {
                Toast.makeText(this, "Game Signin Intent is null", Toast.LENGTH_LONG).show()
                return
            }

            val task = HuaweiIdAuthManager.parseAuthResultFromIntent(data)

            task.addOnSuccessListener { signInHuaweiId: AuthHuaweiId? ->

                val client: PlayersClient = Games.getPlayersClient(this, signInHuaweiId)
                val playerTask: Task<Player> = client.currentPlayer

                playerTask.addOnSuccessListener(OnSuccessListener<Player> { player: Player ->

                    val imageUrl: String = if (player.hasHiResImage()) player.hiResImageUri.toString() else player.iconImageUri.toString()
                    val credential = HWGameAuthProvider.Builder().setPlayerSign(player.playerSign)
                            .setPlayerId(player.playerId)
                            .setDisplayName(player.displayName)
                            .setImageUrl(imageUrl)
                            .setPlayerLevel(player.level)
                            .setSignTs(player.signTs)
                            .build()

                    AGConnectAuth.getInstance().signIn(credential)
                        .addOnSuccessListener { signInResult -> // onSuccess

                            val user = signInResult.user
                            Toast.makeText(this, user.uid, Toast.LENGTH_LONG).show()
                            uId = user.uid

                        }.addOnFailureListener {
                            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                        }

                }).addOnFailureListener { e: Exception ->
                    Toast.makeText(this, "Huawei Game failed: " + e.message, Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener { e: Exception ->
                Toast.makeText(this, "HwID signIn failed: " + e.message, Toast.LENGTH_LONG).show()
            }

        }
    }
}
