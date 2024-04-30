package com.example.tfgproject.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class Team(
    @Exclude var id: String? = null,  // Excluye el campo de la serialización automática
    @PropertyName("name") var name: String? = null,
    @PropertyName("players") var players: List<String>? = null,
    @PropertyName("story") var story: String? = null
)
