package com.testing.appapartment.User.Fragment

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.testing.appapartment.R
import com.testing.appapartment.User.MainActivityUser
import com.testing.appapartment.User.MyApartmentActivity
import com.testing.appapartment.modle.myApartment
import kotlinx.android.synthetic.main.fragment_my_apartment.view.*
import kotlinx.android.synthetic.main.item_my_apartment.view.*

class MyApartmentFragment : Fragment() {
    //recyclerView_my_apartment
    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null
    var adapter: FirestoreRecyclerAdapter<myApartment, apartmentViewHolder>? = null
    var x: String? = null
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
        var root = inflater.inflate(R.layout.fragment_my_apartment, container, false)
        root.imageView5.visibility = View.VISIBLE

        getAllUser(root)

        return root
    }

    fun getAllUser(root: View) {
        prog.show()
        if (auth.currentUser.email == null) {
            startActivity(Intent(context, MainActivityUser::class.java))
        }
        val query = db!!.collection("Apartment").whereEqualTo("email", auth.currentUser.email)
        val options =
            FirestoreRecyclerOptions.Builder<myApartment>().setQuery(query, myApartment::class.java)
                .build()

        adapter = object : FirestoreRecyclerAdapter<myApartment, apartmentViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): apartmentViewHolder {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_my_apartment, parent, false)
                return apartmentViewHolder(
                    view
                )
            }

            override fun onBindViewHolder(
                holder: apartmentViewHolder,
                position: Int,
                model: myApartment
            ) {

                Glide.with(context!!).load(model.image).into(holder.image)
                root.imageView5.visibility = View.GONE

                holder.itemView.setOnClickListener {
                    val idApartment = snapshots.getSnapshot(position).id
                    Log.d("hzm", idApartment)
                    val intent = Intent(context, MyApartmentActivity::class.java)
                    intent.putExtra("idApartment", idApartment)
                    startActivity(intent)


                }




            }


        }

        root.recyclerView_my_apartment.layoutManager = LinearLayoutManager(requireContext())
        root.recyclerView_my_apartment.adapter = adapter


        prog.dismiss()


    }

    class apartmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image = view.imageView_myApartment
    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()

    }


}