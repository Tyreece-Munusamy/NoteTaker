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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import com.example.progressupdatedemo.components.customTopBar.CenterTopAppBar
import com.example.progressupdatedemo.components.buttons.AppButton
import com.example.progressupdatedemo.components.buttons.NegativePositiveButtonRow
import com.example.progressupdatedemo.domain.models.Note
import com.example.progressupdatedemo.navigation.Screen
import com.google.firebase.Timestamp

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CreateNoteScreen(
    navController: NavController,
    createNoteScreenViewModel: CreateNoteScreenViewModel = hiltViewModel(),
) {
    val showSaveNoteAlertDialog = remember { mutableStateOf(false) }
    val title = remember {
        mutableStateOf("")
    }
    val message = remember {
        mutableStateOf("")
    }
    val isUserInputValid = remember(title.value, message.value) {
        createNoteScreenViewModel.validateNoteDetails(title.value, message.value)
    }
    OnBackPressedConfiguration(showSaveNoteAlertDialog, message, navController)
    CreateNoteScreenScaffold(
        showSaveNoteAlertDialog,
        navController,
        createNoteScreenViewModel,
        title,
        message,
        isUserInputValid
    )
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun CreateNoteScreenScaffold(
    showSaveNoteAlertDialog: MutableState<Boolean>,
    navController: NavController,
    createNoteScreenViewModel: CreateNoteScreenViewModel,
    title: MutableState<String>,
    message: MutableState<String>,
    isUserInputValid: Boolean,
) {
    Scaffold(topBar = {
        CreateNoteScreenTopBar(showSaveNoteAlertDialog, message, navController)
    }) {
        CreateNoteScreenContent(
            navController,
            createNoteScreenViewModel,
            showSaveNoteAlertDialog,
            title,
            message,
            isUserInputValid
        )
    }
}

@Composable
private fun CreateNoteScreenTopBar(
    showSaveNoteAlertDialog: MutableState<Boolean>,
    message: MutableState<String>,
    navController: NavController,
) {
    CenterTopAppBar(title = {
        TopBarTitle()
    }, navigationIcon = {
        BackArrowIconButton(showSaveNoteAlertDialog, message, navController)
    })
}

@Composable
private fun BackArrowIconButton(
    showSaveNoteAlertDialog: MutableState<Boolean>,
    message: MutableState<String>,
    navController: NavController,
) {
    IconButton(onClick = {
        if (message.value.trim().isNotEmpty()) {
            showSaveNoteAlertDialog.value = true
        } else {
            navigateToHomeScreen(navController)
        }
    }) {
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
    title: MutableState<String>,
    message: MutableState<String>,
    isUserInputValid: Boolean,
) {
    val isCreateNoteViewModelProcessingNoteCreation =
        createNoteScreenViewModel.isProcessingNoteCreation
    val isAlertDialogPromptingNoteCreateProcess = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val messageFocusRequester = remember {
        FocusRequester()
    }

    if (showSaveNoteAlertDialog.value) {
        title.value = title.value.ifEmpty { "Untitled" }
        SaveNoteAlertDialog(
            showSaveNoteAlertDialog,
            isAlertDialogPromptingNoteCreateProcess,
            navController,
            createNoteScreenViewModel,
            title,
            message,
            context
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TitleInputField(title, messageFocusRequester)

        MessageInputField(
            message, keyboardController, messageFocusRequester
        )

        Spacer(modifier = Modifier.height(20.dp))

        CancelAndCreateButtonRow(
            isCreateNoteViewModelProcessingNoteCreation,
            isUserInputValid,
            createNoteScreenViewModel,
            title,
            message,
            context,
            navController
        )
    }
}

@Composable
private fun TitleInputField(titleState: MutableState<String>, focusRequester: FocusRequester) {
    OutlinedTextField(modifier = Modifier.fillMaxWidth(),
        value = titleState.value,
        onValueChange = { titleState.value = it },
        label = {
            Text(text = "Title")
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions {
            focusRequester.requestFocus()
        })
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MessageInputField(
    messageState: MutableState<String>,
    keyboardController: SoftwareKeyboardController?,
    focusRequester: FocusRequester,
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(325.dp)
            .focusRequester(focusRequester),
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
            KeyboardActions.Default
        },
        maxLines = 15
    )
}

@Composable
private fun CancelAndCreateButtonRow(
    isLoading: MutableState<Boolean>,
    isUserInputValid: Boolean,
    createNoteScreenViewModel: CreateNoteScreenViewModel,
    titleState: MutableState<String>,
    messageState: MutableState<String>,
    context: Context,
    navController: NavController,
) {
    NegativePositiveButtonRow(
        negativeButtonLabel = "Cancel",
        onNegativeButtonClicked = { navController.navigate(Screen.HomeScreen.withArgs("notes")) },
        positiveButtonLabel = "Create",
        onPositiveButtonClicked = {
            if (isUserInputValid) {
                isLoading.value = true
                createNoteScreenViewModel.createNote(
                    Note(
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
        isPositiveButtonLoading = isLoading
    )
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
    CreateNoteScreenAlertDialog(showAlertDialog = showSaveNoteAlertDialog,
        isLoading = isAlertDialogLoading,
        onDiscard = {
            navigateToHomeScreen(navController)

        }) {
        isAlertDialogLoading.value = true
        createNoteScreenViewModel.createNote(
            Note(
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
private fun OnBackPressedConfiguration(
    showSaveNoteAlertDialog: MutableState<Boolean>,
    message: MutableState<String>,
    navController: NavController,
) {
    BackHandler(true) {
        if (message.value.trim().isNotEmpty()) {
            showSaveNoteAlertDialog.value = true
        } else {
            navigateToHomeScreen(navController)
        }
    }
}

private fun navigateToHomeScreen(navController: NavController) {
    navController.navigate(Screen.HomeScreen.withArgs("notes"))
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun CreateNoteScreenAlertDialog(
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
