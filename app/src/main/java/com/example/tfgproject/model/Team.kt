package com.example.tfgproject.model

import com.google.firebase.database.PropertyName
import com.google.firebase.firestore.Exclude

/**
 * Data class representing a Team entity.
 *
 * @property id The unique identifier of the team. This field is excluded from automatic serialization.
 * @property name The name of the team.
 * @property players A list of player IDs belonging to the team.
 * @property story A brief story or description of the team.
 * @property imageUrl The URL of the team's image.
 * @property gamesWon The number of games the team has won.
 * @property gamesLost The number of games the team has lost.
 */
data class Team(
    @Exclude var id: String? = null,  // Exclude the field from automatic serialization
    @PropertyName("name") var name: String? = null,
    @PropertyName("players") var players: List<String>? = null,
    @PropertyName("story") var story: String? = null,
    @PropertyName("imageUrl") var imageUrl: String? = null,
    var gamesWon: Int = 0,
    var gamesLost: Int = 0
)
