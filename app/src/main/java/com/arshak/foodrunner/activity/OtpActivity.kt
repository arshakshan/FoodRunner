package com.arshak.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.arshak.foodrunner.R
import com.arshak.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class OtpActivity : AppCompatActivity() {
    lateinit var edtEnterOtp: EditText
    lateinit var edtEnterNewPass: EditText
    lateinit var edtEnterCnfPass: EditText
    lateinit var btnSubmit: Button
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBarOtp: ProgressBar
    var mobileEntered: String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        edtEnterOtp = findViewById(R.id.etOtp)
        edtEnterNewPass = findViewById(R.id.etNewPassword)
        edtEnterCnfPass = findViewById(R.id.etConfirmNewPassword)
        btnSubmit = findViewById(R.id.btnNext)
        progressLayout = findViewById(R.id.progressLayout)
        progressBarOtp = findViewById(R.id.progressBar)
        btnSubmit.visibility = View.VISIBLE
        progressLayout.visibility = View.GONE
        progressBarOtp.visibility = View.GONE

        mobileEntered = intent.getStringExtra("user_mobile")

        println("Response is $mobileEntered")

        btnSubmit.setOnClickListener {

            progressBarOtp.visibility = View.VISIBLE

            val otpEntered = edtEnterOtp.text.toString()
            val newPassEntered = edtEnterNewPass.text.toString()
            val cnfPassEntered = edtEnterCnfPass.text.toString()

            if (otpEntered == "" || newPassEntered == "" || cnfPassEntered == "") {

                progressBarOtp.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
                Toast.makeText(this@OtpActivity, "All Fields Required1", Toast.LENGTH_SHORT)
                    .show()

            } else {

                if (newPassEntered.length < 6 || cnfPassEntered.length < 6)

                    Toast.makeText(
                        this,
                        "Minimum 6 Characters For Password",
                        Toast.LENGTH_SHORT
                    ).show()

                else {

                    if (newPassEntered != cnfPassEntered) {
                        progressBarOtp.visibility = View.GONE

                        btnSubmit.visibility = View.VISIBLE

                        Toast.makeText(
                            this@OtpActivity,
                            "Password And Confirm Password Do Not Match",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        val queue = Volley.newRequestQueue(this)
                        val url2 = "http://13.235.250.119/v2/reset_password/fetch_result"

                        val jsonParams2 = JSONObject()

                        jsonParams2.put("mobile_number", mobileEntered.toString())
                        jsonParams2.put("password", newPassEntered)
                        jsonParams2.put("otp", edtEnterOtp.text.toString())



                        if (ConnectionManager().checkConnectivity(this@OtpActivity)) {

                            val jsonRequest =
                                object : JsonObjectRequest(Method.POST, url2, jsonParams2,
                                    Response.Listener {

                                        val data = it.getJSONObject("data")
                                        val success = data.getBoolean("success")

                                        println("Response is $it")

                                        try {

                                            if (success) {

                                                val dialog = AlertDialog.Builder(this)
                                                dialog.setTitle("Confirmation")
                                                dialog.setMessage("Your password has been successfully changed")

                                                dialog.setPositiveButton("OK") { text, Listener ->

                                                    startActivity(
                                                        Intent(
                                                            this,
                                                            LoginActivity::class.java
                                                        )
                                                    )

                                                }

                                                dialog.create()
                                                dialog.show()

                                            } else {

                                                progressBarOtp.visibility = View.GONE
                                                btnSubmit.visibility = View.VISIBLE
                                                Toast.makeText(
                                                    this,
                                                    data.getString("errorMessage"),
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                            }
                                        } catch (e: JSONException) {

                                            e.printStackTrace()
                                            Toast.makeText(
                                                this,
                                                "Some unexpected error occured",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        }
                                    }, Response.ErrorListener {

                                        progressLayout.visibility = View.VISIBLE
                                        progressBarOtp.visibility = View.GONE
                                        VolleyLog.e("Error::::", "/post request fail! Error: ${it.message}")
                                        Toast.makeText(this@OtpActivity, it.message, Toast.LENGTH_SHORT).show()

                                    }) {

                                    override fun getHeaders(): MutableMap<String, String> {

                                        val headers = HashMap<String, String>()
                                        headers["Content-type"] = "application/json"
                                        headers["token"] = "40d294a3a614fa"
                                        return headers

                                    }

                                }
                            queue.add(jsonRequest)

                        }
                    }
                }
            }
        }
    }


    fun showDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Information")
        dialog.setMessage("Please refer to the previous email for the OTP")
        dialog.setPositiveButton("OK") { text, listener ->

        }
        dialog.create()
        dialog.show()
    }
}