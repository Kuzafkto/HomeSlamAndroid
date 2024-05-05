package com.example.tfgproject.ui.login

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class LoginViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth
    private val _isAuthenticated = MutableLiveData<Boolean>()
    val registrationState: LiveData<Boolean> get() = _registrationState
    private val _registrationState = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> get() = _isAuthenticated

    init {
        auth = Firebase.auth

        checkAuthentication()
    }

    private fun checkAuthentication() {
        // Establece el valor inicial basado en si el usuario está actualmente autenticado
        val currentUser = auth.currentUser
        _isAuthenticated.value = currentUser != null

        // Agrega un listener de estado de autenticación para escuchar los cambios
        auth.addAuthStateListener { firebaseAuth ->
            _isAuthenticated.value = firebaseAuth.currentUser != null
        }
    }

    /*fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Si el inicio de sesión es exitoso, actualiza el estado de autenticación
                _isAuthenticated.value = true
            } else {
                // Si falla, establece el estado de autenticación en falso
                _isAuthenticated.value = false
            }
        }
    }*/

    fun llamarLoginCoroutine(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = login(fragment = LoginFragment(),email, password)
                Log.d("TESTINGCOROUTINE", "Resultado del login: $result")
            } catch (e: Exception) {
                Log.e("TESTINGCOROUTINE", "Error en login", e)
            }
        }
    }



    fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _registrationState.value = task.isSuccessful
            }
    }
    fun login(fragment: LoginFragment, email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await() // Usando kotlinx-coroutines-play-services para await
                withContext(Dispatchers.Main) {
                    if (result.user != null) {
                        Log.d(TAG, "signInWithEmail:success")
                        // Manejar éxito de autenticación, por ejemplo actualizar UI o navegar
                        //fragment.onLoginSuccess()
                    } else {
                        Log.d(TAG, "signInWithEmail:failure")
                       //fragment.onLoginFailed("Authentication failed.")
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "signInWithEmail:failure", e)
                withContext(Dispatchers.Main) {
                    // Mostrar error usando un método en el fragmento que maneje el contexto adecuadamente
                    //fragment.onLoginFailed("Authentication failed: ${e.localizedMessage}")
                }
            }
        }
    }



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
            completionTask.await()  // Asegúrate de esperar a que se complete la tarea
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Login interrupted", e)
        }
        executor.shutdown()
        return result
    }


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

    override fun onCleared() {
        super.onCleared()
        // Aquí podrías limpiar los recursos, como listeners si los hubiera
    }
}

