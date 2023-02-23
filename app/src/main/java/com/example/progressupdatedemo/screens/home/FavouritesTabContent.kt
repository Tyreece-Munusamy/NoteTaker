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
import com.example.progressupdatedemo.components.icons.NoFavouritesIcon
import com.example.progressupdatedemo.components.textfields.SearchBar
import com.example.progressupdatedemo.data.DataOrException
import com.example.progressupdatedemo.models.Note
import com.example.progressupdatedemo.models.NoteList
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.utils.Constants
import com.example.progressupdatedemo.utils.toJson

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FavouritesTabContent(
    navController: NavController,
    noteListOrException: DataOrException<NoteList, Boolean, Exception>,
) {
    val favouriteNotes = remember {
        mutableStateOf(emptyList<Note>())
    }

    val searchText = remember {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    if (noteListOrException.data == null) {
        UnableToLoadNotesErrorMessage()
    } else {
        favouriteNotes.value = filterNotesByFavourites(noteListOrException)

        if (favouriteNotes.value.isEmpty()) {
            NoFavouriteNotesMessage()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 38.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                SearchBar(searchText, keyboardController, focusManager)
                if (searchText.value.isNotEmpty()) {
                    filterFavouritesBySearchTextValue(favouriteNotes, searchText)
                }
                LazyColumnOfNoteCards(notes = favouriteNotes, onNoteCardClicked = { note ->
                    val noteInJsonFormat = note.toJson()!!
                    navigateToHomeScreenWithFavouritesTabSelected(navController, noteInJsonFormat)
                })
            }
        }
    }
}

private fun navigateToHomeScreenWithFavouritesTabSelected(
    navController: NavController,
    noteInJsonFormat: String,
) {
    navController.navigate(
        Screen.NoteDetailsScreen.withArgs(
            noteInJsonFormat, Constants.HOME_SCREEN_FAVOURITES_TAB
        )
    )
}

private fun filterFavouritesBySearchTextValue(
    favouriteNotes: MutableState<List<Note>>,
    searchState: MutableState<String>,
) {
    favouriteNotes.value = favouriteNotes.value.filter { note ->
        note.title!!.lowercase().contains(searchState.value.lowercase())
    }
}

@Composable
private fun NoFavouriteNotesMessage() {
    ColumnWithCenteredContent {
        NoFavouritesIcon()
        Spacer(modifier = Modifier.height(100.dp))
    }
}

private fun filterNotesByFavourites(noteListOrException: DataOrException<NoteList, Boolean, Exception>) =
    noteListOrException.data?.notes!!.filter { note ->
        note.isFavourite
    }

@Composable
private fun UnableToLoadNotesErrorMessage() {
    ColumnWithCenteredContent {
        Text(text = "Unable to load your favourites")
    }
}