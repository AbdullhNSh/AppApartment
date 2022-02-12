package com.testing.appapartment.User

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.activity_apartment.*
import java.lang.Exception
import java.util.*

class ApartmentActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    var storage: FirebaseStorage? = null
    var reference: StorageReference? = null
    lateinit var prog: ProgressDialog
    var path: String? = null
    var db: FirebaseFirestore? = null
    val idCat = ""
    var numDisplay: Int? = null
    var lat: Double? = null
    var lng: Double? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apartment)

        db = Firebase.firestore

        val idApartment = intent.getStringExtra("idApartment")
        Log.e("idApartment", idApartment.toString())

        if (idApartment == null) {
            startActivity(Intent(this, MainActivityUser::class.java))

        } else {
            getApartment(idApartment)
            try {
                replaceFragment(MapsFragment())

            }catch (e:Exception){
                Handler().postDelayed({
                  if (lat != null) {
                replaceFragment(MapsFragment())
                    }
                 }, 3000)
            }

        }


    }

    fun replaceFragment(fragment: Fragment) {


        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_map_apartment, fragment)
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
                    val address = document.getString("address")
                    val name = document.getString("name")
                    val phone = document.getLong("phone")
                    val info = document.getString("info")
                    val image = document.getString("image")
                    val price = document.getDouble("price")
                    val timeAgo = document.getLong("time")
                    lat = document.getDouble("lat")
                    lng = document.getDouble("lng")

                    numDisplay = document.getLong("numDisplay")!!.toInt()
                    numDisplay = numDisplay!! + 1
                    txt_username_apartment.text = name
                    txt_price_apartment.text = " $\n" + price.toString()
                    txt_address_apartment.text = address
                    txt_info_apartment.text = "about : " + info
                    txt_phone_apartment.text = phone.toString()

                    txt_time_ago_apartment.text = getTimeAgo1(timeAgo!!)

                    Glide.with(this).load(image).into(imageView_apartment)


                    val user = HashMap<String, Any>()
                    user["numDisplay"] = numDisplay!!
                    docRef.update(user)

                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
            }


    }
}