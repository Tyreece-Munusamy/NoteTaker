package com.example.progressupdatedemo.screens.authentication

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.progressupdatedemo.R
import com.example.progressupdatedemo.components.columns.ColumnWithCenteredContent
import com.example.progressupdatedemo.components.buttons.AppButton
import com.example.progressupdatedemo.components.textfields.EmailInputTextField
import com.example.progressupdatedemo.components.textfields.OutlinedInputTextField
import com.example.progressupdatedemo.components.textfields.PasswordInputTextField
import com.example.progressupdatedemo.domain.models.LoginDetailsHolder
import com.example.progressupdatedemo.domain.models.SignUpDetailsHolder
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.utils.toJson

@Composable
fun SignUpScreen(navController: NavController, signUpDetailsHolder: SignUpDetailsHolder) {
    Surface(modifier = Modifier.fillMaxSize()) {
        SignUpScreenContent(navController, signUpDetailsHolder)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SignUpScreenContent(
    navController: NavController,
    signUpDetailsHolder: SignUpDetailsHolder,
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
) {
    val firstName = remember {
        mutableStateOf(signUpDetailsHolder.firstName)
    }
    val lastName = remember {
        mutableStateOf(signUpDetailsHolder.lastName)
    }
    val email = remember {
        mutableStateOf(signUpDetailsHolder.email)
    }
    val password = remember {
        mutableStateOf("")
    }
    val isPasswordVisible = remember {
        mutableStateOf(false)
    }
    val isAuthenticationViewModelProcessingRequest =
        authenticationViewModel.isProcessingAuthenticationRequest
    val isUserInputValid = remember(firstName.value, email.value, password.value, lastName.value) {
        authenticationViewModel.validateSignUpDetails(
            firstName.value, lastName.value, email.value, password.value
        )
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val localContext = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp)
            .height(100.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(5.dp))
        Icon(
            painter = painterResource(id = R.drawable.account_circle),
            contentDescription = "Account Icon",
            modifier = Modifier.size(95.dp)
        )
    }

    ColumnWithCenteredContent(modifier = Modifier.padding(30.dp)) {
        SignUpPromptText()
        Spacer(modifier = Modifier.height(30.dp))
        FirstNameInputField(firstName)
        LastNameInputField(lastName)
        EmailInputField(email)
        PasswordInputField(password, isPasswordVisible, keyboardController)
        SignUpButton(
            isAuthenticationViewModelProcessingRequest,
            isUserInputValid,
            firstName,
            lastName,
            email,
            password,
            localContext,
            navController,
            authenticationViewModel,
        )
    }
    LoginPromptTextWithLink(navController, isAuthenticationViewModelProcessingRequest, localContext)
}

@Composable
private fun SignUpButton(
    isAuthenticationViewModelProcessingRequest: MutableState<Boolean>,
    isUserInputValid: Boolean,
    firstName: MutableState<String>,
    lastName: MutableState<String>,
    email: MutableState<String>,
    password: MutableState<String>,
    context: Context,
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel,
) {
    SignUpScreenSignUpButton(isAuthenticationViewModelProcessingRequest, isUserInputValid) {
        createUserAccount(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            authenticationViewModel = authenticationViewModel,
            navController = navController,
            context = context
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PasswordInputField(
    password: MutableState<String>,
    passwordVisibility: MutableState<Boolean>,
    keyboardController: SoftwareKeyboardController?,
) {
    PasswordInputTextField(
        password = password,
        passwordVisibility = passwordVisibility,
        onAction = KeyboardActions {
            keyboardController?.hide()
        })
}

@Composable
private fun EmailInputField(emailState: MutableState<String>) {
    EmailInputTextField(emailState = emailState)
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
        Icon(
            painter = painterResource(id = R.drawable.account),
            contentDescription = "Account Icon"
        )
    }, isSingleLine = true)
}

fun createUserAccount(
    firstName: MutableState<String>,
    lastName: MutableState<String>,
    email: MutableState<String>,
    password: MutableState<String>,
    authenticationViewModel: AuthenticationViewModel,
    navController: NavController,
    context: Context,
) {
    authenticationViewModel.signUp(firstName.value,
        lastName.value,
        email.value,
        password.value,
        onFailure = {
            val signUpDetailsHolder =
                SignUpDetailsHolder(firstName.value, lastName.value, email.value)
            if (password.value.length < 6) {
                showPasswordShowBeSixCharactersOrMoreToast(context)
                navigateToSignUpScreenWithPresetDetails(navController, signUpDetailsHolder)
            } else {
                showInvalidDetailsToast(context)
                navigateToSignUpScreenWithPresetDetails(navController, signUpDetailsHolder)
            }
        }) {
        navigateToLoginScreenWithPresetEmail(navController, email.value)
    }
}

private fun showPasswordShowBeSixCharactersOrMoreToast(context: Context) {
    Toast.makeText(context, "Password should be 6 characters or more", Toast.LENGTH_SHORT).show()
}

private fun showInvalidDetailsToast(context: Context) {
    Toast.makeText(context, "Please enter valid details", Toast.LENGTH_SHORT).show()
}

private fun navigateToLoginScreenWithPresetEmail(navController: NavController, email: String) {
    navController.navigate(Screen.LoginScreen.withArgs(LoginDetailsHolder(email).toJson().toString()))
}

private fun navigateToSignUpScreenWithPresetDetails(
    navController: NavController,
    signUpDetailsHolder: SignUpDetailsHolder,
) {
    navController.navigate(
        Screen.SignUpScreen.withArgs(
            signUpDetailsHolder.toJson().toString()
        )
    )
}

@Composable
fun SignUpPromptText() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Create Account",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.h4
        )
        Text(
            text = "Please sign up to continue",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.subtitle1,
            color = Color.LightGray
        )
    }
}

@Composable
fun SignUpScreenSignUpButton(
    isLoading: MutableState<Boolean>,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppButton(
            modifier = Modifier
                .padding(bottom = 10.dp, start = 10.dp, end = 10.dp, top = 10.dp)
                .height(56.dp)
                .fillMaxWidth(),
            text = "Sign Up",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            isLoading = isLoading.value,
            enabled = isEnabled
        ) {
            onClick.invoke()
        }
    }
}

@Composable
private fun LoginPromptTextWithLink(
    navController: NavController,
    isAuthenticationViewModelProcessingRequest: MutableState<Boolean>,
    context: Context,
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
            Text(text = "Already have an account? ")
            Text(text = "Login", color = Color.Blue, modifier = Modifier.clickable {
                if (isAuthenticationViewModelProcessingRequest.value) {
                    showErrorNavigatingToLoginScreenToast(context)
                } else {
                    navigateToLoginScreenWithNoPresetDetails(navController)
                }
            })
        }
    }
}

private fun navigateToLoginScreenWithNoPresetDetails(navController: NavController) {
    navController.navigate(
        Screen.LoginScreen.withArgs(
            LoginDetailsHolder().toJson().toString()
        )
    )
}

private fun showErrorNavigatingToLoginScreenToast(context: Context) {
    Toast.makeText(
        context, "Can't navigate to login screen while processing request", Toast.LENGTH_SHORT
    ).show()
}
