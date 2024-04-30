package com.example.tfgproject.model

import com.google.firebase.firestore.PropertyName

data class Game(
    @PropertyName("gameDate") var gameDate: String? = null,
    @PropertyName("local") var local: String? = null,
    @PropertyName("localRuns") var localRuns: Int? = null,
    @PropertyName("story") var story: String? = null,
    @PropertyName("visitor") var visitor: String? = null,
    @PropertyName("visitorRuns") var visitorRuns: Int? = null
)
