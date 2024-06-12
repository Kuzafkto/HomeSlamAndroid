package com.example.tfgproject.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

/**
 * Data class representing a User entity.
 *
 * @property id The unique identifier of the user. This field is excluded from automatic serialization.
 * @property email The email address of the user.
 * @property name The name of the user.
 * @property nickname The nickname of the user.
 * @property picture The URL of the user's profile picture.
 */
@Parcelize
data class User(

    @Exclude
    @PropertyName("id") var id: String? = null,

    @PropertyName("email") var email: String? = null,
    @PropertyName("name") var name: String? = null,
    @PropertyName("nickname") var nickname: String? = null,
    @PropertyName("picture") var picture: String? = null,
) : Parcelable
