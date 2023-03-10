package com.example.progressupdatedemo.presentation.screen.update

import android.annotation.SuppressLint
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.model.Note
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.presentation.component.alert_dialog.AppAlertDialog
import com.example.progressupdatedemo.presentation.component.button.NegativePositiveButtonRow
import com.example.progressupdatedemo.presentation.component.column.ColumnWithCenteredContent
import com.example.progressupdatedemo.presentation.component.loading_animation.CircularLoadingAnimation
import com.example.progressupdatedemo.presentation.component.top_bar.CenteredTextTopAppBar
import com.example.progressupdatedemo.presentation.screen.details.NoteDetailsViewModel
import java.util.*

@Composable
fun UpdateNoteScreen(
    navController: NavController,
    noteUUID: String,
    previousSelectedHomeScreenTab: String,
    noteDetailsViewModel: NoteDetailsViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) { noteDetailsViewModel.getNoteByUUID(noteUUID) }
    val getNoteUseCaseResponse = noteDetailsViewModel.getNoteUseCaseResponseState.value
    val isLoadingNote = remember(noteDetailsViewModel.getNoteUseCaseResponseState.value) {
        noteDetailsViewModel.getNoteUseCaseResponseState.value is Response.Loading
    }

    if (isLoadingNote) {
        CircularLoadingAnimation()
    } else {
        ErrorLoadingNoteMessageOrUpdateNoteScreenScaffold(
            getNoteUseCaseResponse, navController, previousSelectedHomeScreenTab
        )
    }
}

@Composable
fun ErrorLoadingNoteMessageOrUpdateNoteScreenScaffold(
    noteUseCaseResponse: Response<Note>,
    navController: NavController,
    previousSelectedHomeScreenTab: String,
) {
    if (noteUseCaseResponse.message != null || noteUseCaseResponse.data == null) {
        ErrorFetchingNoteDetailsMessage(noteUseCaseResponse.message)
    } else {
        val note = noteUseCaseResponse.data!!
        UpdateNoteScreenScaffold(
            note, navController, previousSelectedHomeScreenTab
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
private fun UpdateNoteScreenScaffold(
    note: Note,
    navController: NavController,
    previousSelectedHomeScreenTab: String,
) {
    val titleState = remember { mutableStateOf(note.title.toString()) }
    val messageTextFieldValueState = remember {
        mutableStateOf(TextFieldValue(note.message.toString(), TextRange(0)))
    }
    val isNoteUpdated = remember(titleState.value, messageTextFieldValueState.value.text) {
        titleState.value != note.title || messageTextFieldValueState.value.text != note.message
    }
    val showUnsavedChangesAlertDialogState = remember { mutableStateOf(false) }
    val onExitScreen: () -> Unit = {
        if (isNoteUpdated) showUnsavedChangesAlertDialogState.value = true
        else navController.popBackStack()
    }

    OnBackPressedConfiguration(onExitScreen)

    Scaffold(topBar = { UpdateNoteScreenTopBar(onExitScreen) }) {
        UpdateNoteScreenContent(
            note,
            navController,
            previousSelectedHomeScreenTab,
            showUnsavedChangesAlertDialogState,
            titleState,
            messageTextFieldValueState,
            isNoteUpdated
        )
    }
}

@Composable
private fun OnBackPressedConfiguration(
    onExitScreen: () -> Unit,
) {
    BackHandler(true) {
        onExitScreen.invoke()
    }
}

@Composable
private fun UpdateNoteScreenTopBar(onExitScreen: () -> Unit) {
    CenteredTextTopAppBar(title = {
        UpdateProfileScreenTopBarTitle()
    }, navigationIcon = {
        BackArrowIconButton(onExitScreen)
    })
}

@Composable
private fun UpdateProfileScreenTopBarTitle() {
    Text(
        text = "Edit Note", fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 18.sp
    )
}

@Composable
private fun BackArrowIconButton(onExitScreen: () -> Unit) {
    IconButton(onClick = {
        onExitScreen.invoke()
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UpdateNoteScreenContent(
    note: Note,
    navController: NavController,
    previousSelectedHomeScreenTab: String,
    showUnsavedChangesAlertDialogState: MutableState<Boolean>,
    titleState: MutableState<String>,
    messageTextFieldValueState: MutableState<TextFieldValue>,
    isNoteUpdated: Boolean,
    updateScreenViewModel: UpdateScreenViewModel = hiltViewModel(),
) {
    val isProcessingUpdateRequest = updateScreenViewModel.isProcessingUpdateRequest.value
    val messageFocusRequester = remember { FocusRequester() }
    val onUpdate: () -> Unit = {
        if (isNoteUpdated) {
            val updateNote = note.copy(
                title = titleState.value,
                message = messageTextFieldValueState.value.text,
            )
            updateScreenViewModel.updateNote(updateNote)
        }
        navigateToDetailsScreen(navController, note.uuid, previousSelectedHomeScreenTab)
    }
    val onDiscard: () -> Unit = { navController.popBackStack() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        messageFocusRequester.requestFocus()
        messageTextFieldValueState.value = TextFieldValue(
            text = note.message.toString(), selection = TextRange(note.message!!.length)
        )
    }

    if (showUnsavedChangesAlertDialogState.value) {
        UnsavedChangesAlertDialog(
            showUnsavedChangesAlertDialogState, isProcessingUpdateRequest, onUpdate, onDiscard
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

        MessageInputField(messageFocusRequester, messageTextFieldValueState, keyboardController)

        Spacer(Modifier.height(20.dp))

        DiscardAndSaveButtonRow(
            isProcessingUpdateRequest, onUpdate, onDiscard
        )
    }
}

private fun navigateToDetailsScreen(
    navController: NavController,
    noteUUID: String,
    previousSelectedHomeScreenTab: String,
) {
    navController.navigate(
        Screen.NoteDetailsScreen.withArgs(
            noteUUID, previousSelectedHomeScreenTab
        )
    )
}

@Composable
private fun UnsavedChangesAlertDialog(
    showAlertDialogState: MutableState<Boolean>,
    isLoading: Boolean,
    onUpdate: () -> Unit,
    onDiscard: () -> Unit,
) {
    AppAlertDialog(alertDialogState = showAlertDialogState,
        isLoading = isLoading,
        title = "Unsaved Changes",
        message = { Text(text = "Save note?") },
        confirmButtonText = "Save",
        onConfirmButtonClicked = { onUpdate.invoke() },
        dismissButtonText = "Discard",
        onDismissButtonClicked = { onDiscard.invoke() })
}

@Composable
private fun TitleInputField(titleState: MutableState<String>) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = titleState.value,
        onValueChange = { titleState.value = it },
        label = {
            TitleInputFieldLabel()
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions.Default
    )
}

@Composable
private fun TitleInputFieldLabel() {
    Text(text = "Title")
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
            MessageInputFieldLabel()
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
private fun MessageInputFieldLabel() {
    Text(text = "Message")
}

@Composable
private fun DiscardAndSaveButtonRow(
    isProcessingUpdateRequest: Boolean,
    onUpdate: () -> Unit,
    onDiscard: () -> Unit,
) {
    NegativePositiveButtonRow(
        negativeButtonLabel = "Discard",
        onNegativeButtonClicked = { onDiscard.invoke() },
        positiveButtonLabel = "Save",
        onPositiveButtonClicked = { onUpdate.invoke() },
        isPositiveButtonLoading = isProcessingUpdateRequest
    )
}