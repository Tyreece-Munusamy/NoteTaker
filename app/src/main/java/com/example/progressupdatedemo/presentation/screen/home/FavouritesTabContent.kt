package com.example.progressupdatedemo.presentation.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import com.example.progressupdatedemo.presentation.component.icons.NoFavouritesIcon
import com.example.progressupdatedemo.presentation.component.loading_animation.CircularLoadingAnimation
import com.example.progressupdatedemo.presentation.component.textfield.SearchBar

@Composable
fun FavouritesTabContent(
    navController: NavController,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val isLoadingUserData = remember(homeScreenViewModel.getNotesUseCaseResponseState.value) {
        homeScreenViewModel.getNotesUseCaseResponseState.value is Response.Loading
    }
    val getNotesUseCaseResponse = homeScreenViewModel.getNotesUseCaseResponseState.value

    if (isLoadingUserData) {
        CircularLoadingAnimation()
    } else {
        FavouritesListOrErrorMessage(
            getNotesUseCaseResponse, navController
        )
    }
}

@Composable
fun FavouritesListOrErrorMessage(
    getNotesUseCaseResponse: Response<List<Note>>,
    navController: NavController,
) {
    if (getNotesUseCaseResponse.message != null) {
        UnableToLoadNotesErrorMessage(getNotesUseCaseResponse.message)
    } else {
        NoFavouritesMessageOrFavouritesList(
            getNotesUseCaseResponse, navController
        )
    }
}

@Composable
private fun UnableToLoadNotesErrorMessage(errorMessage: String?) {
    ColumnWithCenteredContent {
        Text(errorMessage ?: "Error loading favourites")
    }
}

@Composable
private fun NoFavouritesMessageOrFavouritesList(
    getNotesUseCaseResponse: Response<List<Note>>,
    navController: NavController,
) {
    if (getNotesUseCaseResponse.data == null ||
        getNotesUseCaseResponse.data?.size == 0 ||
        filterNotesByFavourites(getNotesUseCaseResponse.data!!).isEmpty()
    ) {
        NoFavouriteNotesMessage()
    } else {
        val favourites = filterNotesByFavourites(getNotesUseCaseResponse.data!!)
        FavouritesListWithSearchBar(
            favourites, navController
        )
    }
}

private fun filterNotesByFavourites(notes: List<Note>) = notes.filter { note -> note.isFavourite }

@Composable
private fun NoFavouriteNotesMessage() {
    ColumnWithCenteredContent {
        NoFavouritesIcon()
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun FavouritesListWithSearchBar(
    favourites: List<Note>,
    navController: NavController,
) {
    val favouritesListState = remember { mutableStateOf(favourites) }
    val searchTextState = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 38.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        SearchBar(searchTextState)

        filterFavouritesBySearchTextValue(
            favouritesListState, searchTextState, favourites
        )

        LazyColumnOfNoteCards(notes = favouritesListState.value, onNoteCardClicked = { note ->
            navigateToNoteDetailsScreen(navController, note.uuid)
        })
    }
}

private fun filterFavouritesBySearchTextValue(
    favouriteNotesListState: MutableState<List<Note>>,
    searchState: MutableState<String>,
    favourites: List<Note>,
) {
    favouriteNotesListState.value = favourites.filter {
        it.title?.lowercase()?.contains(searchState.value.lowercase())!!
    }.ifEmpty { emptyList() }
}

private fun navigateToNoteDetailsScreen(navController: NavController, noteUUID: String) {
    navController.navigate(
        Screen.NoteDetailsScreen.withArgs(
            noteUUID, Constants.HOME_SCREEN_FAVOURITES_TAB
        )
    )
}