package com.example.progressupdatedemo.screens.update

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.progressupdatedemo.R
import com.example.progressupdatedemo.components.CenterTopAppBar
import com.example.progressupdatedemo.components.buttons.AppButton
import com.example.progressupdatedemo.models.Note
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.utils.toJson
import com.google.firebase.Timestamp

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditNoteScreen(
    navController: NavController = NavController(LocalContext.current),
    note: Note = Note(
        title = "Progress Update",
        creationDate = Timestamp.now(),
        message = "Schedule a meeting with the android team to present my application"
    ),
    updateScreenViewModel: UpdateScreenViewModel = hiltViewModel(),
    fromTab: String,
) {
    Scaffold(topBar = {
        CenterTopAppBar(title = {
            Text(
                text = "Edit Note",
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = 18.sp
            )
        }, navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back arrow",
                    tint = Color.White
                )
            }
        })
    }) {
        EditNoteScreenContent(navController, note, updateScreenViewModel, fromTab)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditNoteScreenContent(
    navController: NavController,
    note: Note,
    updateScreenViewModel: UpdateScreenViewModel,
    fromTab: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        val titleState = remember {
            mutableStateOf(note.title.toString())
        }

        val messageTextFieldValue = remember {
            mutableStateOf(
                TextFieldValue(
                    text = note.message.toString(), selection = TextRange(0)
                )
            )
        }

        val userInputIsUpdated = remember(titleState.value, messageTextFieldValue.value.text) {
            titleState.value != note.title || messageTextFieldValue.value.text != note.message
        }

        val isLoading = remember {
            mutableStateOf(false)
        }
        val context = LocalContext.current

        val keyboardController = LocalSoftwareKeyboardController.current
        val emailFocusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            emailFocusRequester.requestFocus()
            messageTextFieldValue.value = TextFieldValue(
                text = note.message.toString(), selection = TextRange(note.message!!.length)
            )
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = titleState.value,
            onValueChange = { titleState.value = it },
            label = {
                Text(text = "Title")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions.Default
        )

        OutlinedTextField(
            modifier = Modifier
                .focusRequester(emailFocusRequester)
                .fillMaxWidth()
                .height(325.dp),
            value = messageTextFieldValue.value,
            onValueChange = {
                messageTextFieldValue.value = it
            },
            label = {
                Text(text = "Message")
            },
            singleLine = false,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions {
                keyboardController?.hide()

            },
            maxLines = 15
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row {

            AppButton(
                modifier = Modifier
                    .weight(1f, true)
                    .height(46.dp),
                text = "Save",
                isLoading = isLoading.value
            ) {
                if (userInputIsUpdated) {
                    isLoading.value = true
                    val updateNote = Note(
                        id = note.id,
                        title = titleState.value,
                        message = messageTextFieldValue.value.text,
                        isFavourite = note.isFavourite,
                        creationDate = note.creationDate
                    )
                    updateScreenViewModel.updateNote(note, updateNote, onFailure = {
                        isLoading.value = false
                        Toast.makeText(context, "Failed to save note", Toast.LENGTH_LONG).show()
                    }) {
                        val jsonNote = updateNote.toJson()
                        isLoading.value = false
                        navController.navigate(Screen.NoteDetailsScreen.withArgs(jsonNote!!, fromTab))
                    }
                } else {
                    val jsonNote = note.toJson()
                    navController.navigate(Screen.NoteDetailsScreen.withArgs(jsonNote!!, fromTab))
                }
            }
            Spacer(modifier = Modifier.width(7.dp))
            AppButton(
                modifier = Modifier
                    .weight(1f, true)
                    .height(46.dp), text = "Cancel"
            ) {
                val jsonNote = note.toJson()
                navController.navigate(Screen.NoteDetailsScreen.withArgs(jsonNote!!, fromTab))
            }

        }
    }
}