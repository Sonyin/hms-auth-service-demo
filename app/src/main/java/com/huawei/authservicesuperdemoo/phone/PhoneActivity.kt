package com.huawei.authservicesuperdemoo.phone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.PhoneAuthProvider
import com.huawei.agconnect.auth.PhoneUser
import com.huawei.agconnect.auth.VerifyCodeSettings
import com.huawei.agconnect.auth.VerifyCodeSettings.ACTION_REGISTER_LOGIN
import com.huawei.authservicesuperdemoo.R
import com.huawei.hmf.tasks.OnFailureListener
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hmf.tasks.TaskExecutors
import java.util.*


class PhoneActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private lateinit var countryCodeEditText : AppCompatEditText
    private lateinit var phoneEditText : AppCompatEditText
    private lateinit var passEditText : AppCompatEditText
    private lateinit var codeEditText : AppCompatEditText
    private lateinit var loginBtn : Button
    private lateinit var registerBtn : Button
    private lateinit var verifyPhoneBtn : Button
    private lateinit var loginWithCodeBtn : Button

    private var uId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)

        countryCodeEditText = findViewById(R.id.countryCodeEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        passEditText = findViewById(R.id.passEditText)
        codeEditText = findViewById(R.id.codeEditText)
        loginBtn = findViewById(R.id.loginBtn)
        registerBtn = findViewById(R.id.registerBtn)
        verifyPhoneBtn = findViewById(R.id.verifyPhoneBtn)
        loginWithCodeBtn = findViewById(R.id.loginWithCodeBtn)

        initView()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, PhoneActivity::class.java))
            }
    }

    private fun initView(){

        verifyPhoneBtn.setOnClickListener{

            val countryCodeStr = countryCodeEditText.text.toString()
            val phoneStr = phoneEditText.text.toString()

            val settings = VerifyCodeSettings.newBuilder()
                .action(ACTION_REGISTER_LOGIN) //ACTION_REGISTER_LOGIN/ACTION_RESET_PASSWORD
                .sendInterval(120) // Minimum sending interval, ranging from 30s to 120s.
                .locale(Locale.getDefault()) // Language in which a verification code is sent, which is optional. The default value is Locale.getDefault.
                .build()

            val task = PhoneAuthProvider.requestVerifyCode(countryCodeStr, phoneStr, settings)
            task.addOnSuccessListener(
                TaskExecutors.uiThread(),
                OnSuccessListener {

                    Toast.makeText(this, getString(R.string.verify_sent_phone), Toast.LENGTH_LONG).show()

                }).addOnFailureListener(
                TaskExecutors.uiThread(),
                OnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                })
        }

        registerBtn.setOnClickListener{

            val countryCodeStr = countryCodeEditText.text.toString()
            val phoneStr = phoneEditText.text.toString()
            val passStr = passEditText.text.toString()
            val codeStr = codeEditText.text.toString()

            val phoneUser = PhoneUser.Builder()
                .setCountryCode(countryCodeStr)
                .setPhoneNumber(phoneStr) // The value of phoneNumber must contains the country/region code and mobile number.
                .setVerifyCode(codeStr)
                .setPassword(passStr) // Mandatory. If this parameter is set, a password has been created for the current user by default and the user can sign in using the password.
                // Otherwise, the user can only sign in using a verification code.
                .build()

            AGConnectAuth.getInstance().createUser(phoneUser)
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

            val countryCodeStr = countryCodeEditText.text.toString()
            val phoneStr = phoneEditText.text.toString()
            val passStr = passEditText.text.toString()

            val credential = PhoneAuthProvider.credentialWithPassword(countryCodeStr, phoneStr, passStr)

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

            val countryCodeStr = countryCodeEditText.text.toString()
            val phoneStr = phoneEditText.text.toString()
            val passStr = passEditText.text.toString()
            val codeStr = codeEditText.text.toString()

            val credentialWithCode = PhoneAuthProvider.credentialWithVerifyCode(countryCodeStr, phoneStr, passStr, codeStr)

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
