package com.example.tfgproject.ui.login

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

/**
 * ViewModel for handling user authentication in the LoginActivity.
 */
class LoginViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth
    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> get() = _isAuthenticated
    private val _registrationState = MutableLiveData<Boolean>()
    val registrationState: LiveData<Boolean> get() = _registrationState

    init {
        auth = FirebaseAuth.getInstance()
        checkAuthentication()
    }

    /**
     * Checks the current authentication state and sets the _isAuthenticated LiveData accordingly.
     */
    private fun checkAuthentication() {
        val currentUser = auth.currentUser
        _isAuthenticated.value = currentUser != null

        auth.addAuthStateListener { firebaseAuth ->
            _isAuthenticated.value = firebaseAuth.currentUser != null
        }
    }

    /**
     * Logs in a user with the given email and password using Firebase Authentication.
     * This method uses coroutines for async operations.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     */
    fun llamarLoginCoroutine(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = login(fragment = LoginFragment(), email, password)
                Log.d("TESTINGCOROUTINE", "Resultado del login: $result")
            } catch (e: Exception) {
                Log.e("TESTINGCOROUTINE", "Error en login", e)
            }
        }
    }

    /**
     * Registers a new user with the given email and password using Firebase Authentication.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     */
    fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _registrationState.value = task.isSuccessful
            }
    }

    /**
     * Logs in a user with the given email and password using Firebase Authentication.
     * This method uses coroutines for async operations and handles success or failure.
     *
     * @param fragment The LoginFragment instance to handle UI updates.
     * @param email The email of the user.
     * @param password The password of the user.
     */
    fun login(fragment: LoginFragment, email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                withContext(Dispatchers.Main) {
                    if (result.user != null) {
                        Log.d(TAG, "signInWithEmail:success")
                        // Manejar éxito de autenticación, por ejemplo actualizar UI o navegar
                        // fragment.onLoginSuccess()
                    } else {
                        Log.d(TAG, "signInWithEmail:failure")
                        // fragment.onLoginFailed("Authentication failed.")
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "signInWithEmail:failure", e)
                withContext(Dispatchers.Main) {
                    // Mostrar error usando un método en el fragmento que maneje el contexto adecuadamente
                    // fragment.onLoginFailed("Authentication failed: ${e.localizedMessage}")
                }
            }
        }
    }

    /**
     * Attempts to log in a user with the given email and password using Firebase Authentication.
     * This method uses coroutines for async operations.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     * @return True if the login was successful, false otherwise.
     */
    suspend fun login2(email: String, password: String): Boolean {
        val executor = Executors.newSingleThreadExecutor()
        var result = false
        val completionTask = auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(executor) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginViewModel", "Login success")
                    _isAuthenticated.postValue(true)
                    result = true
                } else {
                    Log.w("LoginViewModel", "Login failed", task.exception)
                    _isAuthenticated.postValue(false)
                    result = false
                }
            }

        try {
            completionTask.await()
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Login interrupted", e)
        }
        executor.shutdown()
        return result
    }

    /**
     * Attempts to log in a user with the given email and password using Firebase Authentication.
     * This method uses coroutines for async operations.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     * @return True if the login was successful, false otherwise.
     */
    suspend fun loginCopy(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("LoginViewModel", "Before")
                val result: AuthResult = auth.signInWithEmailAndPassword(email, password).await()
                Log.d("LoginViewModel result", result.toString())
                _isAuthenticated.postValue(true)
                Log.d("LoginViewModel", "Login success: true")
                true
            } catch (e: Exception) {
                _isAuthenticated.postValue(false)
                Log.d("LoginViewModel", "Login failed: false", e)
                false
            }
        }
    }

    /**
     * Called when the ViewModel is destroyed.
     * This method is used to clean up resources.
     */
    override fun onCleared() {
        super.onCleared()
        // Aquí podrías limpiar los recursos, como listeners si los hubiera
    }
}
