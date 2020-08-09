package com.arshak.foodrunner.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.arshak.foodrunner.R
import com.arshak.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {


    lateinit var etMobileForgot: EditText
    lateinit var etForgotEmail: EditText
    lateinit var btnNext: Button
    lateinit var progressBarForgot: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var mobile: String
    lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etMobileForgot = findViewById(R.id.etMobileNumber)
        etForgotEmail = findViewById(R.id.etEmail)
        btnNext = findViewById(R.id.btnNext)

        progressLayout = findViewById(R.id.progressLayout)
        progressBarForgot = findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        progressBarForgot.visibility = View.GONE

        email = etForgotEmail.text.toString()


        btnNext.setOnClickListener {

            mobile = etMobileForgot.text.toString()

            progressBarForgot.visibility = View.VISIBLE

            val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

            val jsonParams = JSONObject()

            jsonParams.put("mobile_number", etMobileForgot.text.toString())
            jsonParams.put("email", etForgotEmail.text.toString())


            if (ConnectionManager().checkConnectivity(this)) {

                val jsonObjectRequest =
                    object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        try {

                            if (success) {

                                val firstTry = data.getBoolean("first_try")

                                if (firstTry) {

                                    val builder = AlertDialog.Builder(this@ForgotPasswordActivity)
                                    builder.setTitle("Information")
                                    builder.setMessage("Please check your registered Email for the OTP.")
                                    builder.setCancelable(false)
                                    builder.setPositiveButton("Ok") { _, _ ->

                                        val intent = Intent(
                                            this@ForgotPasswordActivity,
                                            OtpActivity::class.java
                                        )
                                        intent.putExtra("user_mobile", mobile)
                                        startActivity(intent)

                                    }

                                    builder.create().show()

                                } else {

                                    val builder = AlertDialog.Builder(this@ForgotPasswordActivity)
                                    builder.setTitle("Information")

                                    builder.setMessage("Please refer to the previous email for the OTP.")
                                    builder.setCancelable(false)

                                    builder.setPositiveButton("Ok") { _, _ ->
                                        val intent = Intent(
                                            this@ForgotPasswordActivity,
                                            OtpActivity::class.java
                                        )
                                        intent.putExtra("user_mobile", mobile)
                                        startActivity(intent)
                                    }

                                    builder.create().show()
                                }

                            } else {

                                progressLayout.visibility = View.GONE
                                btnNext.visibility = View.VISIBLE
                                progressBarForgot.visibility = View.GONE

                                Toast.makeText(
                                    this,
                                    data.getString("errorMessage"),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        } catch (e: JSONException) {

                            e.printStackTrace()
                            progressLayout.visibility = View.VISIBLE
                            progressBarForgot.visibility = View.GONE
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Incorrect response error!!",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    }, Response.ErrorListener {

                        progressLayout.visibility = View.VISIBLE
                        progressBarForgot.visibility = View.GONE
                        VolleyLog.e("Error::::", "/post request fail! Error: ${it.message}")
                        Toast.makeText(this@ForgotPasswordActivity, it.message, Toast.LENGTH_SHORT)
                            .show()

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

                val dialog = android.app.AlertDialog.Builder(this)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)

                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this)
                }
                dialog.create()
                dialog.show()
            }


        }


    }


    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
        startActivity(intent)
    }
}




