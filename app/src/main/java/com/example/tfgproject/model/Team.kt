package com.example.tfgproject.model

import com.google.firebase.database.PropertyName
import com.google.firebase.firestore.Exclude

data class Team(
    @Exclude var id: String? = null,  // Excluye el campo de la serialización automática
    @PropertyName("name") var name: String? = null,
    @PropertyName("players") var players: List<String>? = null,
    @PropertyName("story") var story: String? = null,
    @PropertyName("imageUrl") var imageUrl:  String? = null,
    var gamesWon: Int = 0,
    var gamesLost: Int = 0
)
