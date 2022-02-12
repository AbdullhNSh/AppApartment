package com.testing.appapartment.profile

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.testing.appapartment.R
import com.testing.appapartment.User.MainActivityUser
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null
    var reference: StorageReference? = null
    lateinit var prog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        db = Firebase.firestore
        auth = Firebase.auth
        storage = Firebase.storage
        reference = storage!!.reference
        prog = ProgressDialog(this)
        prog.setMessage("leading...")
        prog.setCancelable(false)

        getProfileData()

        btn_update_save_profile.setOnClickListener {

            val name = edt_update_username_profile.text.toString()
            var phone = edt_update_phone_profile.text.toString().toLong()

            if (!name.equals("") && phone.toString().isNotEmpty()) {
                updateProfile(name, phone)
            }

        }
        btn_update_cancel_profile.setOnClickListener {
            startActivity(Intent(this,MainActivityUser::class.java))
        }


    }

    fun getProfileData() {
        prog.show()
        db!!.collection("users").get()
            .addOnSuccessListener { querySnapshot ->
                edt_update_username_profile.setText(
                    querySnapshot.documents.get(0).get("name").toString()
                )
                edt_update_phone_profile.setText(
                    querySnapshot.documents.get(0).get("phone").toString()
                )
                edt_update_email_profile.setText(auth.currentUser!!.email)
                prog.dismiss()

            }.addOnFailureListener { exception ->
                prog.dismiss()

            }
    }

    private fun updateProfile(name: String, phone: Long) {
        prog.show()

        val user = HashMap<String, Any>()
        user["name"] = name
        user["phone"] = phone

        db!!.collection("users").whereEqualTo("email", auth.currentUser!!.email).get()
            .addOnSuccessListener { querySnapshot ->
                db!!.collection("users").document(querySnapshot.documents.get(0).id)
                    .update(user)
                val intent = Intent(this, MainActivityUser::class.java)
                startActivity(intent)
                finish()

            }.addOnFailureListener { exception ->
                prog.dismiss()

            }
    }


}