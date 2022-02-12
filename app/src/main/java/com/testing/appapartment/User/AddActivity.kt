package com.testing.appapartment.User

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import com.vansuita.pickimage.bean.PickResult
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import com.vansuita.pickimage.listeners.IPickResult
import kotlinx.android.synthetic.main.activity_add.*
import java.util.*

class AddActivity : AppCompatActivity(), IPickResult {

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null
    var reference: StorageReference? = null
    var path: String? = null
    var name: String? = null
    var email: String? = null
    var info: String? = null
    var address: String? = null
    var phone: Long? = null
    lateinit var prog: ProgressDialog
    var lat: Double? = null
    var lng: Double? = null
    var price: Double? = null
    var price1: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)


        db = Firebase.firestore
        auth = Firebase.auth
        storage = Firebase.storage
        reference = storage!!.reference
        prog = ProgressDialog(this)
        prog.setMessage("loading...")
        prog.setCancelable(false)
       getProfileData()

        path = intent.getStringExtra("path")
        info = intent.getStringExtra("info")
        address = intent.getStringExtra("address")
        price1 = intent.getStringExtra("price")
        lat = intent.getDoubleExtra("lat", -1.0)
        lng = intent.getDoubleExtra("lng", -1.0)

        edt_info.setText(info)
        edt_price.setText(price1)
        edt_address.setText(address)



        addLocation.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("path", path)
            intent.putExtra("info", edt_info.text.toString())
            intent.putExtra("price", edt_price.text.toString())
            intent.putExtra("address", edt_address.text.toString())
            intent.putExtra("lat", lat)
            intent.putExtra("lng", lng)


            startActivity(intent)
        }
        imageView.setOnClickListener {
            PickImageDialog.build(PickSetup()).show(this)
        }



        btn_save_add.setOnClickListener {
            var x = true
            prog.show()
            try {
                price = edt_price.text.toString().toDouble()

            } catch (e: Exception) {
                Log.e("hzm", e.toString())
                edt_price.error = "Please enter the price valid"
                Toast.makeText(this, "Enter the price valid", Toast.LENGTH_LONG).show()
            }
            info = edt_info.text.toString()
            address = edt_address.text.toString()



            Log.e("path", path.toString())
            if (path == null) {

                x = false
                prog.dismiss()
                imageView.setBackgroundColor(resources.getColor(android.R.color.holo_red_dark))
            }
            if (info == "") {
                x = false
                prog.dismiss()
                edt_info.error = "Please enter info"
            }
            if (edt_price.text.toString() == "") {
                x = false

                prog.dismiss()
                edt_price.error = "Please enter price"
            }
            if (address == "") {
                x = false

                prog.dismiss()
                edt_address.error = "Please enter address"
            }
            if ((lat == -1.0) || (lng == -1.0)) {
                x = false
                prog.dismiss()
                addLocation.setBackgroundColor(resources.getColor(android.R.color.holo_red_dark))
            }
            if (x) {

                if (price != null) {
                    addUserToDB(
                        name!!,
                        info!!,
                        price!!,
                        lat!!,
                        lng!!,
                        path.toString(),
                        phone!!,
                        email!!,
                        address!!
                    )
                }

                startActivity(//info: String, price: Double, lat: String, lng: String,  path:String,phone:Int
                    Intent(
                        this,
                        MainActivityUser::class.java
                    )
                )
                finish()

            }
        }


        btn_cancel_add.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    MainActivityUser::class.java
                )
            )
            finish()

        }
    }

    override fun onPickResult(r: PickResult?) {

        imageView.setImageBitmap(r!!.bitmap)
        uploadImage(r.uri)
    }

    fun uploadImage(uri: Uri) {
        prog.show()

        reference!!.child("product" + UUID.randomUUID().toString()).putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    path = uri.toString()

                    Log.e("ham", uri.toString())

                }.addOnFailureListener {

                }
                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                prog.dismiss()

            }
            .addOnFailureListener { Exception ->
                Toast.makeText(this, "$Exception", Toast.LENGTH_LONG).show()
                prog.dismiss()

            }

    }


    private fun addUserToDB(
        name: String,
        info: String,
        price: Double,
        lat: Double,
        lng: Double,
        path: String,
        phone: Long,
        email: String,
        address: String
    ) {
        prog.show()

        val apartment =
            hashMapOf(
                "name" to name,
                "info" to info,
                "image" to path,
                "price" to price,
                "lat" to lat,
                "lng" to lng,
                "isBool" to false,
                "email" to email,
                "time" to Date().time,
                "numDisplay" to 0,
                "phone" to phone,
                "address" to address


            )
        db!!.collection("Apartment").add(apartment)
            .addOnSuccessListener { documentReference ->
                prog.dismiss()

                val intent = Intent(this, MainActivityUser::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { exception ->
                prog.dismiss()
                Log.e("ham", exception.message.toString())

            }
    }


    fun getProfileData() {
        prog.show()
        db!!.collection("users").whereEqualTo("email", auth.currentUser.email).get()
            .addOnSuccessListener { querySnapshot ->
                name = (querySnapshot.documents.get(0).get("name").toString())
                phone = (querySnapshot.documents.get(0).get("phone").toString().toLong())
                email = auth.currentUser.email
                prog.dismiss()

            }.addOnFailureListener { exception ->

            }
    }


}