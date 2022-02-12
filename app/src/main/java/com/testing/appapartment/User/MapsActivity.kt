package com.testing.appapartment.User

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.testing.appapartment.R
import com.testing.appapartment.databinding.ActivityMapsBinding
import java.text.DecimalFormat

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    var mLocation: Location? = null

    var lat: Double? = null
    var lng: Double? = null
    var path: String? = null
    var price: String? = null
    var info: String? = null
    var address: String? = null

    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                mLocation = location
                //  Toast.makeText(this@SplachActivity,"$mLocation",Toast.LENGTH_LONG).show()
            }
            lat = mLocation!!.latitude
            lng = mLocation!!.longitude
            val sydney = LatLng(lat!!, lng!!)
            mMap.addMarker(MarkerOptions().position(sydney).title("Marker"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16f))

            if (mFusedLocationClient != null) {
                mFusedLocationClient!!.removeLocationUpdates(this)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)




        path = intent.getStringExtra("path")
        info = intent.getStringExtra("info")
        address = intent.getStringExtra("address")
        price = intent.getStringExtra("price")
        lat = intent.getDoubleExtra("lat", -1.0)
        lng = intent.getDoubleExtra("lng", -1.0)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            getLocationRequest()!!,
            mLocationCallback,
            Looper.myLooper()!!
        )
    }

    fun getLocationRequest(): LocationRequest? {
        val LocationRequest = LocationRequest.create()
        LocationRequest.interval = 3000
        return LocationRequest
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        mMap.isMyLocationEnabled = true

        // Add a marker in Sydney and move the camera
        var Apartment = LatLng(-34.0, 151.0)

        if((lat != -1.0)||(lng!= -1.0)){
            Apartment = LatLng(lat!!, lng!!)

        }
        mMap.addMarker(MarkerOptions().position(Apartment).title("Marker in Apartment"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Apartment, 16f))




        mMap.setOnMapClickListener { latlng ->
           // mMap.clear()

            mMap.addMarker(MarkerOptions().position(latlng).title("Marker in Apartment"))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Apartment, 16f))

            CalculationByDistance(Apartment,latlng)
            val intent = Intent(this,AddActivity::class.java)
            intent.putExtra("path", path)
            intent.putExtra("info", info)
            intent.putExtra("price", price)
            intent.putExtra("address", address)
            intent.putExtra("lat", lat)
            intent.putExtra("lng", lng)
           // startActivity(intent)


        }
    }


    fun CalculationByDistance(StartP: LatLng, EndP: LatLng): Double {
        val Radius = 6371 // radius of earth in Km
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2)))
        val c = 2 * Math.asin(Math.sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec: Int = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec: Int = Integer.valueOf(newFormat.format(meter))
        Log.e(
            "Radius Value", "" + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec
        )
        return Radius * c
    }
}