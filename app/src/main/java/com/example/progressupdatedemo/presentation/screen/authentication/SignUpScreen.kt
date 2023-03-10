package com.example.progressupdatedemo.presentation.screen.authentication

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.progressupdatedemo.R
import com.example.progressupdatedemo.core.constants.Constants
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.presentation.component.button.AuthenticationButton
import com.example.progressupdatedemo.presentation.component.column.ColumnWithCenteredContent
import com.example.progressupdatedemo.presentation.component.snackbar.AuthenticationErrorSnackbar
import com.example.progressupdatedemo.presentation.component.textfield.EmailInputTextField
import com.example.progressupdatedemo.presentation.component.textfield.OutlinedInputTextField
import com.example.progressupdatedemo.presentation.component.textfield.PasswordInputTextField
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SignUpScreen(navController: NavController) {
    BackHandler(true) { }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val launchSnackbar: (String) -> Unit = { message ->
        coroutineScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
        }
    }

    BackHandler(true) { }
    Scaffold(modifier = Modifier.fillMaxSize(), scaffoldState = scaffoldState, snackbarHost = {
        SnackbarHost(it) { data -> AuthenticationErrorSnackbar(data) }
    }) {
        SignUpScreenContent(navController, launchSnackbar)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SignUpScreenContent(
    navController: NavController,
    launchSnackbar: (String) -> Unit,
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
) {
    val firstNameState = remember { mutableStateOf("") }
    val lastNameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val isProcessingAuthenticationRequest =
        authenticationViewModel.isProcessingAuthenticationRequest
    val isUserInputValid =
        remember(firstNameState.value, emailState.value, passwordState.value, lastNameState.value) {
            authenticationViewModel.validateSignUpDetails(
                firstNameState.value, lastNameState.value, emailState.value, passwordState.value
            )
        }
    val keyboardController = LocalSoftwareKeyboardController.current
    val onSignUpButtonClick = {
        authenticationViewModel.signUp(firstNameState.value,
            lastNameState.value,
            emailState.value,
            passwordState.value,
            onSuccess = {
                navigateToHomeScreenWithNotesTabSelected(navController)
            },
            onFailure = { errorMessage ->
                passwordState.value = ""
                keyboardController?.hide()
                launchSnackbar.invoke(errorMessage)
            })
    }

    SignUpScreenAccountIcon()

    ColumnWithCenteredContent(modifier = Modifier.padding(30.dp)) {
        SignUpPromptText()

        Spacer(modifier = Modifier.height(30.dp))

        FirstNameInputField(firstNameState)

        LastNameInputField(lastNameState)

        EmailInputField(emailState)

        PasswordInputField(passwordState, keyboardController)

        SignUpButton(
            isProcessingAuthenticationRequest.value, isUserInputValid, onSignUpButtonClick
        )
    }

    LoginPromptTextWithLink(
        navController, isProcessingAuthenticationRequest.value, launchSnackbar
    )
}

private fun navigateToHomeScreenWithNotesTabSelected(navController: NavController) {
    navController.navigate(Screen.HomeScreen.withArgs(Constants.HOME_SCREEN_NOTES_TAB))
}

@Composable
private fun SignUpScreenAccountIcon() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp)
            .height(100.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(5.dp))
        AccountIcon()
    }
}

@Composable
private fun AccountIcon() {
    Icon(
        painter = painterResource(id = R.drawable.account_circle),
        contentDescription = "Account Icon",
        modifier = Modifier.size(95.dp)
    )
}

@Composable
fun SignUpPromptText() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        CreateAccountText()
        PleaseSignUpPromptText()
    }
}

@Composable
private fun CreateAccountText() {
    Text(
        text = "Create Account",
        fontWeight = FontWeight.SemiBold,
        style = MaterialTheme.typography.h4
    )
}

@Composable
private fun PleaseSignUpPromptText() {
    Text(
        text = "Please sign up to continue",
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.subtitle1,
        color = Color.LightGray
    )
}

@Composable
private fun FirstNameInputField(firstNameState: MutableState<String>) {
    NameInputField(firstNameState, "First Name")
}

@Composable
private fun LastNameInputField(lastNameState: MutableState<String>) {
    NameInputField(lastNameState, "Last Name")
}

@Composable
private fun NameInputField(nameState: MutableState<String>, label: String) {
    OutlinedInputTextField(valueState = nameState, labelId = label, icon = {
        AccountIconNoCircle()
    }, isSingleLine = true)
}

@Composable
private fun AccountIconNoCircle() {
    Icon(
        painter = painterResource(id = R.drawable.account), contentDescription = "Account Icon"
    )
}

@Composable
private fun EmailInputField(emailState: MutableState<String>) {
    EmailInputTextField(emailState = emailState)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PasswordInputField(
    password: MutableState<String>,
    keyboardController: SoftwareKeyboardController?,
) {
    val isPasswordVisibleState = remember { mutableStateOf(false) }
    PasswordInputTextField(passwordState = password,
        passwordVisibilityState = isPasswordVisibleState,
        onAction = KeyboardActions {
            keyboardController?.hide()
        })
}

@Composable
private fun SignUpButton(
    isLoading: Boolean,
    isUserInputValid: Boolean,
    onClick: () -> Unit,
) {
    AuthenticationButton("Sign Up", isLoading, isEnabled = isUserInputValid, onClick)
}

@Composable
private fun LoginPromptTextWithLink(
    navController: NavController,
    isLoading: Boolean,
    launchSnackbar: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExistingAccountText()
            ClickableLoginText(isLoading, launchSnackbar, navController)
        }
    }
}

@Composable
private fun ExistingAccountText() {
    Text(text = "Already have an account? ")
}

@Composable
private fun ClickableLoginText(
    isLoading: Boolean,
    launchSnackbar: (String) -> Unit,
    navController: NavController,
) {
    Text(text = "Login", color = Color.Blue, modifier = Modifier.clickable {
        if (isLoading) {
            showUnableToNavigateSnackbar(launchSnackbar)
        } else {
            navigateToLoginScreen(navController)
        }
    })
}

private fun showUnableToNavigateSnackbar(launchSnackbar: (String) -> Unit) {
    launchSnackbar.invoke("Can't navigate to login screen while processing request")
}

private fun navigateToLoginScreen(navController: NavController) {
    navController.navigate(Screen.LoginScreen.route)
}
