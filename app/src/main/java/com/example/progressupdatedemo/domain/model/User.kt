package com.example.progressupdatedemo.domain.model

import com.google.firebase.firestore.PropertyName

data class User(
    var uid: String? = null,

    @get:PropertyName("first_name")
    @set:PropertyName("first_name")
    var firstName: String? = null,

    @get:PropertyName("last_name")
    @set:PropertyName("last_name")
    var lastName: String? = null,

    var email: String? = null
)