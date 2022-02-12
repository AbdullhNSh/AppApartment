package com.testing.appapartment.auth

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.testing.appapartment.R
import com.testing.appapartment.User.MainActivityUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {


    var mAuth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    lateinit var prog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        prog = ProgressDialog(this)
        prog.setMessage("leading...")
        prog.setCancelable(false)

       // locationListener  = getSystemService(LOCATION_SERVICE) as LocationListener


        txt_login_Admin.setOnClickListener {
            startActivity(Intent(this, LoginAdminActivity::class.java))
            finish()
        }
        txt_forget_Pass.setOnClickListener {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }
        txt_Register_User.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        btn_login.setOnClickListener {
            var x = true
            val email: String = edt_email_login.text.toString()
            val pass: String = edt_pass_login.text.toString()

            if ((email == "")) {
                x=false
                edt_email_login.error = "Please enter username"
            }
            if (pass == "") {
                x=false

                edt_pass_login.error = "Please enter password"
            }
            if(!isNetworkAvailable()){
                x = false
                Toast.makeText(this,"Please Connected the Network",Toast.LENGTH_LONG).show()
            }
            if(x){
                prog.show()

                mAuth?.signInWithEmailAndPassword(email, pass)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val sharedPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putString("email", email)
                        editor.putString("pass", pass)
                        editor.putString("login", "User")
                        editor.putBoolean("isLogin", true)


                        editor.apply()
                        val intent = Intent(this@LoginActivity, MainActivityUser::class.java)
                        startActivity(intent)
                        finish()

                    } else {

                        Toast.makeText(
                            applicationContext,
                            "please enter the valid value",
                            Toast.LENGTH_LONG
                        ).show()

                        prog.dismiss()

                    }


                }


            }
        }
    }
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
