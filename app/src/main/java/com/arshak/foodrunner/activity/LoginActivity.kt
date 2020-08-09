package com.arshak.foodrunner.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.arshak.foodrunner.R
import com.arshak.foodrunner.util.ConnectionManager
import com.arshak.foodrunner.util.SessionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var btnLogin: Button
    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var txtForgotPassword: TextView
    lateinit var txtRegister: TextView
    lateinit var sessionManager: SessionManager


    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        sessionManager = SessionManager(applicationContext as Context)

        val isLoggedIn = SessionManager(this@LoginActivity as Context).isLoggedIn()

        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtRegister = findViewById(R.id.txtRegister)


        btnLogin.setOnClickListener {

            if (ConnectionManager().checkConnectivity(this)) {

                val queue = Volley.newRequestQueue(this@LoginActivity)

                val url = "http://13.235.250.119/v2/login/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", etMobileNumber.text.toString())
                jsonParams.put("password", etPassword.text.toString())

                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                    Response.Listener {

                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")

                            if (success) {

                                val response = data.getJSONObject("data")

                                println("Response is $it")

                                sharedPreferences.edit()
                                    .putString("user_id", response.getString("user_id")).apply()
                                sharedPreferences.edit()
                                    .putString("user_name", response.getString("name")).apply()
                                sharedPreferences.edit()
                                    .putString(
                                        "user_mobile_number",
                                        response.getString("mobile_number")
                                    )
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("user_address", response.getString("address"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("user_email", response.getString("email")).apply()

                                sessionManager.setLogin(true)
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        MainActivity::class.java
                                    )
                                )
                            } else {
                                val errorMessage = data.getString("errorMessage")
                                Toast.makeText(
                                    this@LoginActivity,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            btnLogin.visibility = View.VISIBLE
                            txtForgotPassword.visibility = View.VISIBLE
                            txtRegister.visibility = View.VISIBLE
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener {
                        btnLogin.visibility = View.VISIBLE
                        txtForgotPassword.visibility = View.VISIBLE
                        txtRegister.visibility = View.VISIBLE
                        Log.e("Error::::", "/post request fail! Error: ${it.message}")
                    }) {

                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "40d294a3a614fa"
                        return headers
                    }
                }

                queue.add(jsonObjectRequest)
            } else {

                //Internet is not available

                val dialog = AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    this@LoginActivity?.finish()
                }

                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@LoginActivity)
                }

                dialog.create()
                dialog.show()

            }

        }

        txtForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        txtRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}





