package com.example.progressupdatedemo.screens.details

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
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
import com.example.progressupdatedemo.components.customTopBar.CenterTopAppBar
import com.example.progressupdatedemo.components.buttons.AppButton
import com.example.progressupdatedemo.models.Note
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.utils.timestampToFormattedDate
import com.example.progressupdatedemo.utils.toJson
import com.google.firebase.Timestamp

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NoteDetailsScreen(
    navController: NavController = NavController(LocalContext.current),
    note: Note = Note(
        title = "Progress Update",
        creationDate = Timestamp.now(),
        isFavourite = false,
        message = "Schedule a meeting with the android team to present my application"
    ),
    noteDetailsViewModel: NoteDetailsViewModel = hiltViewModel(),
    fromTab: String?,
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
        CenterTopAppBar(title = {
            Text(
                text = "Details",
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = 18.sp
            )
        }, navigationIcon = {
            IconButton(onClick = {
                navController.navigate(Screen.HomeScreen.withArgs(fromTab!!))
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back arrow",
                    tint = Color.White
                )
            }
        })
    }, bottomBar = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                val jsonNote = updatedNote.value.toJson()
                navController.navigate(Screen.UpdateNoteScreen.withArgs(jsonNote!!, fromTab!!))
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.pencil),
                    contentDescription = "Pencil icon",
                    tint = Color.Black.copy(alpha = 0.6f)
                )
            }
            IconButton(onClick = {
                if (!updatedNote.value.isFavourite && fromTab.equals("notes")) {
                    updatedNote.value = Note(
                        id = updatedNote.value.id,
                        title = updatedNote.value.title,
                        message = updatedNote.value.message,
                        isFavourite = !updatedNote.value.isFavourite,
                        creationDate = updatedNote.value.creationDate
                    )
                    noteDetailsViewModel.toggleNoteFavouriteStatus(note, updatedNote.value)
                } else if (updatedNote.value.isFavourite && fromTab.equals("favourites")) {
                    updatedNote.value = Note(
                        id = updatedNote.value.id,
                        title = updatedNote.value.title,
                        message = updatedNote.value.message,
                        isFavourite = !updatedNote.value.isFavourite,
                        creationDate = updatedNote.value.creationDate
                    )
                    noteDetailsViewModel.toggleNoteFavouriteStatus(note, updatedNote.value)
                }
            }) {
                Icon(
                    painter = painterResource(id = heartIcon),
                    contentDescription = "Heart icon",
                    tint = Color.Black.copy(alpha = 0.6f)
                )
            }
            DeleteIconButton(
                showAlertDialog = showDeleteAlertDialog,
            )
        }
    }) {
        NoteDetailsScreenContent(
            navController = navController,
            note = updatedNote.value,
            showAlertDialog = showDeleteAlertDialog,
            noteDetailsViewModel = noteDetailsViewModel,
            fromTab = fromTab
        )
    }
}

@Composable
fun NoteDetailsScreenContent(
    navController: NavController,
    note: Note,
    showAlertDialog: MutableState<Boolean>,
    noteDetailsViewModel: NoteDetailsViewModel,
    fromTab: String?,
) {

    val context = LocalContext.current
    val isLoading = remember {
        mutableStateOf(false)
    }

    if (showAlertDialog.value) {
        DeleteAlertDialog(
            showAlertDialog = showAlertDialog,
            isLoading = isLoading,
            currentNote = note,
        ) {
            isLoading.value = true
            noteDetailsViewModel.deleteNote(note, onFailure = {
                isLoading.value = false
                Toast.makeText(context, "Error deleting note", Toast.LENGTH_LONG).show()
            }) {
                isLoading.value = false
                showAlertDialog.value = false
                navController.navigate(Screen.HomeScreen.withArgs(fromTab!!))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = note.title.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = timestampToFormattedDate(note.creationDate!!),
            fontSize = 16.sp,
            color = Color.Gray.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp), text = note.message.toString()
        )
    }
}

@Composable
fun DeleteIconButton(
    showAlertDialog: MutableState<Boolean>,
) {
    IconButton(onClick = {
        showAlertDialog.value = true
    }) {
        Icon(
            painter = painterResource(id = R.drawable.delete),
            contentDescription = "Delete icon",
            tint = Color.Black.copy(alpha = 0.6f)
        )
    }
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
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.alert),
                    contentDescription = "Alert icon"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Delete Note", fontSize = 16.sp)
            }
        }, text = {
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
        }, confirmButton = {
            AppButton(
                modifier = Modifier.width(95.dp), isLoading = isLoading.value, text = "Delete"
            ) {
                onDelete.invoke()
            }
        }, modifier = Modifier.height(170.dp), dismissButton = {
            AppButton(modifier = Modifier.width(95.dp), text = "Cancel") {
                showAlertDialog.value = false
            }
        })
    }
}