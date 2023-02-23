package com.example.progressupdatedemo.screens.details

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.progressupdatedemo.components.buttons.AppButton
import com.example.progressupdatedemo.components.customTopBar.CenterTopAppBar
import com.example.progressupdatedemo.models.Note
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.utils.Constants
import com.example.progressupdatedemo.utils.timestampToFormattedDate
import com.example.progressupdatedemo.utils.toJson

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NoteDetailsScreen(
    navController: NavController,
    note: Note,
    noteDetailsViewModel: NoteDetailsViewModel = hiltViewModel(),
    selectedHomeScreenTab: String,
) {
    val updatedNote = remember {
        mutableStateOf(note)
    }
    val showDeleteAlertDialog = remember {
        mutableStateOf(false)
    }
    val heartIcon =
        if (updatedNote.value.isFavourite) R.drawable.heart else R.drawable.heart_outline

    Scaffold(topBar = {
        NoteDetailsScreenTopBar(navController, selectedHomeScreenTab)
    }, bottomBar = {
        NoteDetailsScreenBottomBar(
            updatedNote,
            navController,
            selectedHomeScreenTab,
            noteDetailsViewModel,
            note,
            heartIcon,
            showDeleteAlertDialog
        )
    }) {
        NoteDetailsScreenContent(
            navController = navController,
            note = updatedNote.value,
            showDeleteAlertDialog = showDeleteAlertDialog,
            noteDetailsViewModel = noteDetailsViewModel,
            selectedHomeScreenTab = selectedHomeScreenTab
        )
    }
}

@Composable
private fun NoteDetailsScreenBottomBar(
    updatedNote: MutableState<Note>,
    navController: NavController,
    selectedHomeScreenTab: String,
    noteDetailsViewModel: NoteDetailsViewModel,
    note: Note,
    heartIcon: Int,
    showDeleteAlertDialog: MutableState<Boolean>,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        EditIconButton(updatedNote, navController, selectedHomeScreenTab)
        HeartIconButton(updatedNote, selectedHomeScreenTab, noteDetailsViewModel, note, heartIcon)
        DeleteIconButton(showDeleteAlertDialog)
    }
}

@Composable
private fun EditIconButton(
    updatedNote: MutableState<Note>,
    navController: NavController,
    selectedHomeScreenTab: String,
) {
    IconButton(onClick = {
        val jsonNote = updatedNote.value.toJson().toString()
        navigateToUpdateScreen(navController, jsonNote, selectedHomeScreenTab)
    }) {
        PencilIcon()
    }
}

private fun navigateToUpdateScreen(
    navController: NavController,
    jsonNote: String,
    selectedHomeScreenTab: String,
) {
    navController.navigate(Screen.UpdateNoteScreen.withArgs(jsonNote, selectedHomeScreenTab))
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
    updatedNote: MutableState<Note>,
    selectedHomeScreenTab: String,
    noteDetailsViewModel: NoteDetailsViewModel,
    note: Note,
    heartIcon: Int,
) {
    IconButton(onClick = {
        if (selectedHomeScreenTab == Constants.HOME_SCREEN_NOTES_TAB && !updatedNote.value.isFavourite) {
            toggleIsFavouriteStatus(updatedNote, note, noteDetailsViewModel)
        } else if (selectedHomeScreenTab == Constants.HOME_SCREEN_FAVOURITES_TAB && updatedNote.value.isFavourite) {
            toggleIsFavouriteStatus(updatedNote, note, noteDetailsViewModel)
        }
    }) {
        HeartIcon(heartIcon)
    }
}

private fun toggleIsFavouriteStatus(
    updatedNote: MutableState<Note>,
    note: Note,
    noteDetailsViewModel: NoteDetailsViewModel,
) {
    updatedNote.value = note.copy(isFavourite = !note.isFavourite)
    noteDetailsViewModel.toggleNoteFavouriteStatus(note, updatedNote.value)
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
private fun NoteDetailsScreenTopBar(
    navController: NavController,
    selectedHomeScreenTab: String,
) {
    CenterTopAppBar(title = {
        TopBarTitle()
    }, navigationIcon = {
        BackArrowIconButton(navController, selectedHomeScreenTab)
    })
}

@Composable
private fun BackArrowIconButton(
    navController: NavController,
    selectedHomeScreenTab: String,
) {
    IconButton(onClick = {
        navigateToHomeScreenWithSelectedTab(navController, selectedHomeScreenTab)
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

@Composable
private fun TopBarTitle() {
    Text(text = "Details", fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 18.sp)
}

@Composable
fun NoteDetailsScreenContent(
    navController: NavController,
    note: Note,
    showDeleteAlertDialog: MutableState<Boolean>,
    noteDetailsViewModel: NoteDetailsViewModel,
    selectedHomeScreenTab: String,
) {

    val context = LocalContext.current
    val isNoteDetailsViewModelProcessingNoteDeletion = noteDetailsViewModel.isProcessingNoteDeletion

    if (showDeleteAlertDialog.value) {
        NoteDetailsDeleteAlertDialog(
            showDeleteAlertDialog,
            isNoteDetailsViewModelProcessingNoteDeletion,
            note,
            noteDetailsViewModel,
            context,
            navController,
            selectedHomeScreenTab
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        NoteTitle(note)
        Spacer(modifier = Modifier.height(4.dp))
        NoteCreationDate(note)
        Spacer(modifier = Modifier.height(10.dp))
        ScrollableNoteMessage(note, Modifier.weight(1f, fill = false))
        Spacer(modifier = Modifier.height(25.dp))
    }
}

@Composable
private fun NoteTitle(note: Note) {
    Text(text = note.title.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun NoteDetailsDeleteAlertDialog(
    showDeleteAlertDialog: MutableState<Boolean>,
    isLoading: MutableState<Boolean>,
    note: Note,
    noteDetailsViewModel: NoteDetailsViewModel,
    context: Context,
    navController: NavController,
    fromTab: String?,
) {
    DeleteAlertDialog(
        showAlertDialog = showDeleteAlertDialog,
        isLoading = isLoading,
        currentNote = note,
    ) {
        noteDetailsViewModel.deleteNote(note, onFailure = {
            showErrorDeletingNoteError(context)
        }) {
            showDeleteAlertDialog.value = false
            navigateToHomeScreenWithOriginTabSelected(navController, fromTab)
        }
    }
}

@Composable
private fun ScrollableNoteMessage(note: Note, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp), text = note.message.toString()
        )
    }
}

@Composable
private fun NoteCreationDate(note: Note) {
    Text(
        text = timestampToFormattedDate(note.creationDate!!),
        fontSize = 16.sp,
        color = Color.Gray.copy(alpha = 0.7f)
    )
}

private fun navigateToHomeScreenWithOriginTabSelected(
    navController: NavController,
    fromTab: String?,
) {
    navigateToHomeScreenWithSelectedTab(navController, fromTab!!)
}

private fun navigateToHomeScreenWithSelectedTab(
    navController: NavController,
    selectedHomeScreenTab: String,
) {
    navController.navigate(Screen.HomeScreen.withArgs(selectedHomeScreenTab))
}

private fun showErrorDeletingNoteError(context: Context) {
    Toast.makeText(context, "Error deleting note", Toast.LENGTH_LONG).show()
}

@Composable
fun DeleteIconButton(
    showAlertDialog: MutableState<Boolean>,
) {
    IconButton(onClick = {
        showAlertDialog.value = true
    }) {
        DeleteIcon()
    }
}

@Composable
private fun DeleteIcon() {
    Icon(
        painter = painterResource(id = R.drawable.delete),
        contentDescription = "Delete icon",
        tint = Color.Black.copy(alpha = 0.6f)
    )
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun DeleteAlertDialog(
    showAlertDialog: MutableState<Boolean>,
    currentNote: Note,
    isLoading: MutableState<Boolean>,
    onDelete: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AlertDialog(onDismissRequest = {
            showAlertDialog.value = false
        }, title = {
            DeleteAlertDialogTitle()
        }, text = {
            DeleteAlertDialogMessage(currentNote)
        }, confirmButton = {
            DeleteAlertDialogConfirmButton(isLoading, onDelete)
        }, modifier = Modifier.height(170.dp), dismissButton = {
            DeleteAlertDialogDismissButton(showAlertDialog)
        })
    }
}

@Composable
private fun DeleteAlertDialogDismissButton(showAlertDialog: MutableState<Boolean>) {
    AppButton(modifier = Modifier.width(95.dp), text = "Cancel") {
        showAlertDialog.value = false
    }
}

@Composable
private fun DeleteAlertDialogConfirmButton(
    isLoading: MutableState<Boolean>,
    onDelete: () -> Unit,
) {
    AppButton(
        modifier = Modifier.width(95.dp), isLoading = isLoading.value, text = "Delete"
    ) {
        onDelete.invoke()
    }
}

@Composable
private fun DeleteAlertDialogMessage(currentNote: Note) {
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
            append(currentNote.title.toString())
        }
        append("?")
    })
}

@Composable
private fun DeleteAlertDialogTitle() {
    Row(
        horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.alert), contentDescription = "Alert icon"
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "Delete Note", fontSize = 16.sp)
    }
}