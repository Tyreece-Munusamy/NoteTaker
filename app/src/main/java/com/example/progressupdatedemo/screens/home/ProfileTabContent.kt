package com.example.progressupdatedemo.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.progressupdatedemo.components.ColumnWithCenteredContent
import com.example.progressupdatedemo.components.buttons.AppButton
import com.example.progressupdatedemo.models.LoginDetailsHolder
import com.example.progressupdatedemo.models.User
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.utils.toJson

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfileTabContent(
    navController: NavController,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    userState: MutableState<User>,
) {
    val loadedUser = homeScreenViewModel.user.value
    val isLoading = remember {
        mutableStateOf(false)
    }
    if (loadedUser.loading == true || userState.value.firstName == null) {
        ColumnWithCenteredContent {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(bottomBar = { BottomAppBar {} }) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Divider(color = Color.White, thickness = 1.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colors.primary,
                                    MaterialTheme.colors.primaryVariant
                                )
                            )
                        )
                        .padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = com.example.progressupdatedemo.R.drawable.account_circle),
                        contentDescription = "Account circle icon",
                        modifier = Modifier.size(125.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "${userState.value.firstName} ${userState.value.lastName}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "First Name",
                        fontSize = 11.sp,
                        style = MaterialTheme.typography.caption
                    )
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = userState.value.firstName.toString(),
                        style = MaterialTheme.typography.caption,
                        fontWeight = FontWeight(550),
                        fontSize = 18.sp
                    )
                    Divider(color = Color.Black.copy(0.2f))
                }

                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Last Name",
                        fontSize = 11.sp,
                        style = MaterialTheme.typography.caption
                    )
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = userState.value.lastName.toString(),
                        style = MaterialTheme.typography.caption,
                        fontWeight = FontWeight(550),
                        fontSize = 18.sp
                    )
                    Divider(color = Color.Black.copy(0.2f))
                }

                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = "Email", fontSize = 11.sp, style = MaterialTheme.typography.caption)
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = userState.value.email.toString(),
                        style = MaterialTheme.typography.caption,
                        fontWeight = FontWeight(525),
                        fontSize = 18.sp
                    )
                    Divider(color = Color.Black.copy(0.2f))
                }

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppButton(
                        text = "Log out",
                        modifier = Modifier.weight(1f),
                        backgroundColor = Color(0xFFDe1313),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        isLoading = isLoading.value
                    ) {
                        isLoading.value = true
                        homeScreenViewModel.signOut()
                        navController.navigate(
                            Screen.LoginScreen.withArgs(LoginDetailsHolder().toJson().toString())
                        )
                        isLoading.value = false
                    }
                }

            }
        }
    }
}