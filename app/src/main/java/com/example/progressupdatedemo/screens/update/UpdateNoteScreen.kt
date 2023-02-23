package com.example.progressupdatedemo.screens.update

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.progressupdatedemo.R
import com.example.progressupdatedemo.components.buttons.AppButton
import com.example.progressupdatedemo.components.buttons.NegativePositiveButtonRow
import com.example.progressupdatedemo.components.customTopBar.CenterTopAppBar
import com.example.progressupdatedemo.models.Note
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.utils.toJson

@Composable
fun UpdateNoteScreen(
    navController: NavController,
    note: Note,
    updateScreenViewModel: UpdateScreenViewModel = hiltViewModel(),
    selectedHomeScreenTab: String,
) {
    val title = remember {
        mutableStateOf(note.title.toString())
    }

    val messageTextFieldValue = remember {
        mutableStateOf(
            TextFieldValue(
                text = note.message.toString(), selection = TextRange(0)
            )
        )
    }
    val isUserInputUpdated = remember(title.value, messageTextFieldValue.value.text) {
        title.value != note.title || messageTextFieldValue.value.text != note.message
    }
    val showUnsavedChangesAlertDialog = remember {
        mutableStateOf(false)
    }

    OnBackPressedConfiguration(showUnsavedChangesAlertDialog, isUserInputUpdated, navController)
    UpdateNoteScreenScaffold(
        navController,
        note,
        updateScreenViewModel,
        selectedHomeScreenTab,
        title,
        messageTextFieldValue,
        showUnsavedChangesAlertDialog,
        isUserInputUpdated
    )
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun UpdateNoteScreenScaffold(
    navController: NavController,
    note: Note,
    updateScreenViewModel: UpdateScreenViewModel,
    selectedHomeScreenTab: String,
    title: MutableState<String>,
    messageTextFieldValue: MutableState<TextFieldValue>,
    showUnsavedChangesAlertDialog: MutableState<Boolean>,
    isUserInputUpdated: Boolean,
) {
    Scaffold(topBar = {
        UpdateNoteScreenTopBar(navController, showUnsavedChangesAlertDialog, isUserInputUpdated)
    }) {
        UpdateNoteScreenContent(
            navController,
            note,
            updateScreenViewModel,
            selectedHomeScreenTab,
            showUnsavedChangesAlertDialog,
            title,
            messageTextFieldValue,
            isUserInputUpdated
        )
    }
}

@Composable
private fun UpdateNoteScreenTopBar(
    navController: NavController,
    showUnsavedChangesAlertDialog: MutableState<Boolean>,
    isUserInputUpdated: Boolean,
) {
    CenterTopAppBar(title = {
        TopBarTitle()
    }, navigationIcon = {
        BackArrowIconButton(isUserInputUpdated, showUnsavedChangesAlertDialog, navController)
    })
}

@Composable
private fun BackArrowIconButton(
    isUserInputUpdated: Boolean,
    showUnsavedChangesAlertDialog: MutableState<Boolean>,
    navController: NavController,
) {
    IconButton(onClick = {
        if (isUserInputUpdated) {
            showUnsavedChangesAlertDialog.value = true
        } else {
            navController.popBackStack()
        }
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
    Text(
        text = "Edit Note", fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 18.sp
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UpdateNoteScreenContent(
    navController: NavController,
    note: Note,
    updateScreenViewModel: UpdateScreenViewModel,
    selectedHomeScreenTab: String,
    showUnsavedChangesAlertDialog: MutableState<Boolean>,
    title: MutableState<String>,
    messageTextFieldValue: MutableState<TextFieldValue>,
    isUserInputUpdated: Boolean,
) {
    val isProcessingUpdateRequest = updateScreenViewModel.isProcessingUpdateRequest
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val emailFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        emailFocusRequester.requestFocus()
        messageTextFieldValue.value = TextFieldValue(
            text = note.message.toString(), selection = TextRange(note.message!!.length)
        )
    }

    if (showUnsavedChangesAlertDialog.value) {
        UnsavedChangedAlert(
            showUnsavedChangesAlertDialog,
            isProcessingUpdateRequest,
            navController,
            isUserInputUpdated,
            note,
            title,
            messageTextFieldValue,
            updateScreenViewModel,
            context,
            selectedHomeScreenTab
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TitleInputField(title)
        MessageInputField(emailFocusRequester, messageTextFieldValue, keyboardController)
        Spacer(modifier = Modifier.height(20.dp))
        DiscardAndSaveButtonRow(
            note,
            navController,
            selectedHomeScreenTab,
            isUserInputUpdated,
            title,
            messageTextFieldValue,
            updateScreenViewModel,
            context,
            isProcessingUpdateRequest
        )
    }
}

@Composable
private fun DiscardAndSaveButtonRow(
    note: Note,
    navController: NavController,
    selectedHomeScreenTab: String,
    isUserInputUpdated: Boolean,
    title: MutableState<String>,
    messageTextFieldValue: MutableState<TextFieldValue>,
    updateScreenViewModel: UpdateScreenViewModel,
    context: Context,
    isProcessingUpdateRequest: MutableState<Boolean>,
) {
    NegativePositiveButtonRow(negativeButtonLabel = "Discard", onNegativeButtonClicked = {
        val noteInJsonFormat = note.toJson().toString()
        navigateToDetailsScreen(navController, noteInJsonFormat, selectedHomeScreenTab)
    }, positiveButtonLabel = "Save", onPositiveButtonClicked = {
        if (isUserInputUpdated) {
            updateNote(
                note,
                title,
                messageTextFieldValue,
                updateScreenViewModel,
                context,
                navController,
                selectedHomeScreenTab
            )
        } else {
            val noteInJsonFormat = note.toJson().toString()
            navigateToDetailsScreen(navController, noteInJsonFormat, selectedHomeScreenTab)
        }
    }, isPositiveButtonLoading = isProcessingUpdateRequest
    )
}

private fun updateNote(
    note: Note,
    title: MutableState<String>,
    messageTextFieldValue: MutableState<TextFieldValue>,
    updateScreenViewModel: UpdateScreenViewModel,
    context: Context,
    navController: NavController,
    selectedHomeScreenTab: String,
) {
    val updateNote = note.copy(title = title.value, message = messageTextFieldValue.value.text)
    updateScreenViewModel.updateNote(note, updateNote, onFailure = {
        showFailedToSaveNoteToast(context)
    }) {
        val updatedNoteInJsonFormat = updateNote.toJson().toString()
        navigateToDetailsScreen(navController, updatedNoteInJsonFormat, selectedHomeScreenTab)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MessageInputField(
    emailFocusRequester: FocusRequester,
    messageTextFieldValue: MutableState<TextFieldValue>,
    keyboardController: SoftwareKeyboardController?,
) {
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
}

@Composable
private fun TitleInputField(title: MutableState<String>) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = title.value,
        onValueChange = { title.value = it },
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
private fun UnsavedChangedAlert(
    showUnsavedChangesAlertDialog: MutableState<Boolean>,
    isProcessingUpdateRequest: MutableState<Boolean>,
    navController: NavController,
    isUserInputUpdated: Boolean,
    note: Note,
    title: MutableState<String>,
    messageTextFieldValue: MutableState<TextFieldValue>,
    updateScreenViewModel: UpdateScreenViewModel,
    context: Context,
    selectedHomeScreenTab: String,
) {
    UpdateNoteScreenSaveChangesAlertDialog(showAlertDialog = showUnsavedChangesAlertDialog,
        isLoading = isProcessingUpdateRequest,
        onDiscard = {
            navController.popBackStack()
        }) {
        updateNote(
            isUserInputUpdated,
            note,
            title,
            messageTextFieldValue,
            updateScreenViewModel,
            context,
            navController,
            selectedHomeScreenTab
        )
    }
}

private fun updateNote(
    isUserInputUpdated: Boolean,
    note: Note,
    title: MutableState<String>,
    messageTextFieldValue: MutableState<TextFieldValue>,
    updateScreenViewModel: UpdateScreenViewModel,
    context: Context,
    navController: NavController,
    selectedHomeScreenTab: String,
) {
    if (isUserInputUpdated) {
        val updateNote = note.copy(
            title = title.value,
            message = messageTextFieldValue.value.text,
        )
        updateScreenViewModel.updateNote(note, updateNote, onFailure = {
            showFailedToSaveNoteToast(context)
        }) {
            val jsonNote = updateNote.toJson()
            navigateToDetailsScreen(navController, jsonNote!!, selectedHomeScreenTab)
        }
    } else {
        val jsonNote = note.toJson()
        navigateToDetailsScreen(navController, jsonNote!!, selectedHomeScreenTab)
    }
}

private fun showFailedToSaveNoteToast(context: Context) {
    Toast.makeText(context, "Failed to save note", Toast.LENGTH_LONG).show()
}

private fun navigateToDetailsScreen(
    navController: NavController,
    noteInJsonFormat: String,
    selectedHomeScreenTab: String,
) {
    navController.navigate(
        Screen.NoteDetailsScreen.withArgs(
            noteInJsonFormat, selectedHomeScreenTab
        )
    )
}

@Composable
fun OnBackPressedConfiguration(
    showUnsavedChangesAlertDialog: MutableState<Boolean>,
    isUserInputUpdated: Boolean,
    navController: NavController,
) {
    BackHandler(true) {
        if (isUserInputUpdated) {
            showUnsavedChangesAlertDialog.value = true
        } else {
            navController.popBackStack()
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun UpdateNoteScreenSaveChangesAlertDialog(
    showAlertDialog: MutableState<Boolean>,
    isLoading: MutableState<Boolean>,
    onDiscard: () -> Unit,
    onSave: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AlertDialog(onDismissRequest = {
            showAlertDialog.value = false
        }, title = {
            SaveNoteAlertDialogTitle()
        }, text = {
            Text(text = "Save note?")
        }, confirmButton = {
            SaveNoteDialogConfirmButton(isLoading, onSave)
        }, modifier = Modifier.height(170.dp), dismissButton = {
            SaveNoteAlertDialogDismissButton(onDiscard)
        })
    }
}

@Composable
private fun SaveNoteDialogConfirmButton(
    isLoading: MutableState<Boolean>,
    onSave: () -> Unit,
) {
    AppButton(
        modifier = Modifier.width(95.dp), isLoading = isLoading.value, text = "Save"
    ) {
        onSave.invoke()
    }
}

@Composable
private fun SaveNoteAlertDialogDismissButton(onDiscard: () -> Unit) {
    AppButton(modifier = Modifier.width(95.dp), text = "Discard") {
        onDiscard.invoke()
    }
}

@Composable
private fun SaveNoteAlertDialogTitle() {
    Row(
        horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.alert), contentDescription = "Alert icon"
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = " Unsaved changes", fontSize = 16.sp)
    }
}