package com.example.progressupdatedemo.domain.models

import com.google.firebase.firestore.PropertyName


data class User(
    var id: String? = null,

    @get:PropertyName("first_name")
    @set:PropertyName("first_name")
    var firstName: String? = null,

    @get:PropertyName("last_name")
    @set:PropertyName("last_name")
    var lastName: String? = null,

    var email: String? = null
) {
    fun toMap() = mutableMapOf(
        "id" to id,
        "first_name" to firstName,
        "last_name" to lastName,
        "email" to email
    )
}