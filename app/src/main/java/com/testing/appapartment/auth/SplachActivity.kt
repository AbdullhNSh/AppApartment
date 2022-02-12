package com.testing.appapartment.auth

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.testing.appapartment.R
import com.testing.appapartment.User.MainActivityUser
import com.testing.appapartment.admin.MainActivityAdmin
import com.testing.appapartment.modle.passwordAdmin

class SplachActivity : AppCompatActivity() {

    var email: String? = null
    var pass: String? = null
    var name: String? = null
    var phone: String? = null
    var isLogin: Boolean? = false
    var login: String? = null
    lateinit var prog: ProgressDialog
    var mAuth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splach)

        prog = ProgressDialog(this)
        prog.setMessage("leading...")
        prog.setCancelable(false)

        val sharedPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        email = sharedPref.getString("email", "").toString()
        pass = sharedPref.getString("pass", "").toString()
        login = sharedPref.getString("login", "")
        isLogin = sharedPref.getBoolean("isLogin", false)
        sharedPref.edit()

        if (!isNetworkAvailable()) {

            val intent = Intent(
                this,
                LoginActivity::class.java
            )
            startActivity(intent)
            finish()

        } else {

            if (isLogin!!) {

                if (login == "User") {
                    if (email!!.isNotEmpty() && pass!!.isNotEmpty()) {

                        mAuth?.signInWithEmailAndPassword(email, pass)
                            ?.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val intent = Intent(this, MainActivityUser::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    prog.dismiss()
                                    Toast.makeText(applicationContext, it.exception.toString(), Toast.LENGTH_LONG).show()

                                }
                            }
                    }
                } else if (login == "Admin") {
                    if (pass.equals(passwordAdmin)) {

                        val intent = Intent(
                            this,
                            MainActivityAdmin::class.java
                        )
                        startActivity(intent)
                        finish()

                    }


                } else {
                    val editor = sharedPref.edit()
                    editor.clear().apply()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()

                }
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()

            }
        }

    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}