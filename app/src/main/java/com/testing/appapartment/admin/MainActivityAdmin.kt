package com.testing.appapartment.admin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.testing.appapartment.R
import com.testing.appapartment.admin.Fragment.HomeAdminFragment
import com.testing.appapartment.admin.Fragment.OrderAdminFragment
import com.testing.appapartment.auth.LoginActivity
import kotlinx.android.synthetic.main.activity_main_admin.*



class MainActivityAdmin : AppCompatActivity()  , BottomNavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)
        title = "Account Admin"

        replaceFragment(HomeAdminFragment())
        bottomNavViewAdmin!!.setOnNavigationItemSelectedListener(this)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.myHome -> replaceFragment(HomeAdminFragment())
            R.id.myCheck ->replaceFragment(OrderAdminFragment())

        }
        return true
    }


    fun replaceFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.navigationAdmin,fragment)
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

}