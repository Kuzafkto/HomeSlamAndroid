package com.example.tfgproject.model
import com.google.firebase.firestore.PropertyName

data class Player(
    @PropertyName("age")var age: Int? = null,
    @PropertyName("name")var name: String? = null,
    @PropertyName("surname")  var surname: String? = null,
    @PropertyName("imageUrl") var imageUrl:  String? = null,

    @PropertyName("positions")
    var positions: List<Int>? = null
)
