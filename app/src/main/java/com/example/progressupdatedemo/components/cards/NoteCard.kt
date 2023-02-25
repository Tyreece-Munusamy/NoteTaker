package com.example.progressupdatedemo.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progressupdatedemo.domain.models.Note

@Composable
fun NoteCard(
    note: Note,
    onNoteCardClicked: (Note) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(bottom = 15.dp)
            .clickable {
                onNoteCardClicked(note)
            }, border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)), elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                NoteTitle(note)
                NoteMessage(note)
            }
        }
    }
}

@Composable
private fun NoteMessage(note: Note) {
    Text(
        text = note.message.toString(),
        modifier = Modifier.fillMaxWidth(),
        color = Color.Black.copy(alpha = 0.5f),
        fontSize = 13.sp,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun NoteTitle(note: Note) {
    Text(text = note.title.toString())
}