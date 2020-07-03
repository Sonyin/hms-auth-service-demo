package com.huawei.authservicesuperdemoo.email

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.EmailAuthProvider
import com.huawei.agconnect.auth.EmailUser
import com.huawei.agconnect.auth.VerifyCodeSettings
import com.huawei.agconnect.auth.VerifyCodeSettings.ACTION_REGISTER_LOGIN
import com.huawei.authservicesuperdemoo.R
import com.huawei.hmf.tasks.OnFailureListener
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hmf.tasks.TaskExecutors
import java.util.*


class EmailActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private lateinit var emailEditText : AppCompatEditText
    private lateinit var passEditText : AppCompatEditText
    private lateinit var codeEditText : AppCompatEditText
    private lateinit var loginBtn : Button
    private lateinit var registerBtn : Button
    private lateinit var verifyEmailBtn : Button
    private lateinit var loginWithCodeBtn : Button

    private var uId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        emailEditText = findViewById(R.id.emailEditText)
        passEditText = findViewById(R.id.passEditText)
        codeEditText = findViewById(R.id.codeEditText)
        loginBtn = findViewById(R.id.loginBtn)
        registerBtn = findViewById(R.id.registerBtn)
        verifyEmailBtn = findViewById(R.id.verifyEmailBtn)
        loginWithCodeBtn = findViewById(R.id.loginWithCodeBtn)

        initView()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, EmailActivity::class.java))
            }
    }

    private fun initView(){

        verifyEmailBtn.setOnClickListener{

            val emailStr = emailEditText.text.toString()

            val settings = VerifyCodeSettings.newBuilder()
                .action(ACTION_REGISTER_LOGIN) //ACTION_REGISTER_LOGIN/ACTION_RESET_PASSWORD
                .sendInterval(120) // Minimum sending interval, ranging from 30s to 120s.
                .locale(Locale.getDefault()) // Language in which a verification code is sent, which is optional. The default value is Locale.getDefault.
                .build()

            val task = EmailAuthProvider.requestVerifyCode(emailStr, settings)
            task.addOnSuccessListener(
                TaskExecutors.uiThread(),
                OnSuccessListener {

                    Toast.makeText(this, getString(R.string.verify_sent_email), Toast.LENGTH_LONG).show()

                }).addOnFailureListener(
                TaskExecutors.uiThread(),
                OnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                })
        }

        registerBtn.setOnClickListener{

            val emailStr = emailEditText.text.toString()
            val passStr = passEditText.text.toString()
            val codeStr = codeEditText.text.toString()

            val emailUser = EmailUser.Builder().setEmail(emailStr).setVerifyCode(codeStr).setPassword(passStr)
                // Optional. If this parameter is set, the current user has created a password and can use the password to sign in.
                // If this parameter is not set, the user can only sign in using a verification code.
                .build()
            AGConnectAuth.getInstance().createUser(emailUser)
                .addOnSuccessListener {
                    // After an account is created, the user is signed in by default.

                    val user = it.user
                    Toast.makeText(this, user.uid, Toast.LENGTH_LONG).show()
                    uId = user.uid
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
        }

        loginBtn.setOnClickListener{

            val emailStr = emailEditText.text.toString()
            val passStr = passEditText.text.toString()

            val credential = EmailAuthProvider.credentialWithPassword(emailStr, passStr)

            AGConnectAuth.getInstance().signIn(credential)
                .addOnSuccessListener {
                    // Obtain sign-in information.

                    val user = it.user
                    Toast.makeText(this, user.uid, Toast.LENGTH_LONG).show()
                    uId = user.uid

                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
        }

        loginWithCodeBtn.setOnClickListener{

            val emailStr = emailEditText.text.toString()
            val passStr = passEditText.text.toString()
            val codeStr = codeEditText.text.toString()

            val credentialWithCode = EmailAuthProvider.credentialWithVerifyCode(emailStr, passStr, codeStr)

            AGConnectAuth.getInstance().signIn(credentialWithCode)
                .addOnSuccessListener {
                    // Obtain sign-in information.

                    val user = it.user
                    Toast.makeText(this, user.uid, Toast.LENGTH_LONG).show()
                    uId = user.uid

                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
        }

    }
}
