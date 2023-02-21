package com.example.progressupdatedemo.screens.update

import android.annotation.SuppressLint
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
import com.example.progressupdatedemo.components.CenterTopAppBar
import com.example.progressupdatedemo.components.buttons.AppButton
import com.example.progressupdatedemo.components.textfields.EmailInputTextField
import com.example.progressupdatedemo.components.textfields.NameInputTextField
import com.example.progressupdatedemo.models.User
import com.example.progressupdatedemo.navigation.ApplicationScreens

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
        showSaveChangesAlertDialog.value = true
    }

    LaunchedEffect(Unit) {
        firstNameFocusRequester.requestFocus()
        firstNameTextFieldValue.value = TextFieldValue(
            text = user.firstName.toString(), selection = TextRange(user.firstName!!.length)
        )
    }

    Scaffold(topBar = {
        CenterTopAppBar(title = {
            Text(
                text = "Edit Profile",
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = 18.sp
            )
        }, navigationIcon = {
            IconButton(onClick = {
                if (isUserProfileUpdated) {
                    showSaveChangesAlertDialog.value = true
                } else {
                    navController.navigate("${ApplicationScreens.HomeScreen.name}/profile")
                }
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back arrow",
                    tint = Color.White
                )
            }
        })
    }) {

        if (showSaveChangesAlertDialog.value && isUserProfileUpdated) {
            SaveChangedAlertDialog(
                showAlertDialog = showSaveChangesAlertDialog,
                isLoading = isLoading,
                onDiscard = {
                    showSaveChangesAlertDialog.value = false
                    navController.navigate("${ApplicationScreens.HomeScreen.name}/profile")

                }) {
                isLoading.value = true
                updateScreenViewModel.updateUser(user.id.toString(),
                    firstNameTextFieldValue.value.text,
                    lastNameState.value,
                    user.email.toString(),
                    onFailure = {
                        Toast.makeText(context, "Failed to update profile", Toast.LENGTH_LONG)
                            .show()
                        navController.navigate("${ApplicationScreens.HomeScreen.name}/profile")
                    }) {
                    navController.navigate("${ApplicationScreens.HomeScreen.name}/profile")
                    isLoading.value = false
                }
            }
        }

        Column(
            modifier = Modifier.padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
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
                    Icon(
                        painter = painterResource(id = R.drawable.account),
                        contentDescription = "Account icon"
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions.Default,
                maxLines = 1
            )

            NameInputTextField(nameState = lastNameState,
                imeAction = ImeAction.Done,
                onAction = KeyboardActions {
                    keyboardController?.hide()
                })

            EmailInputTextField(emailState = emailState, enabled = false, labelId = "")
            Row(modifier = Modifier.padding(10.dp)) {
                AppButton(
                    modifier = Modifier
                        .weight(1f, true)
                        .height(46.dp),
                    text = "Save",
                ) {
                    if (isUserProfileUpdated) {
                        updateScreenViewModel.updateUser(user.id.toString(),
                            firstNameTextFieldValue.value.text,
                            lastNameState.value,
                            user.email.toString(),
                            onFailure = {
                                Toast.makeText(
                                    context, "Failed to update profile", Toast.LENGTH_LONG
                                ).show()
                            }) {
                            navController.navigate("${ApplicationScreens.HomeScreen.name}/profile")
                        }
                    } else {
                        navController.navigate("${ApplicationScreens.HomeScreen.name}/profile")
                    }
                }
                Spacer(modifier = Modifier.width(7.dp))
                AppButton(
                    modifier = Modifier
                        .weight(1f, true)
                        .height(46.dp), text = "Cancel"
                ) {
                    navController.navigate("${ApplicationScreens.HomeScreen.name}/profile")
                }
            }
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun SaveChangedAlertDialog(
    showAlertDialog: MutableState<Boolean>,
    isLoading: MutableState<Boolean>,
    onDiscard: () -> Unit,
    onSave: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AlertDialog(onDismissRequest = {
            showAlertDialog.value = false
        }, title = {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.alert),
                    contentDescription = "Alert icon"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = " Unsaved changes", fontSize = 16.sp)
            }
        }, text = {
            Text(text = "Save changes made to profile?")
        }, confirmButton = {
            AppButton(
                modifier = Modifier.width(95.dp), isLoading = isLoading.value, text = "Save"
            ) {
                onSave.invoke()
            }
        }, modifier = Modifier.height(170.dp), dismissButton = {
            AppButton(modifier = Modifier.width(95.dp), text = "Discard") {
                onDiscard.invoke()
            }
        })
    }
}
