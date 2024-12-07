package com.sinergi5.kliksewa.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sinergi5.kliksewa.R
import com.sinergi5.kliksewa.ui.auth.ui.auth.LoginFragment

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .commitNow()
        }
    }
}