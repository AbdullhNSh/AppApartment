package com.testing.appapartment.admin.Fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.testing.appapartment.modle.OrderAdmin
import com.testing.appapartment.modle.getTimeAgo1
import kotlinx.android.synthetic.main.fragment_home_admin.view.*
import kotlinx.android.synthetic.main.fragment_order_admin.view.*
import kotlinx.android.synthetic.main.item_order_admin.view.*
import android.content.DialogInterface


class OrderAdminFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null
    var adapter: FirestoreRecyclerAdapter<OrderAdmin, orderAdminViewHolder>? = null
    lateinit var prog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth
        prog= ProgressDialog(requireContext())
        prog.setMessage("جاري التحميل")
        prog.setCancelable(false)

    }

    override fun onCreateView(//imageView4
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         var root = inflater.inflate(R.layout.fragment_order_admin, container, false)
        root.imageView4.visibility = View.VISIBLE

        getAllOrder(root)

        return root
    }
    fun getAllOrder(root:View) {
        prog.show()
        val query = db!!.collection("Apartment").whereEqualTo("isBool", false)
        val options =
            FirestoreRecyclerOptions.Builder<OrderAdmin>().setQuery(query, OrderAdmin::class.java)
                .build()

        adapter = object : FirestoreRecyclerAdapter<OrderAdmin, orderAdminViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): orderAdminViewHolder {
                var view = LayoutInflater.from(context)
                    .inflate(R.layout.item_order_admin, parent, false)
                return orderAdminViewHolder(
                    view
                )
            }

            override fun onBindViewHolder(
                holder: orderAdminViewHolder,
                position: Int,
                model: OrderAdmin
            ) {
                root.imageView4.visibility = View.GONE

                holder.time.text = getTimeAgo1(model.time)
                holder.name.text = model.name.toString()
                Glide.with(context!!).load(model.image).into(holder.image)

                holder.itemView.setOnClickListener {
                    val idOrderAdmin = snapshots.getSnapshot(position).id
                    Log.d("hzm", idOrderAdmin)

                    val intent = Intent(context, ApartmentAdminActivity::class.java)
                    intent.putExtra("idOrderAdmin", idOrderAdmin)
                    startActivity(intent)


                }

                holder.itemView.setOnLongClickListener(View.OnLongClickListener {
                  //  Toast.makeText(context, "item123123", Toast.LENGTH_LONG).show()
                    val idOrderAdmin = snapshots.getSnapshot(position).id

                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)

                    builder.setTitle("Delete")
                    builder.setMessage("Are you sure Delete?")

                    builder.setPositiveButton(
                        "YES",
                        DialogInterface.OnClickListener { dialog, which -> // Do nothing but close the dialog
                            db!!.collection("Apartment").document(idOrderAdmin).delete()

                            dialog.dismiss()
                        })

                    builder.setNegativeButton(
                        "NO",
                        DialogInterface.OnClickListener { dialog, which -> // Do nothing
                            dialog.dismiss()
                        })

                    val alert: AlertDialog = builder.create()
                    alert.show()

                    true
                })


            }


        }

        root.recyclerViewOrderAdmin.layoutManager = LinearLayoutManager(context)
        root.recyclerViewOrderAdmin.adapter = adapter

        prog.dismiss()

    }

    class orderAdminViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var time = view.txtTimeOrderAdmin
        var name = view.txtNameOrderAdmin
        var image = view.imageViewOrderAdmin
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
