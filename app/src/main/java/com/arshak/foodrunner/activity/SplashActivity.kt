package com.arshak.foodrunner.activity


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.arshak.foodrunner.R

class SplashActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        Handler().postDelayed({
            val startAct = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(startAct)
            finish()
        },2000)
    }
}





