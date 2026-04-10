package com.kanhaiyajewellers.creditmanager.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.kanhaiyajewellers.creditmanager.R
import com.kanhaiyajewellers.creditmanager.databinding.ActivityMainBinding
import com.kanhaiyajewellers.creditmanager.reminders.ReminderConstants

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Wire bottom navigation to navController
        NavigationUI.setupWithNavController(binding.bottomNav, navController)

        // Show/hide bottom nav based on current destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.dashboardFragment, R.id.searchFragment -> {
                    binding.bottomNav.visibility = View.VISIBLE
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    binding.toolbarBack.visibility = View.GONE
                }
                else -> {
                    binding.bottomNav.visibility = View.GONE
                    binding.toolbarBack.visibility = View.VISIBLE
                }
            }
        }

        binding.toolbarBack.setOnClickListener {
            navController.navigateUp()
        }

        requestNotificationPermissionIfNeeded()
        handleReminderIntent(intent, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        handleReminderIntent(intent, navHostFragment.navController)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }
    }

    private fun handleReminderIntent(incomingIntent: Intent?, navController: NavController) {
        if (incomingIntent?.getBooleanExtra(ReminderConstants.EXTRA_OPEN_CUSTOMER_HISTORY, false) != true) return

        val customerId = incomingIntent.getLongExtra(ReminderConstants.EXTRA_CUSTOMER_ID, -1L)
        if (customerId <= 0L) return

        val args = Bundle().apply {
            putLong("customerId", customerId)
            putString("customerName", incomingIntent.getStringExtra(ReminderConstants.EXTRA_CUSTOMER_NAME) ?: "")
            putString("customerPhone", incomingIntent.getStringExtra(ReminderConstants.EXTRA_CUSTOMER_PHONE) ?: "")
            putFloat("totalPending", incomingIntent.getFloatExtra(ReminderConstants.EXTRA_PENDING_AMOUNT, 0f))
            putFloat("totalPaid", 0f)
        }
        navController.navigate(R.id.customerHistoryFragment, args)
        incomingIntent.removeExtra(ReminderConstants.EXTRA_OPEN_CUSTOMER_HISTORY)
    }
}
