package com.testing.appapartment.admin

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
import com.testing.appapartment.R
import com.testing.appapartment.User.Fragment.MapsFragment
import com.testing.appapartment.modle.getTimeAgo1
import kotlinx.android.synthetic.main.activity_apartment_admin.*
import java.util.*

class ApartmentAdminActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null
    var idApartment: String? = null
    lateinit var prog: ProgressDialog
    var id: Int? = null
    var lat: Double? = null
    var lng: Double? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apartment_admin)

        db = Firebase.firestore
        prog = ProgressDialog(this)
        prog.setMessage("leading...")
        prog.setCancelable(false)


        idApartment = intent.getStringExtra("idOrderAdmin")

        Log.e("docId", idApartment.toString())

        if (idApartment == null) {
            startActivity(Intent(this, MainActivityAdmin::class.java))


        } else {
            getApartment(idApartment!!)
            Handler().postDelayed({
                if (lat != null) {
                    replaceFragment(MapsFragment())
                }
            }, 5000)

        }

        btnOk_Admin.setOnClickListener {
            getApartment1(idApartment!!, true)
        }
        btnDelete_Admin.setOnClickListener {
            getApartment1(idApartment!!, false)

        }

    }

    fun replaceFragment(fragment: Fragment) {


        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_admin_map, fragment)
        var mBundle = Bundle()
        mBundle.putDouble("lat", lat!!)
        mBundle.putDouble("lng", lng!!)
        val fragobj = fragment
        fragobj.arguments = mBundle
        transaction.commit()
    }

    fun getApartment(docId: String) {



        var x =   db!!.collection("Apartment").whereEqualTo("Apartment",false).whereGreaterThan("Apartment",false)
        
        prog.show()
        val docRef = db!!.collection("Apartment").document(docId)//"P3diau5VgMbiWNDlQij3")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                    val name = document.getString("name")
                    Log.e("name", name.toString())


                    val address = document.getString("address")
                    val image = document.getString("image")
                    val info = document.getString("info")
                    val phone = document.getLong("phone")

                    val price = document.getDouble("price")
                    val timeAgo = document.getLong("time")
                    lat = document.getDouble("lat")
                    lng = document.getDouble("lng")

                    txt_phone_apartment_Admin.text = phone.toString()
                    txt_info_apartment_Admin.text = "about : " + info
                    txt_username_apartment_Admin.text = name
                    txt_price_apartment_Admin.text = " $\n " + price.toString()//  price : " + price.toString()+" $"
                    txt_address_apartment_Admin.text = address//"address : " + address
                    txt_time_ago_apartment_Admin.text = getTimeAgo1(timeAgo!!).toString()

                    Glide.with(this).load(image).into(imageView_apartment_Admin)
                    prog.dismiss()

                } else {
                    Log.d("TAG", "No such document")
                    startActivity(Intent(this, MainActivityAdmin::class.java))

                    prog.dismiss()
                }
            }.addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
                prog.dismiss()
            }
    }


    fun getApartment1(docId: String, bool: Boolean) {
        prog.show()

        if (bool) {
            val docRef = db!!.collection("Apartment").document(docId)
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {

                    val user = HashMap<String, Any>()
                    user["isBool"] = true
                    docRef.update(user)
                    startActivity(Intent(this, MainActivityAdmin::class.java))


                } else {
                    Log.d("TAG", "No such document")
                }
            }.addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
        } else {
            db!!.collection("Apartment").document(docId).delete()
            startActivity(Intent(this, MainActivityAdmin::class.java))
        }


    }
}