package com.example.tfgproject.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Game(

    @Exclude
    @PropertyName("id") var id: String? = null,

    @PropertyName("gameDate") var gameDate: String? = null,
    @PropertyName("local") var local: String? = null,
    @PropertyName("localRuns") var localRuns: String? = null,
    @PropertyName("story") var story: String? = null,
    @PropertyName("visitor") var visitor: String? = null,
    @PropertyName("visitorRuns") var visitorRuns: String? = null,
    var localName: String? = null,  // Agregar estas propiedades
    var visitorName: String? = null,
    var localImage: String? = null,
    var visitorImage: String? = null
) : Parcelable{}
