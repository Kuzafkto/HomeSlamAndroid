package com.example.tfgproject.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // LiveData para observar el estado de autenticación
    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> get() = _isAuthenticated

    init {
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

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Si el inicio de sesión es exitoso, actualiza el estado de autenticación
                _isAuthenticated.value = true
            } else {
                // Si falla, establece el estado de autenticación en falso
                _isAuthenticated.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Aquí podrías limpiar los recursos, como listeners si los hubiera
    }
}

