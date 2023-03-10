package com.example.progressupdatedemo.presentation.screen.details

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.progressupdatedemo.R
import com.example.progressupdatedemo.core.constants.Constants
import com.example.progressupdatedemo.core.utils.formatTimestamp
import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.model.Note
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.presentation.component.alert_dialog.AppAlertDialog
import com.example.progressupdatedemo.presentation.component.column.ColumnWithCenteredContent
import com.example.progressupdatedemo.presentation.component.loading_animation.CircularLoadingAnimation
import com.example.progressupdatedemo.presentation.component.top_bar.CenteredTextTopAppBar
import com.google.firebase.Timestamp
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NoteDetailsScreen(
    navController: NavController,
    noteUUID: String,
    previousHomeScreenTab: String,
    noteDetailsViewModel: NoteDetailsViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) { noteDetailsViewModel.getNoteByUUID(noteUUID) }
    val getNoteUseCaseResponse = noteDetailsViewModel.getNoteUseCaseResponseState.value
    val isLoadingNote = remember(noteDetailsViewModel.getNoteUseCaseResponseState.value) {
        noteDetailsViewModel.getNoteUseCaseResponseState.value is Response.Loading
    }

    OnBackPressedConfiguration(navController, previousHomeScreenTab)

    if (isLoadingNote) {
        CircularLoadingAnimation()
    } else {
        ErrorFetchingNoteMessageOrNoteDetailsScreenScaffold(
            getNoteUseCaseResponse, navController, previousHomeScreenTab, noteDetailsViewModel
        )
    }
}

@Composable
private fun OnBackPressedConfiguration(
    navController: NavController,
    previousHomeScreenTab: String,
) {
    BackHandler(true) {
        navigateToHomeScreenWithPreviousTabSelected(navController, previousHomeScreenTab)
    }
}

private fun navigateToHomeScreenWithPreviousTabSelected(
    navController: NavController,
    selectedHomeScreenTab: String,
) {
    navController.navigate(Screen.HomeScreen.withArgs(selectedHomeScreenTab))
}

@Composable
private fun ErrorFetchingNoteMessageOrNoteDetailsScreenScaffold(
    getNoteUseCaseResponse: Response<Note>,
    navController: NavController,
    previousHomeScreenTab: String,
    noteDetailsViewModel: NoteDetailsViewModel,
) {
    if (getNoteUseCaseResponse.message != null || getNoteUseCaseResponse.data == null) {
        ErrorFetchingNoteDetailsMessage(getNoteUseCaseResponse.message)
    } else {
        val note = getNoteUseCaseResponse.data!!
        NoteDetailsScreenScaffold(
            note,
            navController,
            previousHomeScreenTab,
            noteDetailsViewModel,
        )
    }
}

@Composable
private fun ErrorFetchingNoteDetailsMessage(message: String?) {
    ColumnWithCenteredContent {
        Text(text = message ?: "Error fetching note details")
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun NoteDetailsScreenScaffold(
    note: Note,
    navController: NavController,
    previousHomeScreenTab: String,
    noteDetailsViewModel: NoteDetailsViewModel,
) {
    val updatedNoteState = remember { mutableStateOf(note) }
    val showDeleteAlertDialogState = remember { mutableStateOf(false) }
    val onUpdateNote: () -> Unit = {
        updateNoteIfEligible(previousHomeScreenTab, updatedNoteState, note, noteDetailsViewModel)
    }
    val onDeleteNote: () -> Unit = {
        noteDetailsViewModel.deleteNote(note)
        navigateToHomeScreenWithPreviousTabSelected(navController, previousHomeScreenTab)
    }

    Scaffold(topBar = {
        NoteDetailsScreenTopBar(navController, previousHomeScreenTab)
    }, bottomBar = {
        NoteDetailsScreenBottomBar(
            updatedNoteState.value,
            navController,
            previousHomeScreenTab,
            showDeleteAlertDialogState,
            onUpdateNote,
        )
    }) {
        NoteDetailsScreenContent(
            note = updatedNoteState.value,
            showDeleteAlertDialogState = showDeleteAlertDialogState,
            noteDetailsViewModel = noteDetailsViewModel,
            onDeleteNote = onDeleteNote
        )
    }
}

@Composable
private fun NoteDetailsScreenTopBar(
    navController: NavController,
    selectedHomeScreenTab: String,
) {
    CenteredTextTopAppBar(title = {
        NoteDetailsScreenTopBarTitle()
    }, navigationIcon = {
        BackArrowIconButton(navController, selectedHomeScreenTab)
    })
}

@Composable
private fun NoteDetailsScreenTopBarTitle() {
    Text(text = "Details", fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 18.sp)
}

@Composable
private fun BackArrowIconButton(
    navController: NavController,
    selectedHomeScreenTab: String,
) {
    IconButton(onClick = {
        navigateToHomeScreenWithPreviousTabSelected(navController, selectedHomeScreenTab)
    }) {
        BackArrowIcon()
    }
}

@Composable
private fun BackArrowIcon() {
    Icon(
        imageVector = Icons.Default.ArrowBack, contentDescription = "Back arrow", tint = Color.White
    )
}

private fun updateNoteIfEligible(
    selectedHomeScreenTab: String,
    updatedNoteState: MutableState<Note>,
    note: Note,
    noteDetailsViewModel: NoteDetailsViewModel,
) {
    if (selectedHomeScreenTab == Constants.HOME_SCREEN_NOTES_TAB && !updatedNoteState.value.isFavourite) {
        toggleNoteIsFavouriteStatus(updatedNoteState, note, noteDetailsViewModel)
    } else if (selectedHomeScreenTab == Constants.HOME_SCREEN_FAVOURITES_TAB && updatedNoteState.value.isFavourite) {
        toggleNoteIsFavouriteStatus(updatedNoteState, note, noteDetailsViewModel)
    }
}

private fun toggleNoteIsFavouriteStatus(
    updatedNoteState: MutableState<Note>,
    note: Note,
    noteDetailsViewModel: NoteDetailsViewModel,
) {
    updatedNoteState.value = note.copy(isFavourite = !note.isFavourite)
    noteDetailsViewModel.toggleNoteFavouriteStatus(updatedNoteState.value)
}

@Composable
private fun NoteDetailsScreenBottomBar(
    note: Note,
    navController: NavController,
    previousHomeScreenTab: String,
    showDeleteAlertDialogState: MutableState<Boolean>,
    updateNote: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val heartIcon = if (note.isFavourite) R.drawable.heart else R.drawable.heart_outline

        EditIconButton(note, navController, previousHomeScreenTab)
        HeartIconButton(heartIcon, updateNote)
        DeleteIconButton(showDeleteAlertDialogState)
    }
}

@Composable
private fun EditIconButton(
    note: Note,
    navController: NavController,
    previousHomeScreenTab: String,
) {
    IconButton(onClick = {
        navigateToUpdateNoteScreen(note.uuid, navController, previousHomeScreenTab)
    }) {
        PencilIcon()
    }
}

private fun navigateToUpdateNoteScreen(
    noteUUID: String,
    navController: NavController,
    previousHomeScreenTab: String,
) {
    navController.navigate(
        Screen.UpdateNoteScreen.withArgs(
            noteUUID, previousHomeScreenTab
        )
    )
}

@Composable
private fun PencilIcon() {
    Icon(
        painter = painterResource(id = R.drawable.pencil),
        contentDescription = "Pencil icon",
        tint = Color.Black.copy(alpha = 0.6f)
    )
}

@Composable
private fun HeartIconButton(
    heartIcon: Int,
    onUpdateNote: () -> Unit,
) {
    IconButton(onClick = { onUpdateNote.invoke() }) {
        HeartIcon(heartIcon)
    }
}

@Composable
private fun HeartIcon(heartIcon: Int) {
    Icon(
        painter = painterResource(id = heartIcon),
        contentDescription = "Heart icon",
        tint = Color.Black.copy(alpha = 0.6f)
    )
}

@Composable
private fun DeleteIconButton(showAlertDialogState: MutableState<Boolean>) {
    IconButton(onClick = { showAlertDialogState.value = true }) { DeleteIcon() }
}

@Composable
private fun DeleteIcon() {
    Icon(
        painter = painterResource(id = R.drawable.delete),
        contentDescription = "Delete icon",
        tint = Color.Black.copy(alpha = 0.6f)
    )
}

@Composable
private fun NoteDetailsScreenContent(
    note: Note,
    showDeleteAlertDialogState: MutableState<Boolean>,
    noteDetailsViewModel: NoteDetailsViewModel,
    onDeleteNote: () -> Unit,
) {
    val isProcessingNoteDeletion = noteDetailsViewModel.isProcessingNoteDeletion.value

    if (showDeleteAlertDialogState.value) {
        DeleteNoteAlertDialog(
            showDeleteAlertDialogState, isProcessingNoteDeletion, note, onDeleteNote
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        NoteTitleText(note.title!!)

        Spacer(Modifier.height(4.dp))

        NoteCreationDateText(note.creationDate!!)

        Spacer(Modifier.height(10.dp))

        ScrollableNoteMessage(note.message!!, Modifier.weight(1f, fill = false))

        Spacer(Modifier.height(25.dp))
    }
}

@Composable
private fun DeleteNoteAlertDialog(
    showDeleteAlertDialogState: MutableState<Boolean>,
    isLoading: Boolean,
    note: Note,
    onDeleteNote: () -> Unit,
) {
    AppAlertDialog(alertDialogState = showDeleteAlertDialogState,
        isLoading = isLoading,
        title = "Delete Note",
        message = { DeleteAlertDialogMessage(note) },
        confirmButtonText = "Delete",
        onConfirmButtonClicked = { onDeleteNote.invoke() },
        dismissButtonText = "Cancel",
        onDismissButtonClicked = { showDeleteAlertDialogState.value = false })
}

@Composable
private fun DeleteAlertDialogMessage(note: Note) {
    Text(buildAnnotatedString {
        withStyle(
            SpanStyle(
                fontWeight = FontWeight.Normal
            )
        ) {
            append("Are you sure you want to delete ")
        }
        withStyle(
            SpanStyle(
                fontWeight = FontWeight.Bold
            )
        ) {
            append(note.title.toString())
        }
        append("?")
    })
}

@Composable
private fun NoteTitleText(noteTitle: String) {
    Text(text = noteTitle, fontSize = 22.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun NoteCreationDateText(noteCreationDateTimestamp: Timestamp) {
    Text(
        text = formatTimestamp(noteCreationDateTimestamp),
        fontSize = 16.sp,
        color = Color.Gray.copy(alpha = 0.7f)
    )
}

@Composable
private fun ScrollableNoteMessage(noteMessage: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp), text = noteMessage
        )
    }
}

