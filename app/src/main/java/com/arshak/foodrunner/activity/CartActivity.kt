package com.arshak.foodrunner.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.arshak.foodrunner.R
import com.arshak.foodrunner.adapter.CartItemAdapter
import com.arshak.foodrunner.adapter.MenuRecyclerAdapter
import com.arshak.foodrunner.databases.OrderEntity
import com.arshak.foodrunner.databases.RestaurantDatabase
import com.arshak.foodrunner.fragment.MenuFragment
import com.arshak.foodrunner.model.FoodItem
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var recyclerCart: RecyclerView
    private lateinit var cartItemAdapter: CartItemAdapter
    private var orderList = ArrayList<FoodItem>()
    private lateinit var txtResName: TextView
    private lateinit var rlLoading: RelativeLayout
    private lateinit var rlCart: RelativeLayout
    private lateinit var btnPlaceOrder: Button
    private var resId: Int = 0
    private var resName: String = ""
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        sharedPreferences =
            getSharedPreferences("FoodApp", Context.MODE_PRIVATE) as SharedPreferences

        initi()
        setupToolbar()
        setUpCartList()
        placeOrder()
    }


    private fun initi() {

        rlLoading = findViewById(R.id.rlLoading)
        rlCart = findViewById(R.id.rlCart)
        txtResName = findViewById(R.id.txtCartResName)
        txtResName.text = MenuFragment.resName
        val bundle = intent.getBundleExtra("data")
        resId = intent.getIntExtra("resId", 0)
        println("Response is resid $resId")
        resName = bundle.getString("resName", "") as String

    }

    private fun setupToolbar() {

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpCartList() {

        recyclerCart = findViewById(R.id.recyclerCartItems)

        val dbList = GetItemsFromDBAsync(applicationContext).execute().get()

        for (element in dbList) {
            orderList.addAll(
                Gson().fromJson(element.foodItems, Array<FoodItem>::class.java).asList()
            )
        }

        if (orderList.isEmpty()) {
            rlCart.visibility = View.GONE
            rlLoading.visibility = View.VISIBLE
        } else {
            rlCart.visibility = View.VISIBLE
            rlLoading.visibility = View.GONE
        }

        cartItemAdapter = CartItemAdapter(this@CartActivity, orderList)
        val mLayoutManager = LinearLayoutManager(this@CartActivity)
        recyclerCart.layoutManager = mLayoutManager
        recyclerCart.itemAnimator = DefaultItemAnimator()
        recyclerCart.adapter = cartItemAdapter
    }


    private fun placeOrder() {

        btnPlaceOrder = findViewById(R.id.btnConfirmOrder)
        var sum = 0
        for (i in 0 until orderList.size) {
            sum += orderList[i].itemCost as Int
        }
        val total = "Place Order(Total: Rs. $sum)"
        btnPlaceOrder.text = total

        btnPlaceOrder.setOnClickListener {
            rlLoading.visibility = View.VISIBLE
            rlCart.visibility = View.VISIBLE
            sendServerRequest()
        }
    }

    private fun sendServerRequest() {

        val queue = Volley.newRequestQueue(this)
        val userId = sharedPreferences.getString("user_id", "user_id")
        val jsonParams = JSONObject()

        jsonParams.put(
            "user_id", userId as String
        )

        jsonParams.put("restaurant_id", resId)

        var sum = 0
        for (i in 0 until orderList.size) {
            sum += orderList[i].itemCost as Int
        }

        jsonParams.put("total_cost", sum.toString())

        val foodArray = JSONArray()

        for (i in 0 until orderList.size) {
            val foodId = JSONObject()
            foodId.put("food_item_id", orderList[i].itemId)
            foodArray.put(i, foodId)
        }

        jsonParams.put("food", foodArray)

        println("Response is $userId")

        val PLACE_ORDER = "http://13.235.250.119/v2/place_order/fetch_result/"

        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST, PLACE_ORDER, jsonParams, Response.Listener {

                try {

                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")

                    println("Response is $it")

                    if (success) {

                        val clearCart =
                            ClearDBAsync(applicationContext, resId.toString()).execute().get()
                        MenuRecyclerAdapter.isCartEmpty = true

                        val dialog = Dialog(
                            this@CartActivity,
                            android.R.style.Theme_Black_NoTitleBar_Fullscreen
                        )
                        dialog.setContentView(R.layout.order_placed_dialog)
                        dialog.show()
                        dialog.setCancelable(false)
                        val btnOk = dialog.findViewById<Button>(R.id.btnOk)
                        btnOk.setOnClickListener {
                            dialog.dismiss()
                            startActivity(Intent(this@CartActivity, MainActivity::class.java))
                            ActivityCompat.finishAffinity(this@CartActivity)
                        }
                    } else {
                        rlCart.visibility = View.VISIBLE
                        Toast.makeText(
                            this@CartActivity,
                            "Some Error occurred (1)",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                } catch (e: Exception) {
                    rlCart.visibility = View.VISIBLE
                    e.printStackTrace()
                }

            }, Response.ErrorListener {
                rlCart.visibility = View.VISIBLE
                Toast.makeText(this@CartActivity, it.message, Toast.LENGTH_SHORT).show()
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "40d294a3a614fa"
                    return headers
                }
            }

        queue.add(jsonObjectRequest)

    }

    class GetItemsFromDBAsync(context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "item-db").build()
        override fun doInBackground(vararg params: Void?): List<OrderEntity> {
            return db.orderDao().getAllOrders()
        }

    }

    class ClearDBAsync(context: Context, val resId: String) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "item-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().deleteOrders(resId)
            db.close()
            return true
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val clearCart =
            ClearDBAsync(applicationContext, resId.toString()).execute().get()
        MenuRecyclerAdapter.isCartEmpty = true
        onBackPressed()
        return true
    }
}