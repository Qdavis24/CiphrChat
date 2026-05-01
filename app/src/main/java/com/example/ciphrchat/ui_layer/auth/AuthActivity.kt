package com.example.ciphrchat.ui_layer.auth

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.ciphrchat.R
import com.example.ciphrchat.data_layer.models.User
import com.example.ciphrchat.data_layer.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHost = supportFragmentManager.findFragmentById(
            R.id.authActivity_fragmentContainerView_navHost
        ) as NavHostFragment
        val navController = navHost.navController

        lifecycleScope.launch {
            var user: User? = null
            withContext(Dispatchers.IO) {
                user = UserRepository.getUser()
            }
            navController.navigate(decideCorrectStartFrag(user))
        }


    }

    fun decideCorrectStartFrag(user: User?): Int {
        if (user == null) {
            return R.id.registerFragment
        } else {
            return R.id.loginFragment
        }
    }


}