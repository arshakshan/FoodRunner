package com.arshak.foodrunner.activity

import android.app.ActionBar
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.arshak.foodrunner.R
import com.arshak.foodrunner.databases.RestaurantDatabase
import com.arshak.foodrunner.databases.RestaurantEntity
import com.arshak.foodrunner.fragment.*
import com.arshak.foodrunner.model.Restaurant
import com.arshak.foodrunner.util.ConnectionManager
import com.arshak.foodrunner.util.Messenger
import com.google.android.material.navigation.NavigationView
import org.json.JSONException

class MainActivity() : AppCompatActivity(), Messenger {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    var previousMenuItem: MenuItem? = null

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.toolbar)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        setSupportActionBar(toolbar)

        setUpToolbar()

        openHome()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {

                R.id.nav_home -> {

                    openHome()

                    drawerLayout.closeDrawers()
                }

                R.id.nav_favorites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FavoritesFragment()
                        )

                        .commit()

                    supportActionBar?.title = "Favourites"
                    drawerLayout.closeDrawers()
                }

                R.id.nav_myprofile -> {

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            ProfileFragment()
                        )

                        .commit()

                    supportActionBar?.title = "Profile"
                    drawerLayout.closeDrawers()
                }

                R.id.nav_order_history -> {

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            HistoryFragment()
                        )

                        .commit()

                    supportActionBar?.title = "Order History"
                    drawerLayout.closeDrawers()
                }

                R.id.nav_faqs -> {

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FaqFragment()
                        )
                        .commit()

                    supportActionBar?.title = "FAQ"
                    drawerLayout.closeDrawers()

                }

                R.id.nav_logout -> {

                    val dialog = AlertDialog.Builder(this@MainActivity as Context)
                    dialog.setTitle("Confirm")
                    dialog.setMessage("Are you sure you want to Log out?")
                    dialog.setPositiveButton("YES") { text, listener ->
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        sharedPreferences.edit().clear().apply()
                        finish()
                    }

                    dialog.setNegativeButton("NO") { text, listener ->
                        dialog.setCancelable(true)
                    }

                    dialog.create()
                    dialog.show()

                }
            }
            return@setNavigationItemSelectedListener (true)
        }

        val convertView = LayoutInflater.from(this@MainActivity).inflate(R.layout.nav_header_main, null)
        val userName: TextView = convertView.findViewById(R.id.txtName)
        val userPhone: TextView = convertView.findViewById(R.id.txtPhone)
        if (convertView.getParent() != null) {
            (convertView.getParent() as ViewGroup).removeView(convertView) // <- fix
        }
        userName.text = sharedPreferences.getString("user_name", getString(R.string.nav_username))
        val phoneText = "+91-${sharedPreferences.getString("user_mobile_number", getString(R.string.nav_phone))}"
        userPhone.text = phoneText
        navigationView.addHeaderView(convertView)
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    fun openHome() {
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()

        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.nav_home)
    }



    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)

        when(frag){

            !is HomeFragment -> openHome()

            else -> super.onBackPressed()

        }
    }

    override fun passData(id: Int,name: String) {

        val bundle = Bundle()
        bundle.putInt("id",id)
        bundle.putString("name",name)

    }

}
