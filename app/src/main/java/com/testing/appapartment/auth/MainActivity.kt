package com.testing.appapartment.auth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.testing.appapartment.R
import com.testing.appapartment.User.MainActivityUser
import com.testing.appapartment.admin.MainActivityAdmin
import com.testing.appapartment.modle.passwordAdmin
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    var mLocationRequest: LocationRequest? = null
    var mGoogleApiClient: GoogleApiClient? = null
    val REQUEST_LOCATION = 199
    var result: PendingResult<LocationSettingsResult>? = null
    private var locationManager: LocationManager? = null
    private var handler: Handler? = null
    var mLocation: Location? = null
    var lat = 0.0
    var lng = 0.0
    private val SPLASH_DISPLAY_LENGTH = 1000
    lateinit var prog: ProgressDialog

    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                mLocation = location
                //  Toast.makeText(this@SplachActivity,"$mLocation",Toast.LENGTH_LONG).show()

            }
            lat = mLocation!!.latitude
            lng = mLocation!!.longitude


            Log.e("Splash", "$lat $lng")
            if (mFusedLocationClient != null) {
                //  Toast.makeText(this@SplachActivity,"$mFusedLocationClient",Toast.LENGTH_LONG).show()
                mFusedLocationClient!!.removeLocationUpdates(this)
            }
        }
    }

    var mAuth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        prog = ProgressDialog(this)
        prog.setMessage("leading...")
        prog.setCancelable(false)

        handler = Handler()
        requestStoragePermission()
        Handler().postDelayed({
            goToApp()

        }, 15000)


    }

    private fun requestStoragePermission() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    report.areAllPermissionsGranted()
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()
                    } else {
                        if (ActivityCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this@MainActivity,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                1
                            )
                        } else {
                            mFusedLocationClient =
                                LocationServices.getFusedLocationProviderClient(this@MainActivity)
                            mLocationRequest = LocationRequest.create()
                            mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            if (isLocationServiceEnabled(this@MainActivity)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (ContextCompat.checkSelfPermission(
                                            this@MainActivity,
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        )
                                        == PackageManager.PERMISSION_GRANTED
                                    ) {
                                        mFusedLocationClient!!.requestLocationUpdates(
                                            mLocationRequest!!,
                                            mLocationCallback,
                                            Looper.myLooper()!!
                                        )
                                    } //Request Location Permission
                                } else {
                                    mFusedLocationClient!!.requestLocationUpdates(
                                        mLocationRequest!!,
                                        mLocationCallback,
                                        Looper.myLooper()!!

                                    )

                                }
                                if (isLocationServiceEnabled(this@MainActivity)) {
                                    goToApp()

                                }
                            } else {
                                mGoogleApiClient = GoogleApiClient.Builder(this@MainActivity)
                                    .addConnectionCallbacks(this@MainActivity)
                                    .addOnConnectionFailedListener(this@MainActivity)
                                    .addApi(LocationServices.API)
                                    .build()
                                mGoogleApiClient!!.connect()
                                goToApp()

                                if (isLocationServiceEnabled(this@MainActivity)) {
                                    goToApp()


                                }


                            }
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener { error: DexterError? ->
                Toast.makeText(
                    applicationContext,
                    "try_again",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .onSameThread()
            .check()
    }

    private fun showSettingsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("NeedPermissions")
        builder.setMessage("PermissionsDes")
        builder.setPositiveButton("GOTOSETTINGS") { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("cancel") { dialog, _ ->
            dialog.cancel()
            goToApp()
        }
        builder.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    @SuppressLint("WrongConstant")
    fun isLocationServiceEnabled(context: Context): Boolean {
        var gps_enabled = false
        var network_enabled = false
        try {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            val criteria = Criteria()
            criteria.powerRequirement =
                Criteria.POWER_MEDIUM // Chose your desired power consumption level.
            criteria.accuracy = Criteria.ACCURACY_MEDIUM // Choose your accuracy requirement.
            criteria.isSpeedRequired = true // Chose if speed for first location fix is required.
            criteria.isAltitudeRequired = true // Choose if you use altitude.
            criteria.isBearingRequired = true // Choose if you use bearing.
            criteria.isCostAllowed = true // Choose if this provider can waste money :-)
            locationManager!!.getBestProvider(criteria, true)
            gps_enabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            network_enabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: IndexOutOfBoundsException) {
            ex.message
        }
        return gps_enabled || network_enabled
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOCATION -> when (resultCode) {
                Activity.RESULT_OK -> requestStoragePermission()
                Activity.RESULT_CANCELED -> goToApp()

                else -> {
                }
            }
            else -> {
            }
        }
    }

    override fun onConnected(@Nullable bundle: Bundle?) {
        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(3 * 1000)
            .setFastestInterval(1000)
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest!!)
        builder.setAlwaysShow(true)
        result =
            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient!!, builder.build())
        result!!.setResultCallback(
            ResultCallback { result: LocationSettingsResult ->
                val status: Status = result.status
                when (status.statusCode) {
                    LocationSettingsStatusCodes.SUCCESS -> {
                    }
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        status.startResolutionForResult(this@MainActivity, REQUEST_LOCATION)
                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        goToApp()
                    }
                }
            }
        )
    }


    override fun onConnectionSuspended(i: Int) {}


    override fun onStart() {
        super.onStart()
        if (!isLocationServiceEnabled(this@MainActivity)) if (mGoogleApiClient != null) {
            mGoogleApiClient!!.connect()

            if (mAuth?.currentUser == null) {
                val intent = Intent(
                    this,
                    LoginActivity::class.java
                )
                //  startActivity(intent)
            }
        }
    }


    override fun onStop() {
        //TODO this added
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.disconnect()
        }
        if (mFusedLocationClient != null) {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
        }
        super.onStop()
    }

    override fun onDestroy() {
        if (mGoogleApiClient != null) mGoogleApiClient!!.disconnect()
        handler!!.removeCallbacksAndMessages(null)
        if (mFusedLocationClient != null) {

            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
        }
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        if (mGoogleApiClient != null) mGoogleApiClient!!.disconnect()
        if (mFusedLocationClient != null) {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun goToApp() {
        val sharedPref =
            getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val email: String = sharedPref.getString("email", "").toString()
        val pass: String = sharedPref.getString("pass", "").toString()
        val login: String = sharedPref.getString("login", "").toString()
        val isLogin: Boolean = sharedPref.getBoolean("isLogin", false)


        val editor = sharedPref.edit()
        editor.putBoolean("isLogin", true)
        sharedPref.edit()
        // editor.putString("MyLat",lat.toString())
        // editor.putString("MyLng",lng.toString())


        prog.show()
        if (isLogin) {
            if (!isNetworkAvailable()) {
                //  Toast.makeText(this,"Please Connected the Network",Toast.LENGTH_LONG).show()
                val intent = Intent(
                    this@MainActivity,
                    LoginActivity::class.java
                )
                startActivity(intent)
            } else {
                if (login == "User") {
                    if (email.isNotEmpty() && pass.isNotEmpty()) {

                        mAuth?.signInWithEmailAndPassword(email, pass)
                            ?.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val intent = Intent(
                                        this@MainActivity,
                                        MainActivityUser::class.java
                                    )
                                    startActivity(intent)
                                } else {
                                    prog.dismiss()

                                    Toast.makeText(
                                        applicationContext,
                                        it.exception.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()

                                }
                            }
                    }
                } else if (login == "Admin") {
                    if (pass.equals(passwordAdmin)) {

                        val intent = Intent(
                            this@MainActivity,
                            MainActivityAdmin::class.java
                        )
                        startActivity(intent)
                    }
                } else {
                    val intent =
                        Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        } else {
            val intent =
                Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}