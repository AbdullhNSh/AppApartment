package com.testing.appapartment.auth

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.testing.appapartment.R
import com.testing.appapartment.User.MainActivityUser
import kotlinx.android.synthetic.main.activity_register.*
import java.lang.Exception


class RegisterActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    lateinit var prog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        prog = ProgressDialog(this)
        prog.setMessage("leading...")
        prog.setCancelable(false)


        if (!isNetworkAvailable()) {
            Toast.makeText(applicationContext, "Please Connected the Network", Toast.LENGTH_LONG)
                .show()

        }

        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        txt_login_Admin.setOnClickListener {
            startActivity(Intent(this, LoginAdminActivity::class.java))

        }
        txt_Login_User.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))

        }

        btn_Register.setOnClickListener {
            var x = true
            var phone: Long? =null
            var pass = edt_pass_register.text.toString()
            var email = edt_email_register.text.toString()
            try {
                var phone = edt_phone_register.text.toString().toLong()

            }catch (e:Exception){
                x = false

                edt_phone_register.error = "Enter the valid phone"

            }
            var name = edt_name_register.text.toString()


            if (email.toString() == "") {
                x = false
                edt_email_register.error = "Enter the email"
            }
            if (pass.toString() == "") {
                x = false

                edt_pass_register.error = "Enter the pass"

            }
            if (name.toString() == "") {
                x = false

                edt_name_register.error = "Enter the name"

            }
            if(!isNetworkAvailable()){
                x = false
                Toast.makeText(this,"Please Connected the Network",Toast.LENGTH_LONG).show()
            }
            if (phone.toString() == "") {
                x = false

                edt_phone_register.error = "Enter the phone"

            }
            if (x) {
                createNewAcount(email, pass, name, phone!!)

            }
        }


    }

    private fun createNewAcount(email: String, pass: String, name: String, phone: Long) {
        prog.show()

        mAuth?.createUserWithEmailAndPassword(email, pass)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = mAuth!!.currentUser
                    addUser(user.uid, name, email, phone)

                    val sharedPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putString("email", email)
                    editor.putString("pass", pass)
                    editor.putString("name", name)
                    editor.putLong("phone", phone)
                    editor.putString("login", "User")
                    editor.putBoolean("isLogin", true)

                    editor.apply()



                } else {
                    prog.dismiss()
                    if (!isNetworkAvailable()) {
                        Toast.makeText(
                            applicationContext,
                            "Please Connected the Network",
                            Toast.LENGTH_LONG
                        ).show()

                    }


                }
            }
    }

    private fun addUser(id: String, name: String, email: String, phone: Long) {
        prog.show()

        var user =
            hashMapOf("id" to id, "name" to name, "email" to email, "phone" to phone)
        db!!.collection("users").add(user)
            .addOnSuccessListener { documentReference ->
                val intent = Intent(
                    this,
                    MainActivityUser::class.java
                )
                startActivity(intent)

            }.addOnFailureListener {
                prog.dismiss()

            }

    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}