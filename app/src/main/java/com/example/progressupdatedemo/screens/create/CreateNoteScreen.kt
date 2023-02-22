package com.example.progressupdatedemo.screens.create

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.progressupdatedemo.R
import com.example.progressupdatedemo.components.CenterTopAppBar
import com.example.progressupdatedemo.components.buttons.AppButton
import com.example.progressupdatedemo.models.Note
import com.example.progressupdatedemo.navigation.Screen
import com.google.firebase.Timestamp

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CreateNoteScreen(
    navController: NavController,
    createNoteScreenViewModel: CreateNoteScreenViewModel = hiltViewModel(),
) {
    val showSaveNoteAlertDialog = remember { mutableStateOf(false) }
    ShowSaveAlertDialogOnBackPressed(showSaveNoteAlertDialog)
    CreateNoteScreenScaffold(showSaveNoteAlertDialog, navController, createNoteScreenViewModel)
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun CreateNoteScreenScaffold(
    showSaveNoteAlertDialog: MutableState<Boolean>,
    navController: NavController,
    createNoteScreenViewModel: CreateNoteScreenViewModel,
) {
    Scaffold(topBar = {
        CreateNoteScreenTopBar(showSaveNoteAlertDialog)
    }) {
        CreateNoteScreenContent(navController, createNoteScreenViewModel, showSaveNoteAlertDialog)
    }
}

@Composable
private fun CreateNoteScreenTopBar(showSaveNoteAlertDialog: MutableState<Boolean>) {
    CenterTopAppBar(title = {
        TopBarTitle()
    }, navigationIcon = {
        BackArrowIconButton(showSaveNoteAlertDialog)
    })
}

@Composable
private fun BackArrowIconButton(showSaveNoteAlertDialog: MutableState<Boolean>) {
    IconButton(onClick = { showSaveNoteAlertDialog.value = true }) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back arrow",
            tint = Color.White
        )
    }
}

@Composable
private fun TopBarTitle() {
    Text(
        text = "Create Note",
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        fontSize = 18.sp
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateNoteScreenContent(
    navController: NavController,
    createNoteScreenViewModel: CreateNoteScreenViewModel,
    showSaveNoteAlertDialog: MutableState<Boolean>,
) {
    val titleState = remember {
        mutableStateOf("")
    }
    val messageState = remember {
        mutableStateOf("")
    }
    val isUserInputValid = remember(titleState.value, messageState.value) {
        titleState.value.trim().isNotEmpty() && messageState.value.trim().isNotEmpty()
    }
    val isLoading = remember {
        mutableStateOf(false)
    }
    val isAlertDialogLoading = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    if (showSaveNoteAlertDialog.value && isUserInputValid) {
        SaveNoteAlertDialog(
            showSaveNoteAlertDialog,
            isAlertDialogLoading,
            navController,
            createNoteScreenViewModel,
            titleState,
            messageState,
            context
        )
    } else if (showSaveNoteAlertDialog.value && !isUserInputValid) {
        navigateToHomeScreen(navController)
        showSaveNoteAlertDialog.value = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TitleTextField(titleState)

        MessageTextField(
            messageState,
            keyboardController,
            isUserInputValid,
            isLoading,
            createNoteScreenViewModel,
            titleState,
            context,
            navController
        )

        Spacer(modifier = Modifier.height(20.dp))

        CreateAndCancelButtonsRow(
            isLoading,
            isUserInputValid,
            createNoteScreenViewModel,
            titleState,
            messageState,
            context,
            navController
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MessageTextField(
    messageState: MutableState<String>,
    keyboardController: SoftwareKeyboardController?,
    isUserInputValid: Boolean,
    isLoading: MutableState<Boolean>,
    createNoteScreenViewModel: CreateNoteScreenViewModel,
    titleState: MutableState<String>,
    context: Context,
    navController: NavController,
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(325.dp),
        value = messageState.value,
        onValueChange = { messageState.value = it },
        label = {
            Text(text = "Message")
        },
        singleLine = false,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions {
            keyboardController?.hide()
            if (isUserInputValid) {
                isLoading.value = true
                createNoteScreenViewModel.createNote(Note(
                    title = titleState.value,
                    message = messageState.value,
                    creationDate = Timestamp.now()
                ), onFailure = {
                    isLoading.value = false
                    Toast.makeText(context, "Note creation failed", Toast.LENGTH_LONG).show()
                }) {
                    navigateToHomeScreen(navController)
                    isLoading.value = false
                }
            } else {
                Toast.makeText(context, "Please enter a title and message", Toast.LENGTH_LONG)
                    .show()
            }
        },
        maxLines = 15
    )
}

@Composable
private fun TitleTextField(titleState: MutableState<String>) {
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
}

@Composable
private fun CreateAndCancelButtonsRow(
    isLoading: MutableState<Boolean>,
    isUserInputValid: Boolean,
    createNoteScreenViewModel: CreateNoteScreenViewModel,
    titleState: MutableState<String>,
    messageState: MutableState<String>,
    context: Context,
    navController: NavController,
) {
    Row {
        AppButton(
            modifier = Modifier
                .weight(1f, true)
                .height(46.dp), text = "Cancel"
        ) {
            navController.navigate(Screen.HomeScreen.withArgs("notes"))
        }
        Spacer(modifier = Modifier.width(7.dp))
        AppButton(
            modifier = Modifier
                .weight(1f, true)
                .height(46.dp),
            text = "Create",
            isLoading = isLoading.value
        ) {
            if (isUserInputValid) {
                isLoading.value = true
                createNoteScreenViewModel.createNote(Note(
                    title = titleState.value,
                    message = messageState.value,
                    creationDate = Timestamp.now()
                ), onFailure = {
                    isLoading.value = false
                    Toast.makeText(context, "Note creation failed", Toast.LENGTH_LONG).show()
                }) {
                    navigateToHomeScreen(navController)
                    isLoading.value = false
                }
            } else {
                Toast.makeText(context, "Please enter a title and message", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}

@Composable
private fun SaveNoteAlertDialog(
    showSaveNoteAlertDialog: MutableState<Boolean>,
    isAlertDialogLoading: MutableState<Boolean>,
    navController: NavController,
    createNoteScreenViewModel: CreateNoteScreenViewModel,
    titleState: MutableState<String>,
    messageState: MutableState<String>,
    context: Context,
) {
    SaveNoteAlertDialog(
        showAlertDialog = showSaveNoteAlertDialog,
        isLoading = isAlertDialogLoading,
        onDiscard = {
            navigateToHomeScreen(navController)

        }) {
        isAlertDialogLoading.value = true
        createNoteScreenViewModel.createNote(Note(
            title = titleState.value,
            message = messageState.value,
            creationDate = Timestamp.now()
        ), onFailure = {
            Toast.makeText(context, "Note creation failed", Toast.LENGTH_LONG).show()
            isAlertDialogLoading.value = false
        }) {
            isAlertDialogLoading.value = false
            navigateToHomeScreen(navController)
        }
    }
}

@Composable
private fun ShowSaveAlertDialogOnBackPressed(showSaveNoteAlertDialog: MutableState<Boolean>) {
    BackHandler(true) {
        showSaveNoteAlertDialog.value = true
    }
}

private fun navigateToHomeScreen(navController: NavController) {
    navController.navigate(Screen.HomeScreen.withArgs("notes"))
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun SaveNoteAlertDialog(
    showAlertDialog: MutableState<Boolean>,
    isLoading: MutableState<Boolean>,
    onDiscard: () -> Unit,
    onSave: () -> Unit,
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
                Text(text = " Unsaved changes", fontSize = 16.sp)
            }
        }, text = {
            Text(text = "Save note?")
        }, confirmButton = {
            AppButton(
                modifier = Modifier.width(95.dp), isLoading = isLoading.value, text = "Save"
            ) {
                onSave.invoke()
            }
        }, modifier = Modifier.height(170.dp), dismissButton = {
            AppButton(modifier = Modifier.width(95.dp), text = "Discard") {
                onDiscard.invoke()
            }
        })
    }
}
