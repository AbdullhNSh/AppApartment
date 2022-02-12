package com.testing.appapartment.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.firebase.auth.FirebaseAuth
import com.testing.appapartment.R
import kotlinx.android.synthetic.main.activity_forget_password.*
import kotlinx.android.synthetic.main.activity_forget_password.txt_Login_User
import kotlinx.android.synthetic.main.activity_forget_password.txt_login_Admin
import kotlinx.android.synthetic.main.activity_register.*


class ForgetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        val auth = FirebaseAuth.getInstance()


        txt_login_Admin.setOnClickListener {
            startActivity(Intent(this, LoginAdminActivity::class.java))

        }
        txt_Login_User.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))

        }

        btn_forget.setOnClickListener {
            val emailAddress = edt_email.text.toString()

            Log.e("Email sent.", "$emailAddress")

            if(emailAddress.equals("")){
                edt_email.error="please Enter The Email"
            }else{
                auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.e("Email sent.", "Email sent.")
                        }else{


                        }
                    }
            }


        }

    }
}