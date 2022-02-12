package com.testing.appapartment.auth

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.testing.appapartment.R
import com.testing.appapartment.admin.MainActivityAdmin
import com.testing.appapartment.modle.passwordAdmin
import kotlinx.android.synthetic.main.activity_login_admin.*

class LoginAdminActivity : AppCompatActivity() {

    lateinit var prog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_admin)

        prog = ProgressDialog(this)
        prog.setMessage("leading...")
        prog.setCancelable(false)

        btn_Login_Admin.setOnClickListener {
            var  pass = password_Admin.text.toString()
            if(pass == passwordAdmin){
                val sharedPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putString("pass", pass)
                editor.putString("login", "Admin")
                editor.putBoolean("isLogin", true)


                editor.apply()
                startActivity(Intent(this,MainActivityAdmin::class.java))
                finish()

            }else{
                password_Admin.error = "Error password"
            }
        }
        txt_login.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()

        }
        txt_Register.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
            finish()

        }
    }
}