package com.example.tfgproject.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

/**
 * Data class representing a Game entity.
 *
 * @property id The unique identifier for the game. Excluded from Firestore.
 * @property gameDate The date of the game.
 * @property local The ID of the local team.
 * @property localRuns The runs scored by the local team.
 * @property story The story associated with the game.
 * @property visitor The ID of the visitor team.
 * @property visitorRuns The runs scored by the visitor team.
 * @property localName The name of the local team.
 * @property visitorName The name of the visitor team.
 * @property localImage The image URL of the local team.
 * @property visitorImage The image URL of the visitor team.
 */
@Parcelize
data class Game(
    @Exclude var id: String? = null,
    @PropertyName("gameDate") var gameDate: String? = null,
    @PropertyName("local") var local: String? = null,
    @PropertyName("localRuns") var localRuns: String? = null,
    @PropertyName("story") var story: String? = null,
    @PropertyName("visitor") var visitor: String? = null,
    @PropertyName("visitorRuns") var visitorRuns: String? = null,
    var localName: String? = null,
    var visitorName: String? = null,
    var localImage: String? = null,
    var visitorImage: String? = null
) : Parcelable
