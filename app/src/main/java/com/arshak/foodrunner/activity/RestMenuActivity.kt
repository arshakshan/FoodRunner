package com.arshak.foodrunner.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.arshak.foodrunner.R
import com.arshak.foodrunner.adapter.MenuRecyclerAdapter
import com.arshak.foodrunner.fragment.HomeFragment
import com.arshak.foodrunner.fragment.MenuFragment
import com.arshak.foodrunner.model.Restaurant
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_rest_menu.*

class RestMenuActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var restaurant: Restaurant

    var resId: Int ?= 0
    var resName: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rest_menu)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        frameLayout = findViewById(R.id.frame)


        setUpToolbar()
        resId = intent?.getIntExtra("id",0)
        resName = intent?.getStringExtra("name")
        openMenu()
    }

    fun openMenu() {

        val fragment = MenuFragment()

        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = resName
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onSupportNavigateUp(): Boolean {
        val dialog = AlertDialog.Builder(this@RestMenuActivity as Context)
        if(!MenuRecyclerAdapter.isCartEmpty) {
            dialog.setTitle("Confirmation")
            dialog.setMessage("Going back will reset cart items. Do you still want to proceed")
            dialog.setPositiveButton("Yes") { text, listener ->
                val Intent = Intent(this@RestMenuActivity, MainActivity::class.java)
                startActivity(Intent)
                finish()
            }

            dialog.setNegativeButton("No") { text, listener ->

            }

            dialog.create()
            dialog.show()

        }else{
            onBackPressed()
        }

        return true
    }

   

}