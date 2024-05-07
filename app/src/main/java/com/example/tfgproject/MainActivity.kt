package com.example.tfgproject

import android.content.ContentValues.TAG
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

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var toolbarViewModel: ToolbarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = firestoreSettings

        FirebaseApp.initializeApp(this)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setSupportActionBar(binding.toolbar)
       // val toolbar=binding.toolbar
       // setSupportActionBar(toolbar)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        loginViewModel.isAuthenticated.observe(this) { isAuthenticated ->
            if (!isAuthenticated) {
                navigateToLogin()
            }
        }



        toolbarViewModel = ViewModelProvider(this).get(ToolbarViewModel::class.java)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        /*toolbarViewModel.title.observe(this) { title ->
            supportActionBar?.title = title
        }*/
        lifecycleScope.launch {
            toolbarViewModel.title.collect { title ->
                findViewById<TextView>(R.id.toolbar_title).text = title
            }
        }

        val hamburgerButton=binding.buttonHamburger
        hamburgerButton.setOnClickListener {
            // Log para el botón hamburguesa
            Log.d(TAG, "Hamburger menu clicked")
        }
        val profileButton = binding.buttonProfile
        profileButton.setOnClickListener {
            // utiliza el NavController para navegar al fragmento de perfil
            navController.navigate(R.id.navigation_profile)
        }




        val navView: BottomNavigationView = binding.navView
        val navHostFragment=supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration= AppBarConfiguration(
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
                // ll usuario no está autenticado, navegar al loginFragment
                navController.navigate(R.id.action_global_navigation_login)
            }
        }


        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Ss el usuario no está autenticado y el destino no es el LoginFragment,
            // entonces redirige al LoginFragment.
            if (!loginViewModel.isAuthenticated.value!! && destination.id != R.id.loginFragment) {
                navController.navigate(R.id.action_global_navigation_login)
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.loginFragment) {

                binding.navView.visibility = View.GONE
                hamburgerButton.visibility=View.GONE
                profileButton.visibility=View.GONE
            } else {
                binding.navView.visibility = View.VISIBLE
                hamburgerButton.visibility=View.VISIBLE
                profileButton.visibility=View.VISIBLE
            }

        }
    }

    private fun navigateToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        // limpia la pila de actividades para que el usuario no pueda volver a la MainActivity sin autenticarse.
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginIntent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }





}