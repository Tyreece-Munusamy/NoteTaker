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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
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
import com.example.progressupdatedemo.components.textfields.EmailInputTextField
import com.example.progressupdatedemo.domain.models.User
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.utils.Constants

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UpdateProfileScreen(
    user: User,
    navController: NavController = NavController(LocalContext.current),
    updateScreenViewModel: UpdateScreenViewModel = hiltViewModel(),
) {
    val firstNameTextFieldValue = remember {
        mutableStateOf(
            TextFieldValue(
                text = user.firstName.toString(), selection = TextRange(0)
            )
        )
    }
    val lastNameState = remember {
        mutableStateOf(user.lastName.toString())
    }
    val emailState = remember {
        mutableStateOf(user.email.toString())
    }
    val isUserProfileUpdated = remember(firstNameTextFieldValue.value.text, lastNameState.value) {
        firstNameTextFieldValue.value.text != user.firstName || lastNameState.value != user.lastName
    }
    val showSaveChangesAlertDialog = remember {
        mutableStateOf(false)
    }
    val isLoading = remember {
        mutableStateOf(false)
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val firstNameFocusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    BackHandler(true) {
        if (isUserProfileUpdated) {
            showSaveChangesAlertDialog.value = true
        } else {
            navigateToHomeScreenWithProfileTabSelected(navController)
        }
    }

    LaunchedEffect(Unit) {
        firstNameFocusRequester.requestFocus()
        firstNameTextFieldValue.value = TextFieldValue(
            text = user.firstName.toString(), selection = TextRange(user.firstName!!.length)
        )
    }

    UpdateProfileScreenScaffold(
        isUserProfileUpdated,
        showSaveChangesAlertDialog,
        navController,
        isLoading,
        updateScreenViewModel,
        user,
        firstNameTextFieldValue,
        lastNameState,
        context,
        firstNameFocusRequester,
        keyboardController,
        emailState
    )
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun UpdateProfileScreenScaffold(
    isUserProfileUpdated: Boolean,
    showSaveChangesAlertDialog: MutableState<Boolean>,
    navController: NavController,
    isLoading: MutableState<Boolean>,
    updateScreenViewModel: UpdateScreenViewModel,
    user: User,
    firstNameTextFieldValue: MutableState<TextFieldValue>,
    lastNameState: MutableState<String>,
    context: Context,
    firstNameFocusRequester: FocusRequester,
    keyboardController: SoftwareKeyboardController?,
    emailState: MutableState<String>,
) {
    val isProcessingUpdateRequest = updateScreenViewModel.isProcessingUpdateRequest

    Scaffold(topBar = {
        UpdateProfileScreenTopBar(isUserProfileUpdated, showSaveChangesAlertDialog, navController)
    }) {

        if (showSaveChangesAlertDialog.value && isUserProfileUpdated) {
            UnsavedChangesAlertDialog(
                showSaveChangesAlertDialog,
                isLoading,
                navController,
                updateScreenViewModel,
                user,
                firstNameTextFieldValue,
                lastNameState,
                context
            )
        }

        Column(
            modifier = Modifier.padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            FirstNameInputField(firstNameFocusRequester, firstNameTextFieldValue)
            LastNameInputField(firstNameFocusRequester, lastNameState, keyboardController)
            NonFocusableEmailInputField(emailState)

            CancelAndSaveButtonRow(
                isUserProfileUpdated,
                updateScreenViewModel,
                user,
                firstNameTextFieldValue,
                lastNameState,
                context,
                navController,
                isProcessingUpdateRequest
            )
        }
    }
}

@Composable
private fun CancelAndSaveButtonRow(
    isUserProfileUpdated: Boolean,
    updateScreenViewModel: UpdateScreenViewModel,
    user: User,
    firstNameTextFieldValue: MutableState<TextFieldValue>,
    lastNameState: MutableState<String>,
    context: Context,
    navController: NavController,
    isProcessingUpdateRequest: MutableState<Boolean>,
) {
    NegativePositiveButtonRow(
        negativeButtonLabel = "Cancel",
        onNegativeButtonClicked = { navigateToHomeScreenWithProfileTabSelected(navController) },
        positiveButtonLabel = "Save",
        onPositiveButtonClicked = {
            if (isUserProfileUpdated) {
                updateScreenViewModel.updateUser(user.id.toString(),
                    firstNameTextFieldValue.value.text,
                    lastNameState.value,
                    user.email.toString(),
                    onFailure = {
                        showFailedToUpdateProfileToast(context)
                    }) {
                    navigateToHomeScreenWithProfileTabSelected(navController)
                }
            } else {
                navigateToHomeScreenWithProfileTabSelected(navController)
            }
        },
        isPositiveButtonLoading = isProcessingUpdateRequest
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LastNameInputField(
    firstNameFocusRequester: FocusRequester,
    lastNameState: MutableState<String>,
    keyboardController: SoftwareKeyboardController?,
) {
    OutlinedTextField(
        modifier = Modifier
            .focusRequester(firstNameFocusRequester)
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
        value = lastNameState.value,
        onValueChange = {
            lastNameState.value = it
        },
        textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colors.onBackground),
        label = {
            Text(text = "Last Name")
        },
        leadingIcon = {
            AccountIcon()
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions {
            keyboardController?.hide()
        },
        maxLines = 1
    )
}

@Composable
private fun NonFocusableEmailInputField(emailState: MutableState<String>) {
    EmailInputTextField(emailState = emailState, enabled = false, labelId = "")
}

@Composable
private fun FirstNameInputField(
    firstNameFocusRequester: FocusRequester,
    firstNameTextFieldValue: MutableState<TextFieldValue>,
) {
    OutlinedTextField(
        modifier = Modifier
            .focusRequester(firstNameFocusRequester)
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
        value = firstNameTextFieldValue.value,
        onValueChange = {
            firstNameTextFieldValue.value = it
        },
        textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colors.onBackground),
        label = {
            Text(text = "First Name")
        },
        leadingIcon = {
            AccountIcon()
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions.Default,
        maxLines = 1
    )
}

@Composable
private fun AccountIcon() {
    Icon(
        painter = painterResource(id = R.drawable.account), contentDescription = "Account icon"
    )
}

@Composable
private fun UnsavedChangesAlertDialog(
    showSaveChangesAlertDialog: MutableState<Boolean>,
    isProcessingUpdateRequest: MutableState<Boolean>,
    navController: NavController,
    updateScreenViewModel: UpdateScreenViewModel,
    user: User,
    firstNameTextFieldValue: MutableState<TextFieldValue>,
    lastNameState: MutableState<String>,
    context: Context,
) {
    UpdateProfileScreenSaveChangesAlertDialog(showAlertDialog = showSaveChangesAlertDialog,
        isProcessingUpdateRequest = isProcessingUpdateRequest,
        onDiscard = {
            showSaveChangesAlertDialog.value = false
            navigateToHomeScreenWithProfileTabSelected(navController)

        }) {
        updateScreenViewModel.updateUser(user.id.toString(),
            firstNameTextFieldValue.value.text,
            lastNameState.value,
            user.email.toString(),
            onFailure = {
                showFailedToUpdateProfileToast(context)
                navigateToHomeScreenWithProfileTabSelected(navController)
            }) {
            navigateToHomeScreenWithProfileTabSelected(navController)
        }
    }
}

private fun showFailedToUpdateProfileToast(context: Context) {
    Toast.makeText(
        context, "Failed to update profile", Toast.LENGTH_LONG
    ).show()
}

@Composable
private fun UpdateProfileScreenTopBar(
    isUserProfileUpdated: Boolean,
    showSaveChangesAlertDialog: MutableState<Boolean>,
    navController: NavController,
) {
    CenterTopAppBar(title = {
        TopBarTitle()
    }, navigationIcon = {
        BackArrowIconButton(isUserProfileUpdated, showSaveChangesAlertDialog, navController)
    })
}

@Composable
private fun BackArrowIconButton(
    isUserProfileUpdated: Boolean,
    showSaveChangesAlertDialog: MutableState<Boolean>,
    navController: NavController,
) {
    IconButton(onClick = {
        if (isUserProfileUpdated) {
            showSaveChangesAlertDialog.value = true
        } else {
            navigateToHomeScreenWithProfileTabSelected(navController)
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

private fun navigateToHomeScreenWithProfileTabSelected(navController: NavController) {
    navController.navigate(Screen.HomeScreen.withArgs(Constants.HOME_SCREEN_PROFILE_TAB))
}

@Composable
private fun TopBarTitle() {
    Text(
        text = "Edit Profile",
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        fontSize = 18.sp
    )
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun UpdateProfileScreenSaveChangesAlertDialog(
    showAlertDialog: MutableState<Boolean>,
    isProcessingUpdateRequest: MutableState<Boolean>,
    onDiscard: () -> Unit,
    onSave: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AlertDialog(onDismissRequest = {
            showAlertDialog.value = false
        }, title = {
            SaveProfileAlertDialogTitle()
        }, text = {
            SaveProfileChangesMessage()
        }, confirmButton = {
            SaveProfileChangesConfirmButton(isProcessingUpdateRequest, onSave)
        }, modifier = Modifier.height(170.dp), dismissButton = {
            SaveProfileChangesDismissButton(onDiscard)
        })
    }
}

@Composable
private fun SaveProfileChangesDismissButton(onDiscard: () -> Unit) {
    AppButton(modifier = Modifier.width(95.dp), text = "Discard") {
        onDiscard.invoke()
    }
}

@Composable
private fun SaveProfileChangesConfirmButton(
    isProcessingUpdateRequest: MutableState<Boolean>,
    onSave: () -> Unit,
) {
    AppButton(
        modifier = Modifier.width(95.dp), isLoading = isProcessingUpdateRequest.value, text = "Save"
    ) {
        onSave.invoke()
    }
}

@Composable
private fun SaveProfileChangesMessage() {
    Text(text = "Save changes made to profile?")
}

@Composable
private fun SaveProfileAlertDialogTitle() {
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
