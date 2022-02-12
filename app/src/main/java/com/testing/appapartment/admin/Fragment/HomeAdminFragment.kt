package com.testing.appapartment.admin.Fragment

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
import com.testing.appapartment.admin.ApartmentAdminActivity
import com.testing.appapartment.modle.Apartment
import kotlinx.android.synthetic.main.fragment_home_admin.view.*
import kotlinx.android.synthetic.main.item_apartment.view.*


class HomeAdminFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null
    var x = 0
    var adapter: FirestoreRecyclerAdapter<Apartment, ApartmentViewHolder>? = null
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
        var root = inflater.inflate(R.layout.fragment_home_admin, container, false)

        root.imageView3.visibility = View.VISIBLE

        getAllUser(root)


        return root
    }

    fun getAllUser(root: View) {
        prog.show()
        val query = db!!.collection("Apartment")
        val options =
            FirestoreRecyclerOptions.Builder<Apartment>().setQuery(query, Apartment::class.java)
                .build()

        adapter = object : FirestoreRecyclerAdapter<Apartment, ApartmentViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ApartmentViewHolder {
                var view = LayoutInflater.from(context)
                    .inflate(R.layout.item_apartment, parent, false)
                return ApartmentViewHolder(
                    view
                )
            }

            override fun onBindViewHolder(
                holder: ApartmentViewHolder,
                position: Int,
                model: Apartment
            ) {
                holder.price.text = model.price.toString()+"$"

                holder.address.text = model.address

                root.imageView3.visibility = View.GONE

                Glide.with(context!!).load(model.image).into(holder.image)


                holder.itemView.setOnClickListener {
                    val idOrderAdmin = snapshots.getSnapshot(position).id
                    Log.d("idOrderAdmin", idOrderAdmin)

                    val intent = Intent(context, ApartmentAdminActivity::class.java)
                    intent.putExtra("idOrderAdmin", idOrderAdmin)
                    intent.putExtra("id", 1)

                    startActivity(intent)


                }

                holder.itemView.setOnLongClickListener(View.OnLongClickListener {
                    Toast.makeText(context, "item123123", Toast.LENGTH_LONG).show()


                    true
                })


            }


        }

        root.recyclerViewAdmin.layoutManager = LinearLayoutManager(context)
        root.recyclerViewAdmin.adapter = adapter
        prog.dismiss()


    }

    class ApartmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var price = view.item_price_apartment
        var address = view.item_address_apartment

        var image = view.item_image_apartment
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