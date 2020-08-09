package com.arshak.foodrunner.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.arshak.foodrunner.R
import com.arshak.foodrunner.adapter.OrderHistoryAdapter
import com.arshak.foodrunner.databases.OrderEntity
import com.arshak.foodrunner.model.OrderDetails
import com.arshak.foodrunner.util.ConnectionManager
import org.json.JSONException


class HistoryFragment : Fragment() {

    lateinit var orderHistoryAdapter: OrderHistoryAdapter
    lateinit var sharedPreferences: SharedPreferences
    lateinit var recyclerOrderHistory: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var noOrders: RelativeLayout

    lateinit var layoutManager: RecyclerView.LayoutManager
    val orderHistoryList = arrayListOf<OrderDetails>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_history, container, false)

        sharedPreferences = activity?.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        ) as SharedPreferences

        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)
        layoutManager = LinearLayoutManager(activity)
        progressLayout = view.findViewById(R.id.rlLoading)
        progressBar = view.findViewById(R.id.progressBar)

        val userId = sharedPreferences.getString("user_id", "user_id")

        println("Response is $userId")

        noOrders = view.findViewById(R.id.rlNoOrders)

        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, url, null, Response.Listener {
                    try {

                        progressLayout.visibility = View.GONE

                        val data = it.getJSONObject("data")

                        val success = data.getBoolean("success")


                        if (success) {

                            val resArray = data.getJSONArray("data")

                            println("Response is $it")

                            for (i in 0 until resArray.length()) {


                                val OrderJsonObject = resArray.getJSONObject(i)

                                val foodItems = OrderJsonObject.getJSONArray("food_items")

                                val orderDetails = OrderDetails(
                                    OrderJsonObject.getString("order_id").toInt(),
                                    OrderJsonObject.getString("restaurant_name"),
                                    OrderJsonObject.getString("order_placed_at"),
                                    foodItems
                                )

                                orderHistoryList.add(orderDetails)

                                orderHistoryAdapter =
                                    OrderHistoryAdapter(
                                        activity as Context,
                                        orderHistoryList
                                    )

                                recyclerOrderHistory.adapter = orderHistoryAdapter
                                recyclerOrderHistory.layoutManager = layoutManager

                                if (orderHistoryList.isEmpty()) {

                                    noOrders.visibility = View.VISIBLE

                                } else {

                                    noOrders.visibility = View.GONE

                                    if (activity != null) {
                                        orderHistoryAdapter = OrderHistoryAdapter(
                                            activity as Context,
                                            orderHistoryList
                                        )

                                        val mLayoutManager =
                                            LinearLayoutManager(activity as Context)
                                        recyclerOrderHistory.layoutManager = mLayoutManager
                                        recyclerOrderHistory.itemAnimator = DefaultItemAnimator()
                                        recyclerOrderHistory.adapter = orderHistoryAdapter
                                    }
                                }


                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some error occured (1)",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some Unexpected error occured (2)",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                },
                Response.ErrorListener {


                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley error occured",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {


                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "40d294a3a614fa"


                    return headers
                }
            }


            queue.add(jsonObjectRequest)


        } else {


            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Not found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)


            }
            dialog.create()
            dialog.show()
        }


        return view
    }
}


