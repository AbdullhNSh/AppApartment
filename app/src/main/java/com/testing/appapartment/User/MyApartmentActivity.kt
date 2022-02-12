package com.testing.appapartment.User

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.testing.appapartment.R
import com.testing.appapartment.User.Fragment.MapsFragment
import com.testing.appapartment.modle.getTimeAgo1
import kotlinx.android.synthetic.main.activity_my_apartment.*
import java.util.HashMap

class MyApartmentActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    var storage: FirebaseStorage? = null
    var reference: StorageReference? = null
    lateinit var prog: ProgressDialog
    var path: String? = null
    var db: FirebaseFirestore? = null
    val idCat = ""
    var numDisplay: Int? =null
    var lat: Double? = null
    var lng: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_apartment)

        db = Firebase.firestore

        val idApartment = intent.getStringExtra("idApartment")
        Log.e("idApartment", idApartment.toString())

        if (idApartment == null) {
            startActivity(Intent(this, MainActivityUser::class.java))

        } else {
            getApartment(idApartment)
            Handler().postDelayed({
                if (lat != null) {
                    replaceFragment(MapsFragment())
                }
            }, 5000)
        }

        btnDeleteMyApartment.setOnClickListener {
            db!!.collection("Apartment").document(idApartment!!).delete()
            startActivity(Intent(this,MainActivityUser::class.java))

        }


    }
    fun replaceFragment(fragment: Fragment) {



        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_map_my_apartment, fragment)

        var mBundle = Bundle()
        mBundle.putDouble("lat", lat!!)
        mBundle.putDouble("lng", lng!!)
        val fragobj = fragment
        fragobj.arguments = mBundle
        transaction.commit()

    }

    fun getApartment(docId: String) {
        val docRef = db!!.collection("Apartment").document(docId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {



                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                    //   val name = document.getString("name")
                    val address = document.getString("address")
                    txt_address_my_apartment.text = address

                    val image = document.getString("image")
                    val info = document.getString("info")
                   // val address = document.getString("address")

                    val price = document.getDouble("price")
                    val timeAgo = document.getLong("time")
                    val bool  = document.getBoolean("isBool")

                    lat = document.getDouble("lat")
                    lng = document.getDouble("lng")
                    numDisplay = document.getLong("numDisplay")!!.toInt()

                    txt_num_my_apartment.text = numDisplay.toString()
                    txt_price_my_apartment.text = "$\n" + price.toString()

                    txt_time_ago_my_apartment.text = getTimeAgo1(timeAgo!!)
                   txt_bool_my_apartment.text  = "approval \n" +bool.toString()
                    txt_info_my_apartment.text = "about --> " + info
                    Glide.with(this).load(image).into(imageView_my_apartment)





                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
            }


    }
}