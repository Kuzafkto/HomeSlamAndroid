package com.example.tfgproject.model

import com.google.firebase.firestore.PropertyName

/**
 * Data class representing a Player entity.
 *
 * @property age The age of the player.
 * @property name The first name of the player.
 * @property surname The surname of the player.
 * @property imageUrl The URL of the player's image.
 * @property positions A list of positions the player can play, represented by integers.
 */
data class Player(
    @PropertyName("age") var age: Int? = null,
    @PropertyName("name") var name: String? = null,
    @PropertyName("surname") var surname: String? = null,
    @PropertyName("imageUrl") var imageUrl: String? = null,

    @PropertyName("positions") var positions: List<Int>? = null
)
