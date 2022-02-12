package com.testing.appapartment.User.Fragment

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.testing.appapartment.R
import com.testing.appapartment.User.MainActivityUser
import com.testing.appapartment.auth.LoginActivity
import com.testing.appapartment.profile.EditProfileActivity
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null
    lateinit var prog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth

        prog = ProgressDialog(requireContext())
        prog.setMessage("leading...")
        prog.setCancelable(false)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        getProfileData(root)

        root.btn_edit_profile.setOnClickListener {
            val intent = Intent(
                context,
                EditProfileActivity::class.java
            )
            startActivity(intent)
        }
        root.btn_logOut.setOnClickListener {
            val sharedPref = requireActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.clear().apply()
            Toast.makeText(context, "Data Cleared Successfully", Toast.LENGTH_SHORT)
                .show()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    fun getProfileData(root: View) {
        prog.show()
        db!!.collection("users").whereEqualTo("email", auth.currentUser.email).get()
            .addOnSuccessListener { querySnapshot ->
                try {
                    root.edt_username_profile.text =querySnapshot.documents.get(0).get("name").toString()
                    root.edt_phone_profile.text = querySnapshot.documents.get(0).get("phone").toString()
                    root.edt_email_profile.text = auth.currentUser!!.email
                    prog.dismiss()
                } catch (e: Exception) {
                    prog.dismiss()
                    Toast.makeText(requireActivity(), "$e", Toast.LENGTH_LONG).show()
                    startActivity(Intent(context,MainActivityUser::class.java))
                }
            }.addOnFailureListener { exception ->
                startActivity(Intent(context,MainActivityUser::class.java))

            }
    }
}