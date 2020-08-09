package com.arshak.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.arshak.foodrunner.R


class ProfileFragment : Fragment() {

    lateinit var txtUserName: TextView
    lateinit var txtPhone: TextView
    lateinit var txtEmail: TextView
    lateinit var txtAddress: TextView
    lateinit var sharedPrefs: SharedPreferences
    var s1: String = "+91-"
    lateinit var s2: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        txtUserName = view.findViewById(R.id.txtUserName)
        txtPhone = view.findViewById(R.id.txtPhone)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtAddress = view.findViewById(R.id.txtAddress)
        txtUserName.text = sharedPrefs.getString("user_name", "")

        s2 = sharedPrefs.getString("user_mobile_number", "").toString()
        s1 = s1 + s2

        txtPhone.text = s1
        txtEmail.text = sharedPrefs.getString("user_email", "")
        txtAddress.text = sharedPrefs.getString("user_address", "")
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPrefs = context.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
    }

}