package com.example.tfgproject.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(

    @Exclude
    @PropertyName("id") var id: String? = null,

    @PropertyName("email") var email: String? = null,
    @PropertyName("name") var name: String? = null,
    @PropertyName("nickname") var nickname: String? = null,
    @PropertyName("picture") var picture: String? = null,
) : Parcelable {}

