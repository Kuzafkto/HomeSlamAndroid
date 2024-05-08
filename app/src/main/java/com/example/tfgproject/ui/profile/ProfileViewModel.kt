package com.example.tfgproject.ui.profile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgproject.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    private val _userData = MutableStateFlow<User?>(null) // Suponiendo que User es tu clase de modelo
    val userData = _userData.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val docRef = FirebaseFirestore.getInstance().collection("users").document(userId)
                try {
                    val snapshot = docRef.get().await()
                    val user = snapshot.toObject(User::class.java)
                    _userData.value = user
                } catch (e: Exception) {
                    _userData.value = null
                }
            }
        }
    }
}
