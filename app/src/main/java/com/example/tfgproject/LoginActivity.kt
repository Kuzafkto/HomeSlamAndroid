package com.example.tfgproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.tfgproject.databinding.ActivityLoginBinding
import com.example.tfgproject.ui.login.LoginFragment
import com.example.tfgproject.ui.register.RegisterFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * LoginActivity handles the authentication process, including login and registration.
 */
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    /**
     * Called when the activity is first created. This is where you should do all of your normal static set up.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }

    /**
     * Switches to the RegisterFragment to allow the user to register a new account.
     */
    fun switchToRegister() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, RegisterFragment())
            .addToBackStack(null)  // Optional
            .commit()
    }

    /**
     * Switches back to the LoginFragment to allow the user to log in.
     */
    fun switchToLogin() {
        supportFragmentManager.popBackStack()
    }

    /**
     * Signs in the user with the provided email and password.
     *
     * @param email The email address of the user.
     * @param password The password of the user.
     */
    fun signInWithEmailAndPassword(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.non_empty_field_error), Snackbar.LENGTH_LONG).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginActivity", "signInWithEmail:success")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.w("LoginActivity", "signInWithEmail:failure", task.exception)
                    Snackbar.make(findViewById(android.R.id.content), "${getString(R.string.auth_failed)}: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
                }
            }
    }
}
