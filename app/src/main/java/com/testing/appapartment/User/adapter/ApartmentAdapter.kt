package com.testing.appapartment.User.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.testing.appapartment.R
import com.testing.appapartment.User.MyApartmentActivity
import com.testing.appapartment.modle.myApartment

class ApartmentAdapter(val activity: Activity, val list: ArrayList<myApartment>) :
    RecyclerView.Adapter<ApartmentAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.item_my_apartment, parent, false)
        return ViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(activity).load(list[position].image).into(holder.image)
        holder.image.setOnClickListener {
            var  idApartment = ""
            val intent=Intent(activity,MyApartmentActivity::class.java)
            intent.putExtra("id",idApartment)
            activity.startActivity(intent)
        }


    }


    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        val image = itemView.findViewById<ImageView>(R.id.imageView_myApartment)


    }


}











