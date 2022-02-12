package com.testing.appapartment.User

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.testing.appapartment.R
import com.testing.appapartment.User.Fragment.HomeUserFragment
import com.testing.appapartment.User.Fragment.MapsFragment2
import com.testing.appapartment.User.Fragment.MyApartmentFragment
import com.testing.appapartment.User.Fragment.ProfileFragment
import com.testing.appapartment.auth.LoginActivity
import kotlinx.android.synthetic.main.activity_main_user.*

class MainActivityUser : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {
    var mAuth: FirebaseAuth? =null
//    admin approval

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_user)

        mAuth= FirebaseAuth.getInstance()
        val sharedPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val name: String = sharedPref.getString("name", "Account User").toString()

        sharedPref.edit()

        title = "$name"
        replaceFragment(HomeUserFragment())
        bottomNavView!!.setOnNavigationItemSelectedListener(this)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.myHome -> replaceFragment(HomeUserFragment())
            R.id.myProfile -> replaceFragment(ProfileFragment())
            R.id.myApartment -> replaceFragment(MyApartmentFragment())
            R.id.myMap -> replaceFragment(MapsFragment2())


        }
        return true
    }

    fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.navigation, fragment)
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.itemLogout -> {
                val sharedPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.clear().apply()
                Toast.makeText(applicationContext, "Data Cleared Successfully", Toast.LENGTH_SHORT)
                    .show()
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()

        if (mAuth?.currentUser == null) {
            val intent = Intent(
                this,
                LoginActivity::class.java
            )
            startActivity(intent)
        }
    }
}



