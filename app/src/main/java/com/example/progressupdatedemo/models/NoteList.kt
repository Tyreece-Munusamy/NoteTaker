package com.example.progressupdatedemo.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import java.util.UUID

data class NoteList(
    @Exclude var id: String? = UUID.randomUUID().toString(),

    var userId: String? = null,

    @set:PropertyName("notes")
    @get:PropertyName("notes")
    var notes: List<Note>? = null
)