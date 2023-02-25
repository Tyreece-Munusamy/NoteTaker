package com.example.progressupdatedemo.domain.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import java.util.*

data class Note(
    var id: String? = UUID.randomUUID().toString(),

    var title: String? = null,
    var message: String? = null,

    @get:PropertyName("favourite")
    @set:PropertyName("favourite")
    var isFavourite: Boolean = false,

    @get:PropertyName("creation_date")
    @set:PropertyName("creation_date")
    var creationDate: Timestamp? = null
)
