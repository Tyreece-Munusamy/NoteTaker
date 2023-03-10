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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.example.progressupdatedemo.core.constants.Constants
import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.model.User
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.presentation.component.alert_dialog.AppAlertDialog
import com.example.progressupdatedemo.presentation.component.button.NegativePositiveButtonRow
import com.example.progressupdatedemo.presentation.component.column.ColumnWithCenteredContent
import com.example.progressupdatedemo.presentation.component.loading_animation.CircularLoadingAnimation
import com.example.progressupdatedemo.presentation.component.textfield.EmailInputTextField
import com.example.progressupdatedemo.presentation.component.top_bar.CenteredTextTopAppBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UpdateProfileScreen(
    navController: NavController = NavController(LocalContext.current),
    updateScreenViewModel: UpdateScreenViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) { updateScreenViewModel.getUser() }
    val getUserUseCaseResponse = updateScreenViewModel.getUserUseCaseResponseState.value
    val isLoadingUser = remember(updateScreenViewModel.getUserUseCaseResponseState.value) {
        updateScreenViewModel.getUserUseCaseResponseState.value is Response.Loading
    }

    if (isLoadingUser) {
        CircularLoadingAnimation()
    } else {
        UserProfileScreenScaffoldOrErrorLoadingUserDataMessage(
            getUserUseCaseResponse, navController, updateScreenViewModel
        )
    }
}

@Composable
fun UserProfileScreenScaffoldOrErrorLoadingUserDataMessage(
    getUserUseCaseResponse: Response<User>,
    navController: NavController,
    updateScreenViewModel: UpdateScreenViewModel,
) {
    if (getUserUseCaseResponse.message != null || getUserUseCaseResponse.data == null) {
        ErrorLoadingUserDataMessage(getUserUseCaseResponse)
    } else {
        val user = getUserUseCaseResponse.data!!
        UpdateProfileScreenScaffold(
            user,
            navController,
            updateScreenViewModel,
        )
    }
}

@Composable
private fun ErrorLoadingUserDataMessage(getUserUseCaseResponse: Response<User>) {
    ColumnWithCenteredContent {
        Text(text = getUserUseCaseResponse.message ?: "Error loading user data")
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun UpdateProfileScreenScaffold(
    user: User,
    navController: NavController,
    updateScreenViewModel: UpdateScreenViewModel,
) {
    val firstNameFocusRequester = remember { FocusRequester() }
    val firstNameTextFieldValueState = remember {
        mutableStateOf(TextFieldValue(user.firstName.toString(), TextRange(0))
        )
    }
    val lastNameState = remember { mutableStateOf(user.lastName.toString()) }
    val isUserProfileUpdated =
        remember(firstNameTextFieldValueState.value.text, lastNameState.value) {
            updateScreenViewModel.isUserUpdate(
                user, firstNameTextFieldValueState.value.text, lastNameState.value
            )
        }
    val isProcessingUpdateRequest = updateScreenViewModel.isProcessingUpdateRequest.value
    val showSaveChangesAlertDialogState = remember { mutableStateOf(false) }
    val onExitScreen: () -> Unit = {
        if (isUserProfileUpdated) {
            showSaveChangesAlertDialogState.value = true
        } else {
            navigateToHomeScreenWithProfileTabSelected(navController)
        }
    }

    LaunchedEffect(Unit) {
        firstNameFocusRequester.requestFocus()
        firstNameTextFieldValueState.value = TextFieldValue(
            text = user.firstName.toString(), selection = TextRange(user.firstName!!.length)
        )
    }

    OnBackPressedConfiguration(onExitScreen)

    Scaffold(topBar = { UpdateProfileScreenTopBar(onExitScreen) }) {
        UpdateProfileScreenContent(
            user,
            showSaveChangesAlertDialogState,
            isUserProfileUpdated,
            isProcessingUpdateRequest,
            firstNameFocusRequester,
            firstNameTextFieldValueState,
            lastNameState,
            updateScreenViewModel,
            navController
        )
    }
}

private fun navigateToHomeScreenWithProfileTabSelected(navController: NavController) {
    navController.navigate(Screen.HomeScreen.withArgs(Constants.HOME_SCREEN_PROFILE_TAB))
}

@Composable
private fun OnBackPressedConfiguration(onExitScreen: () -> Unit) {
    BackHandler(true) {
        onExitScreen.invoke()
    }
}

@Composable
private fun UpdateProfileScreenTopBar(onExitScreen: () -> Unit, ) {
    CenteredTextTopAppBar(title = {
        UpdateProfileScreenTopBarTitle()
    }, navigationIcon = {
        BackArrowIconButton(onExitScreen)
    })
}

@Composable
private fun UpdateProfileScreenTopBarTitle() {
    Text(
        text = "Edit Profile",
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        fontSize = 18.sp
    )
}

@Composable
private fun BackArrowIconButton(
    onExitScreen: () -> Unit,
) {
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

@Composable
private fun UpdateProfileScreenContent(
    user: User,
    showSaveChangesAlertDialogState: MutableState<Boolean>,
    isUserProfileUpdated: Boolean,
    isProcessingUpdateRequest: Boolean,
    firstNameFocusRequester: FocusRequester,
    firstNameTextFieldValueState: MutableState<TextFieldValue>,
    lastNameState: MutableState<String>,
    updateScreenViewModel: UpdateScreenViewModel,
    navController: NavController
) {
    val emailState = remember { mutableStateOf(user.email.toString()) }
    val onSave: () -> Unit = {
        if (isUserProfileUpdated) {
            updateScreenViewModel.updateUser(
                user.uid.toString(),
                firstNameTextFieldValueState.value.text,
                lastNameState.value,
                user.email.toString()
            )
        }
        navigateToHomeScreenWithProfileTabSelected(navController)
    }
    val onDiscard: () -> Unit = {
        navigateToHomeScreenWithProfileTabSelected(navController)
    }

    if (showSaveChangesAlertDialogState.value && isUserProfileUpdated) {
        UnsavedChangesAlertDialog(
            showSaveChangesAlertDialogState, isProcessingUpdateRequest, onSave, onDiscard
        )
    }

    Column(
        modifier = Modifier.padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        FirstNameInputField(firstNameFocusRequester, firstNameTextFieldValueState)

        LastNameInputField(lastNameState)

        NonFocusableEmailInputField(emailState)

        CancelAndSaveButtonRow(isProcessingUpdateRequest, onSave, onDiscard)
    }
}

@Composable
private fun UnsavedChangesAlertDialog(
    showAlertDialogState: MutableState<Boolean>,
    isLoading: Boolean,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
) {
    AppAlertDialog(
        alertDialogState = showAlertDialogState,
        isLoading = isLoading,
        title = "Unsaved Changes",
        message = { UnsavedChangesAlertDialogMessage() },
        confirmButtonText = "Save",
        onConfirmButtonClicked = onSave,
        dismissButtonText = "Discard",
        onDismissButtonClicked = onDiscard
    )
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
            FirstNameInputFieldLabel()
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
private fun FirstNameInputFieldLabel() {
    Text(text = "First Name")
}

@Composable
private fun AccountIcon() {
    Icon(
        painter = painterResource(id = R.drawable.account), contentDescription = "Account icon"
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LastNameInputField(
    lastNameState: MutableState<String>,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
        value = lastNameState.value,
        onValueChange = {
            lastNameState.value = it
        },
        textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colors.onBackground),
        label = {
            LastNameInputFieldLabel()
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
private fun LastNameInputFieldLabel() {
    Text(text = "Last Name")
}

@Composable
private fun UnsavedChangesAlertDialogMessage() {
    Text(text = "Save changes made to profile?")
}

@Composable
private fun NonFocusableEmailInputField(emailState: MutableState<String>) {
    EmailInputTextField(
        modifier = Modifier.padding(bottom = 10.dp),
        emailState = emailState,
        enabled = false,
        labelId = ""
    )
}

@Composable
private fun CancelAndSaveButtonRow(
    isProcessingUpdateRequest: Boolean,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
) {
    NegativePositiveButtonRow(
        negativeButtonLabel = "Cancel",
        onNegativeButtonClicked = onDiscard,
        positiveButtonLabel = "Save",
        onPositiveButtonClicked = onSave,
        isPositiveButtonLoading = isProcessingUpdateRequest
    )
}


