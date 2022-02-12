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
import com.testing.appapartment.User.AddActivity
import com.testing.appapartment.User.ApartmentActivity
import com.testing.appapartment.modle.Apartment
import kotlinx.android.synthetic.main.fragment_home_user.view.*
import kotlinx.android.synthetic.main.item_apartment.view.*

class HomeUserFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null
    var adapter: FirestoreRecyclerAdapter<Apartment, apartmentViewHolder>? = null
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
        val root = inflater.inflate(R.layout.fragment_home_user, container, false)
        root.imageView2.visibility = View.VISIBLE

        getAllUser(root)

        root.add_apartment.setOnClickListener {
            startActivity(Intent(context, AddActivity::class.java))
        }
        return root


    }

    fun getAllUser(root: View) {
        prog.show()
        val query = db!!.collection("Apartment").whereEqualTo("isBool", true)
        val options =
            FirestoreRecyclerOptions.Builder<Apartment>().setQuery(query, Apartment::class.java)
                .build()

        adapter = object : FirestoreRecyclerAdapter<Apartment, apartmentViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): apartmentViewHolder {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_apartment, parent, false)
                return apartmentViewHolder(
                    view
                )
            }

            override fun onBindViewHolder(
                holder: apartmentViewHolder,
                position: Int,
                model: Apartment
            ) {
                holder.price.text = model.price.toString()+" $"
                holder.address.text = model.address.toString()


                Glide.with(context!!).load(model.image).into(holder.image)
                root.imageView2.visibility = View.GONE


                holder.itemView.setOnClickListener {
                    val idApartment = snapshots.getSnapshot(position).id
                    Log.d("hzm", idApartment)
                    val intent = Intent(context, ApartmentActivity::class.java)
                    intent.putExtra("idApartment", idApartment)
                    startActivity(intent)


                }

                holder.itemView.setOnLongClickListener(View.OnLongClickListener {
                    Toast.makeText(context, "item123123", Toast.LENGTH_LONG).show()


                    true
                })


            }


        }


        root.recyclerViewUser1.layoutManager = LinearLayoutManager(requireContext())
        root.recyclerViewUser1.adapter = adapter
        prog.dismiss()

    }

    class apartmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var price = view.item_price_apartment
        var image = view.item_image_apartment
        var address = view.item_address_apartment
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