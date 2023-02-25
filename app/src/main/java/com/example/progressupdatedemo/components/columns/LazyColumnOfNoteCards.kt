package com.example.progressupdatedemo.components.columns

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.progressupdatedemo.components.cards.NoteCard
import com.example.progressupdatedemo.domain.models.Note

@Composable
fun LazyColumnOfNoteCards(
    notes: MutableState<List<Note>>,
    onNoteCardClicked: (Note) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(
            start = 18.dp, end = 18.dp, bottom = 18.dp
        )
    ) {
        items(notes.value) { note ->
            NoteCard(note, onNoteCardClicked)
        }
    }
}