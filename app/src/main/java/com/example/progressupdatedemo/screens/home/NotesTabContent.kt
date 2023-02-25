package com.example.progressupdatedemo.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.progressupdatedemo.components.columns.ColumnWithCenteredContent
import com.example.progressupdatedemo.components.columns.LazyColumnOfNoteCards
import com.example.progressupdatedemo.components.icons.NoNotesIcon
import com.example.progressupdatedemo.components.textfields.SearchBar
import com.example.progressupdatedemo.data.DataOrException
import com.example.progressupdatedemo.domain.models.Note
import com.example.progressupdatedemo.domain.models.NoteList
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.utils.Constants
import com.example.progressupdatedemo.utils.toJson

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NotesTabContent(
    navController: NavController,
    noteListOrException: DataOrException<NoteList, Boolean, Exception>,
) {
    val searchText = remember {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val notes = remember {
        mutableStateOf(emptyList<Note>())
    }

    if (noteListOrException.exception != null) {
        UnableToLoadNotesErrorMessage()
    } else {
        notes.value = noteListOrException.data?.notes!!

        if (notes.value.isEmpty()) {
            NoNotesMessage()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 38.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                SearchBar(
                    searchState = searchText,
                    keyboardController = keyboardController,
                    focusManager = focusManager
                )
                filterNotesBySearchTextValue(notes, searchText)
                LazyColumnOfNoteCards(notes = notes, onNoteCardClicked = { note ->
                    val noteInJsonFormat = note.toJson()!!
                    navigateToNoteDetailsScreen(navController, noteInJsonFormat)
                })
            }
        }
    }
}

private fun navigateToNoteDetailsScreen(
    navController: NavController,
    noteInJsonFormat: String,
) {
    navController.navigate(
        Screen.NoteDetailsScreen.withArgs(
            noteInJsonFormat, Constants.HOME_SCREEN_NOTES_TAB
        )
    )
}

@Composable
private fun NoNotesMessage() {
    ColumnWithCenteredContent {
        NoNotesIcon()
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun UnableToLoadNotesErrorMessage() {
    ColumnWithCenteredContent {
        Text(text = "Error loading your notes!")
    }
}

private fun filterNotesBySearchTextValue(
    notes: MutableState<List<Note>>,
    searchState: MutableState<String>,
) {
    notes.value = notes.value.filter { note ->
        note.title!!.lowercase().contains(searchState.value.lowercase())
    }
}