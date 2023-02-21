package com.example.progressupdatedemo.screens.authentication

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import com.example.progressupdatedemo.components.textfields.NameInputTextField
import com.example.progressupdatedemo.components.textfields.PasswordInputTextField
import com.example.progressupdatedemo.navigation.ApplicationScreens

@Composable
fun SignUpScreen(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        SignUpScreenContent(navController)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SignUpScreenContent(
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val firstNameState = remember {
        mutableStateOf("")
    }
    val lastNameState = remember {
        mutableStateOf("")
    }
    val emailState = remember {
        mutableStateOf("")
    }
    val passwordState = remember {
        mutableStateOf("")
    }
    val passwordVisibility = remember {
        mutableStateOf(false)
    }
    val isUserInputValid =
        remember(firstNameState.value, emailState.value, passwordState.value, lastNameState.value) {
            firstNameState.value.trim().isNotEmpty() && emailState.value.trim()
                .isNotEmpty() && passwordState.value.trim()
                .isNotEmpty() && lastNameState.value.trim().isNotEmpty()
        }
    val isLoading = remember {
        mutableStateOf(false)
    }
    val keyboardController = LocalSoftwareKeyboardController.current

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
        NameInputTextField(nameState = firstNameState, label = "First Name")
        NameInputTextField(nameState = lastNameState, label = "Last Name")
        EmailInputTextField(emailState = emailState)
        PasswordInputTextField(passwordState = passwordState,
            passwordVisibility = passwordVisibility,
            onAction = KeyboardActions {
                keyboardController?.hide()
            })
        SignUpButton(isLoading, isUserInputValid) {
            if (isUserInputValid) {
                createUserAccount(
                    firstNameState = firstNameState,
                    lastNameState = lastNameState,
                    email = emailState,
                    password = passwordState,
                    isLoading = isLoading,
                    navController = navController,
                    authenticationViewModel = authenticationViewModel
                )
            } else {
                Toast.makeText(context, "Please enter valid details", Toast.LENGTH_SHORT).show()
                navController.navigate(ApplicationScreens.SignUpScreen.name)
            }
        }
    }
    LoginPromptTextWithLink(navController = navController)
}

fun createUserAccount(
    firstNameState: MutableState<String>,
    lastNameState: MutableState<String>,
    email: MutableState<String>,
    password: MutableState<String>,
    isLoading: MutableState<Boolean>,
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel,
) {
    isLoading.value = true
    authenticationViewModel.createUserWithEmailAndPassword(firstNameState.value,lastNameState.value,
        email.value,
        password.value,
        onFailure = {
            isLoading.value = false
            firstNameState.value = ""
            email.value = ""
            password.value = ""
        }) {
        isLoading.value = false
        navController.navigate("${ApplicationScreens.HomeScreen.name}/notes")
    }
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
fun SignUpButton(isLoading: MutableState<Boolean>, isEnabled: Boolean, onClick: () -> Unit) {
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
private fun LoginPromptTextWithLink(navController: NavController) {
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
                navController.navigate(ApplicationScreens.LoginScreen.name)
            })
        }
    }
}
