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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.progressupdatedemo.R
import com.example.progressupdatedemo.components.ColumnWithCenteredContent
import com.example.progressupdatedemo.components.buttons.AppButton
import com.example.progressupdatedemo.components.textfields.EmailInputTextField
import com.example.progressupdatedemo.components.textfields.PasswordInputTextField
import com.example.progressupdatedemo.models.LoginDetailsHolder
import com.example.progressupdatedemo.models.SignUpDetailsHolder
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.utils.toJson

@Composable
fun LoginScreen(
    navController: NavController,
    loginDetailsHolder: LoginDetailsHolder,
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        LoginScreenContent(navController, authenticationViewModel, loginDetailsHolder)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LoginScreenContent(
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel,
    loginDetailsHolder: LoginDetailsHolder,
) {
    val email = remember {
        mutableStateOf(loginDetailsHolder.email)
    }

    val password = remember {
        mutableStateOf("")
    }

    val isPasswordVisible = remember {
        mutableStateOf(false)
    }

    val isEmailAndPasswordValid = remember(email.value, password.value) {
        authenticationViewModel.validateEmailAndPassword(email.value, password.value)
    }

    val isAuthenticationViewModelProcessingRequest =
        authenticationViewModel.isProcessingAuthenticationRequest

    val localContext = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LoginScreenAccountIcon()

    ColumnWithCenteredContent(
        modifier = Modifier
            .padding(30.dp)
            .fillMaxSize()
    ) {
        LoginPromptText()
        Spacer(modifier = Modifier.height(30.dp))
        EmailInputTextField(emailState = email)
        PasswordInputTextField(passwordState = password,
            passwordVisibility = isPasswordVisible,
            onAction = KeyboardActions {
                keyboardController?.hide()
                if (isEmailAndPasswordValid) {
                    signInUser(
                        isAuthenticationViewModelProcessingRequest,
                        email,
                        password,
                        authenticationViewModel,
                        localContext,
                        navController
                    )
                } else {
                    Toast.makeText(localContext, "Please enter valid details", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        LoginButton(isAuthenticationViewModelProcessingRequest, isEmailAndPasswordValid) {
            if (isEmailAndPasswordValid) {
                signInUser(
                    loadingState = isAuthenticationViewModelProcessingRequest,
                    emailState = email,
                    passwordState = password,
                    authenticationViewModel = authenticationViewModel,
                    context = localContext,
                    navController = navController
                )
            } else {
                Toast.makeText(localContext, "Please enter valid details", Toast.LENGTH_SHORT)
                    .show()
                navController.navigate(
                    Screen.LoginScreen.withArgs(
                        LoginDetailsHolder().toJson().toString()
                    )
                )
            }
        }
    }
    SignUpPromptTextWithLink(navController = navController)
}

@Composable
private fun LoginScreenAccountIcon() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp)
            .height(100.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(45.dp))
        Icon(
            painter = painterResource(id = R.drawable.account_circle),
            contentDescription = "Account Icon",
            modifier = Modifier.size(95.dp)
        )
    }
}

fun signInUser(
    loadingState: MutableState<Boolean>,
    emailState: MutableState<String>,
    passwordState: MutableState<String>,
    authenticationViewModel: AuthenticationViewModel,
    context: Context,
    navController: NavController,
) {
    loadingState.value = true
    authenticationViewModel.signInUser(emailState.value, passwordState.value, onFailure = {
        Toast.makeText(
            context, "Please enter a valid email address or password", Toast.LENGTH_SHORT
        ).show()
        val loginDetailsHolder = LoginDetailsHolder(emailState.value)
        loadingState.value = false
        emailState.value = ""
        passwordState.value = ""
        navController.navigate(
            Screen.LoginScreen.withArgs(loginDetailsHolder.toJson().toString())
        )
    }) {
        loadingState.value = false
        navController.navigate(Screen.HomeScreen.withArgs("notes"))
    }
}

@Composable
private fun LoginPromptText() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Login", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.h4)
        Text(
            text = "Please sign in to continue",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.subtitle1,
            color = Color.LightGray
        )
    }
}

@Composable
private fun LoginButton(
    loadingState: MutableState<Boolean>,
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
            text = "Login",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            isLoading = loadingState.value,
            enabled = isEnabled
        ) {
            onClick.invoke()
        }
    }
}

@Composable
private fun SignUpPromptTextWithLink(navController: NavController) {
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
            Text(text = "Don't have an account? ")
            Text(text = "Sign up", color = Color.Blue, modifier = Modifier.clickable {
                navController.navigate(Screen.SignUpScreen.withArgs(SignUpDetailsHolder().toJson().toString()))
            })
        }
    }
}