package com.example.progressupdatedemo.presentation.screen.create

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.progressupdatedemo.core.constants.Constants
import com.example.progressupdatedemo.core.utils.isValid
import com.example.progressupdatedemo.domain.model.Note
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.presentation.component.alert_dialog.AppAlertDialog
import com.example.progressupdatedemo.presentation.component.button.NegativePositiveButtonRow
import com.example.progressupdatedemo.presentation.component.top_bar.CenteredTextTopAppBar
import com.google.firebase.Timestamp

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CreateNoteScreen(
    navController: NavController,
    createNoteScreenViewModel: CreateNoteScreenViewModel = hiltViewModel(),
) {
    val showSaveNoteAlertDialogState = remember { mutableStateOf(false) }
    val messageState = remember { mutableStateOf("") }

    OnBackPressedConfiguration(messageState.value, showSaveNoteAlertDialogState, navController)

    CreateNoteScreenScaffold(
        showSaveNoteAlertDialogState,
        navController,
        createNoteScreenViewModel,
        messageState,
    )
}

@Composable
private fun OnBackPressedConfiguration(
    message: String,
    showSaveNoteAlertDialogState: MutableState<Boolean>,
    navController: NavController,
) {
    BackHandler(true) {
        if (message.isValid()) {
            showSaveNoteAlertDialogState.value = true
        } else {
            navigateToHomeScreenWithNotesTabSelected(navController)
        }
    }
}

private fun navigateToHomeScreenWithNotesTabSelected(navController: NavController) {
    navController.navigate(Screen.HomeScreen.withArgs(Constants.HOME_SCREEN_NOTES_TAB))
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun CreateNoteScreenScaffold(
    showSaveNoteAlertDialogState: MutableState<Boolean>,
    navController: NavController,
    createNoteScreenViewModel: CreateNoteScreenViewModel,
    messageState: MutableState<String>,
) {
    Scaffold(topBar = {
        CreateNoteScreenTopBar(showSaveNoteAlertDialogState, messageState.value, navController)
    }) {
        CreateNoteScreenContent(
            navController,
            createNoteScreenViewModel,
            showSaveNoteAlertDialogState,
            messageState,
        )
    }
}

@Composable
private fun CreateNoteScreenTopBar(
    showSaveNoteAlertDialog: MutableState<Boolean>,
    message: String,
    navController: NavController,
) {
    CenteredTextTopAppBar(title = {
        CreateNoteScreenTopBarTitle()
    }, navigationIcon = {
        BackArrowIconButton(showSaveNoteAlertDialog, message, navController)
    })
}

@Composable
private fun CreateNoteScreenTopBarTitle() {
    Text(
        text = "Create Note",
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        fontSize = 18.sp
    )
}

@Composable
private fun BackArrowIconButton(
    showSaveNoteAlertDialogState: MutableState<Boolean>,
    messageState: String,
    navController: NavController,
) {
    IconButton(onClick = {
        if (messageState.isValid()) {
            showSaveNoteAlertDialogState.value = true
        } else {
            navigateToHomeScreenWithNotesTabSelected(navController)
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
fun CreateNoteScreenContent(
    navController: NavController,
    createNoteScreenViewModel: CreateNoteScreenViewModel,
    showSaveNoteAlertDialogState: MutableState<Boolean>,
    messageState: MutableState<String>,
) {
    val titleState = remember { mutableStateOf("") }
    val isUserInputValid = remember(titleState.value, messageState.value) {
        createNoteScreenViewModel.validateNoteDetails(titleState.value, messageState.value)
    }
    val isProcessingNoteCreation = createNoteScreenViewModel.isProcessingNoteCreation.value
    val onCreate: () -> Unit = {
        createNote(createNoteScreenViewModel, titleState.value, messageState.value)
        showSaveNoteAlertDialogState.value = false
        navigateToHomeScreenWithNotesTabSelected(navController)
    }
    val onDiscard: () -> Unit = {
        navigateToHomeScreenWithNotesTabSelected(navController)
    }

    if (showSaveNoteAlertDialogState.value) {
        titleState.value = titleState.value.ifEmpty { "Untitled" }
        SaveNoteAlertDialog(
            showSaveNoteAlertDialogState, isProcessingNoteCreation, onCreate, onDiscard
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TitleInputField(titleState)

        MessageInputField(messageState)

        Spacer(modifier = Modifier.height(20.dp))

        CancelAndCreateButtonRow(
            isProcessingNoteCreation, isUserInputValid, onCreate, onDiscard
        )
    }
}

private fun createNote(
    createNoteScreenViewModel: CreateNoteScreenViewModel,
    title: String,
    message: String,
) {
    createNoteScreenViewModel.createNote(
        Note(title = title, message = message, creationDate = Timestamp.now())
    )
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun SaveNoteAlertDialog(
    showAlertDialogState: MutableState<Boolean>,
    isLoading: Boolean,
    onCreate: () -> Unit,
    onDiscard: () -> Unit,
) {
    AppAlertDialog(
        showAlertDialogState,
        isLoading,
        "Create Note",
        { Text(text = "Exit without saving note?") },
        "Save",
        onCreate,
        "Discard",
        onDiscard
    )
}

@Composable
private fun TitleInputField(titleState: MutableState<String>) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = titleState.value,
        onValueChange = { titleState.value = it },
        label = { TitleInputFieldLabel() },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
        )
    )
}

@Composable
private fun TitleInputFieldLabel() {
    Text(text = "Title")
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MessageInputField(
    messageState: MutableState<String>,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(325.dp),
        value = messageState.value,
        onValueChange = { messageState.value = it },
        label = { MessageInputFieldLabel() },
        singleLine = false,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions {
            keyboardController?.hide()
            KeyboardActions.Default
        },
        maxLines = 15
    )
}

@Composable
private fun MessageInputFieldLabel() {
    Text(text = "Message")
}

@Composable
private fun CancelAndCreateButtonRow(
    isLoading: Boolean,
    isUserInputValid: Boolean,
    onCreate: () -> Unit,
    onDiscard: () -> Unit,
) {
    val context = LocalContext.current

    NegativePositiveButtonRow(
        negativeButtonLabel = "Cancel",
        onNegativeButtonClicked = { onDiscard.invoke() },
        positiveButtonLabel = "Create",
        onPositiveButtonClicked = {
            if (isUserInputValid) {
                onCreate.invoke()
            } else {
                showUnEnteredDetailsToast(context)
            }
        },
        isPositiveButtonLoading = isLoading
    )
}

private fun showUnEnteredDetailsToast(context: Context) {
    Toast.makeText(context, "Please enter a title and message", Toast.LENGTH_LONG).show()
}

