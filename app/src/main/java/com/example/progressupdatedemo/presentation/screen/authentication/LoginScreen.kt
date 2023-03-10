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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.progressupdatedemo.R
import com.example.progressupdatedemo.core.constants.Constants
import com.example.progressupdatedemo.core.constants.TestTags
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.presentation.component.button.AuthenticationButton
import com.example.progressupdatedemo.presentation.component.column.ColumnWithCenteredContent
import com.example.progressupdatedemo.presentation.component.snackbar.AuthenticationErrorSnackbar
import com.example.progressupdatedemo.presentation.component.textfield.EmailInputTextField
import com.example.progressupdatedemo.presentation.component.textfield.PasswordInputTextField
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LoginScreen(navController: NavController) {
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
        LoginScreenContent(navController, launchSnackbar)
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LoginScreenContent(
    navController: NavController,
    launchSnackbar: (String) -> Unit,
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val isEmailAndPasswordValid = remember(emailState.value, passwordState.value) {
        authenticationViewModel.validateLoginDetails(emailState.value, passwordState.value)
    }
    val isProcessingAuthenticationRequest =
        authenticationViewModel.isProcessingAuthenticationRequest.value
    val keyboardController = LocalSoftwareKeyboardController.current
    val onLoginButtonClick: () -> Unit = {
        authenticationViewModel.signIn(email = emailState.value,
            password = passwordState.value,
            onSuccess = { navigateToHomeScreenWithNotesTabSelected(navController) },
            onFailure = { errorMessage ->
                passwordState.value = ""
                keyboardController?.hide()
                launchSnackbar.invoke(errorMessage)
            })
    }

    LoginScreenAccountIcon()

    ColumnWithCenteredContent(modifier = Modifier.padding(30.dp)) {
        LoginPromptText()

        Spacer(Modifier.height(30.dp))

        EmailInputField(emailState)

        PasswordInputField(passwordState, keyboardController)

        LoginButton(
            isProcessingAuthenticationRequest, isEmailAndPasswordValid, onLoginButtonClick
        )
    }

    SignUpPromptTextWithLink(
        navController, isProcessingAuthenticationRequest, launchSnackbar
    )
}

@Composable
private fun LoginScreenAccountIcon() {
    Column(
        modifier = Modifier
            .testTag(TestTags.LOGIN_SCREEN_ACCOUNT_ICON_TAG)
            .fillMaxWidth()
            .padding(30.dp)
            .height(130.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(35.dp))
        AccountIcon()
    }
}

@Composable
private fun AccountIcon() {
    Icon(
        painter = painterResource(R.drawable.account_circle),
        contentDescription = "Account Icon",
        modifier = Modifier.size(95.dp)
    )
}

@Composable
private fun LoginPromptText() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TestTags.LOGIN_SCREEN_LOGIN_PROMPT_TEXT_TAG),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        LoginText()
        SignInToContinueText()
    }
}

@Composable
private fun LoginText() {
    Text(text = "Login", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.h4)
}

@Composable
private fun SignInToContinueText() {
    Text(
        text = "Please sign in to continue",
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.subtitle1,
        color = Color.LightGray
    )
}

@Composable
private fun EmailInputField(emailState: MutableState<String>) {
    EmailInputTextField(
        modifier = Modifier.testTag(TestTags.LOGIN_SCREEN_EMAIL_INPUT_FIELD_TAG),
        emailState = emailState
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PasswordInputField(
    passwordState: MutableState<String>,
    keyboardController: SoftwareKeyboardController?,
) {
    val isPasswordVisibleState = remember { mutableStateOf(false) }
    PasswordInputTextField(
        passwordState = passwordState,
        passwordVisibilityState = isPasswordVisibleState,
        onAction = KeyboardActions {
            keyboardController?.hide()
        },
        modifier = Modifier.testTag(TestTags.LOGIN_SCREEN_PASSWORD_INPUT_FIELD_TAG)
    )
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    isEmailAndPasswordValid: Boolean,
    onClick: () -> Unit,
) {
    AuthenticationButton("Login", isLoading, isEnabled = isEmailAndPasswordValid) {
        onClick.invoke()
    }
}

@Composable
private fun SignUpPromptTextWithLink(
    navController: NavController,
    isProcessingRequest: Boolean,
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
            modifier = Modifier
                .fillMaxWidth()
                .testTag(TestTags.LOGIN_SCREEN_SIGN_UP_PROMPT_TEXT_TAG),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NoAccountText()
            ClickableSignUpText(isProcessingRequest, launchSnackbar, navController)
        }
    }
}

@Composable
private fun NoAccountText() {
    Text(text = "Don't have an account? ")
}

@Composable
private fun ClickableSignUpText(
    isProcessingRequest: Boolean,
    launchSnackbar: (String) -> Unit,
    navController: NavController,
) {
    Text(text = "Sign up", color = Color.Blue, modifier = Modifier.clickable {
        if (isProcessingRequest) {
            showUnableToNavigateSnackbar(launchSnackbar)
        } else {
            navigateToSignUpScreen(navController)
        }
    })
}

private fun showUnableToNavigateSnackbar(launchSnackbar: (String) -> Unit) {
    launchSnackbar.invoke("Can't navigate to sign up screen while processing request")
}

private fun navigateToSignUpScreen(navController: NavController) {
    navController.navigate(Screen.SignUpScreen.route)
}

private fun navigateToHomeScreenWithNotesTabSelected(navController: NavController) {
    navController.navigate(Screen.HomeScreen.withArgs(Constants.HOME_SCREEN_NOTES_TAB))
}


