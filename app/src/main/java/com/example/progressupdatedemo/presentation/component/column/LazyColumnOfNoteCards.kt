package com.example.progressupdatedemo.presentation.component.column

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.progressupdatedemo.domain.model.Note
import com.example.progressupdatedemo.presentation.component.card.NoteCard

@Composable
fun LazyColumnOfNoteCards(
    notes: List<Note>,
    onNoteCardClicked: (Note) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(
            start = 18.dp, end = 18.dp, bottom = 18.dp
        )
    ) {
        items(notes) { note ->
            NoteCard(note, onNoteCardClicked)
        }
    }
}