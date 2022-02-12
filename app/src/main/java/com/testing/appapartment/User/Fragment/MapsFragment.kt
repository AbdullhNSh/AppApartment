package com.testing.appapartment.User.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.testing.appapartment.R

class MapsFragment : Fragment() {
    //lottie-android-master
    val sydney: LatLng? = null
    var lat: Double? = null
    var lng: Double? = null

    private val callback = OnMapReadyCallback { googleMap ->
        val sydney = LatLng(lat!!, lng!!)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker Apartment"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10f))
        googleMap.setOnMapClickListener{latlng->
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16f))

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_maps, container, false)
        val bundle = arguments
        lat = bundle!!.getDouble("lat")
        lng = bundle.getDouble("lng")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

}