package com.example.ciphrchat.ui_layer.main

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.ciphrchat.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    val viewModel: MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainActivity_fragmentContainerView_navHost) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.mainActivity_bottomNav)
        val navHost =
            findViewById<FragmentContainerView>(R.id.mainActivity_fragmentContainerView_navHost)
        val spinner = findViewById<ProgressBar>(R.id.mainActivity_progressBar_loading)
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNav.visibility = if (destination.id == R.id.conversationFragment) View.GONE else View.VISIBLE
        }

        observeViewModel(bottomNav, navHost, spinner)
    }

    private fun observeViewModel(
        bottomNav: BottomNavigationView,
        navHost: FragmentContainerView,
        spinner: ProgressBar
    ) {
        viewModel.isConnected.observe(this) { connected ->
            if (connected) {
                spinner.visibility = View.GONE
                navHost.visibility = View.VISIBLE
                bottomNav.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        viewModel.contactRequestManager.unreadCount.observe(this) { count ->
            val badge = bottomNav.getOrCreateBadge(R.id.contactRequestsFragment)
            if (count == 0) {
                badge.isVisible = false
            } else {
                badge.isVisible = true
                badge.number = count
            }
        }
    }
}
