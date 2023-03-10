package com.example.progressupdatedemo.presentation.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.progressupdatedemo.core.constants.Constants
import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.model.Note
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.presentation.component.column.ColumnWithCenteredContent
import com.example.progressupdatedemo.presentation.component.column.LazyColumnOfNoteCards
import com.example.progressupdatedemo.presentation.component.icons.NoNotesIcon
import com.example.progressupdatedemo.presentation.component.loading_animation.LoadingShimmerEffect
import com.example.progressupdatedemo.presentation.component.textfield.SearchBar

@Composable
fun NotesTabContent(
    navController: NavController,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val isLoadingUserData = remember(homeScreenViewModel.getNotesUseCaseResponseState.value) {
        homeScreenViewModel.getNotesUseCaseResponseState.value is Response.Loading
    }
    val getNotesUseCaseResponse = homeScreenViewModel.getNotesUseCaseResponseState.value

    if (isLoadingUserData) {
        LoadingShimmerEffect()
    } else {
        NoteListOrErrorMessage(
            getNotesUseCaseResponse, navController
        )
    }
}

@Composable
private fun NoteListOrErrorMessage(
    getNotesUseCaseResponse: Response<List<Note>>,
    navController: NavController,
) {
    if (getNotesUseCaseResponse.message != null) {
        UnableToLoadNotesErrorMessage(getNotesUseCaseResponse.message)
    } else {
        NoNotesMessageOrNoteListWithSearchBar(
            getNotesUseCaseResponse, navController
        )
    }
}

@Composable
private fun UnableToLoadNotesErrorMessage(errorMessage: String) {
    ColumnWithCenteredContent {
        Text(text = errorMessage)
    }
}

@Composable
private fun NoNotesMessageOrNoteListWithSearchBar(
    getNotesUseCaseResponse: Response<List<Note>>,
    navController: NavController,
) {
    if (getNotesUseCaseResponse.data == null || getNotesUseCaseResponse.data?.size == 0) {
        NoNotesMessage()
    } else {
        NoteListWithSearchBar(getNotesUseCaseResponse, navController)
    }
}

@Composable
private fun NoNotesMessage() {
    ColumnWithCenteredContent {
        NoNotesIcon()
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun NoteListWithSearchBar(
    getNotesUseCaseResponse: Response<List<Note>>,
    navController: NavController,
) {
    val notesState = remember { mutableStateOf(getNotesUseCaseResponse.data ?: emptyList()) }
    val searchTextState = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 38.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        SearchBar(searchTextState)

        filterNotesBySearchTextValue(notesState, searchTextState, getNotesUseCaseResponse)

        LazyColumnOfNoteCards(notes = notesState.value, onNoteCardClicked = { note ->
            navigateToNoteDetailsScreen(navController, note.uuid)
        })
    }
}

private fun filterNotesBySearchTextValue(
    notes: MutableState<List<Note>>,
    searchState: MutableState<String>,
    getNotesUseCaseResponse: Response<List<Note>>,
) {
    notes.value = getNotesUseCaseResponse.data?.filter {
        it.title?.lowercase()?.contains(searchState.value.lowercase())!!
    } ?: emptyList()
}

private fun navigateToNoteDetailsScreen(navController: NavController, noteUUID: String) {
    navController.navigate(
        Screen.NoteDetailsScreen.withArgs(
            noteUUID, Constants.HOME_SCREEN_NOTES_TAB
        )
    )
}
