package com.example.tfgproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tfgproject.databinding.ActivityMainBinding
import com.example.tfgproject.ui.login.LoginViewModel
import com.example.tfgproject.ui.toolbar.ToolbarViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.launch

/**
 * MainActivity is the primary activity that handles navigation and user authentication.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var toolbarViewModel: ToolbarViewModel

    /**
     * Called when the activity is first created. This is where you should do all of your normal static set up.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = firestoreSettings

        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        loginViewModel.isAuthenticated.observe(this) { isAuthenticated ->
            if (!isAuthenticated) {
                navigateToLogin()
            }
        }

        toolbarViewModel = ViewModelProvider(this).get(ToolbarViewModel::class.java)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        lifecycleScope.launch {
            toolbarViewModel.title.collect { title ->
                findViewById<TextView>(R.id.toolbar_title).text = title
            }
        }

        val profileButton = binding.buttonProfile
        profileButton.setOnClickListener {
            navController.navigate(R.id.navigation_profile)
        }

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_noticias,
                R.id.navigation_partidos,
                R.id.navigation_equipos,
                R.id.navigation_login,
                R.id.action_global_navigation_login,
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        loginViewModel.isAuthenticated.observe(this) { isAuthenticated ->
            if (!isAuthenticated) {
                navController.navigate(R.id.action_global_navigation_login)
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (!loginViewModel.isAuthenticated.value!! && destination.id != R.id.loginFragment) {
                navController.navigate(R.id.action_global_navigation_login)
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.loginFragment || destination.id == R.id.ProfileFragment) {
                binding.navView.visibility = View.GONE
                profileButton.visibility = View.GONE
            } else {
                binding.navView.visibility = View.VISIBLE
                profileButton.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Navigates to the LoginActivity to allow the user to log in.
     */
    private fun navigateToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginIntent)
        finish()
    }

    /**
     * Handles navigation when the user presses the Up button in the app bar.
     *
     * @return True if the navigation was successful, false otherwise.
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
