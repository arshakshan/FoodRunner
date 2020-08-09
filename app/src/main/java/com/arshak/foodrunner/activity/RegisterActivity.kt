package com.arshak.foodrunner.activity

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.arshak.foodrunner.R
import com.arshak.foodrunner.util.SessionManager
import org.json.JSONObject




class RegisterActivity : AppCompatActivity() {

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    lateinit var sharedPreferences: SharedPreferences

    lateinit var etUserName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etAddress: EditText
    lateinit var etNewPassword: EditText
    lateinit var etConfirmNewPassword: EditText
    lateinit var btnNext: Button
    lateinit var sessionManager: SessionManager
    var emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etUserName = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etAddress = findViewById(R.id.etAddress)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmNewPassword = findViewById(R.id.etNewPassword)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        sessionManager = SessionManager(applicationContext as Context)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        btnNext = findViewById(R.id.btnNext)
        progressLayout.visibility = View.GONE
        progressBar.visibility = View.GONE

        btnNext.setOnClickListener() {

            val name = etUserName.text.toString()
            val email = etEmail.text.toString()
            val phn = etMobileNumber.text.toString()
            val address = etAddress.text.toString()
            val pwd = etNewPassword.text.toString()
            val confirmPwd = etConfirmNewPassword.text.toString()

            if ((name == "") || (email == "") || (phn == "") || (address == "") || (pwd == "") || (confirmPwd == "")) {
                Toast.makeText(this@RegisterActivity, "All Fields Required", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (name.length < 3){
                    Toast.makeText(
                        this@RegisterActivity,
                        "Name should be at least 3 Characters",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else if (phn.length != 10) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Phone Number Should Be Of Exact 10 Digits",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else if (pwd.length < 4 || confirmPwd.length < 4) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Minimum 4 Characters For Password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else if (pwd != confirmPwd) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Password And Confirm Password Do Not Match",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else if (!email.trim().matches(emailPattern)) {
                    Toast.makeText(this@RegisterActivity, "Invalid Email", Toast.LENGTH_SHORT)
                        .show()
                }
                else {

                    sendRegisterRequest(name, phn, address, pwd, email)
                }
            }
        }
    }

    fun sendRegisterRequest(
        name: String,
        phone: String,
        address: String,
        password: String,
        email: String
    ) {

        val queue = Volley.newRequestQueue(this)

        val REGISTER = "http://13.235.250.119/v2/register/fetch_result/"

        val jsonParams = JSONObject()
        jsonParams.put("name", name)
        jsonParams.put("mobile_number", phone)
        jsonParams.put("password", password)
        jsonParams.put("address", address)
        jsonParams.put("email", email)

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            REGISTER,
            jsonParams,
            Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")

                    if (success) {

                        val response = data.getJSONObject("data")
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
                        SessionManager(this@RegisterActivity as Context).setLogin(true)
                        startActivity(
                            Intent(
                                this@RegisterActivity,
                                MainActivity::class.java
                            )
                        )
                        finish()
                    } else {
                        progressLayout.visibility = View.VISIBLE
                        progressBar.visibility = View.INVISIBLE
                        val errorMessage = data.getString("errorMessage")
                        Toast.makeText(
                            this@RegisterActivity,
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    progressLayout.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                Toast.makeText(this@RegisterActivity, it.message, Toast.LENGTH_SHORT).show()
                progressLayout.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
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
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
    }

}

