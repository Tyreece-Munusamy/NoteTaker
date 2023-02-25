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
import com.example.progressupdatedemo.R
import com.example.progressupdatedemo.components.buttons.AppButton
import com.example.progressupdatedemo.components.columns.ColumnWithCenteredContent
import com.example.progressupdatedemo.data.DataOrException
import com.example.progressupdatedemo.domain.models.LoginDetailsHolder
import com.example.progressupdatedemo.domain.models.User
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.utils.toJson

@Composable
fun ProfileTabContent(
    navController: NavController,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    userOrException: DataOrException<User, Boolean, Exception>,
) {
    val user = remember {
        mutableStateOf(User())
    }
    val isLoggingOut = homeScreenViewModel.isLoggingOut

    if (userOrException.exception != null) {
        ColumnWithCenteredContent {
            Text(text = "Error loading user details")
        }
    } else {
        user.value = userOrException.data!!
        ProfileTabScaffold(user, isLoggingOut, homeScreenViewModel, navController)
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun ProfileTabScaffold(
    user: MutableState<User>,
    isLoggingOut: MutableState<Boolean>,
    homeScreenViewModel: HomeScreenViewModel,
    navController: NavController,
) {
    Scaffold {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Divider(color = Color.White, thickness = 1.dp)
            ProfileSummary(user)
            Spacer(modifier = Modifier.height(15.dp))
            FirstNameField(user)
            LastNameField(user)
            EmailField(user)
            Spacer(modifier = Modifier.height(5.dp))
            LogOutButton(isLoggingOut, homeScreenViewModel, navController)
        }
    }
}

@Composable
private fun LogOutButton(
    isLoggingOut: MutableState<Boolean>,
    homeScreenViewModel: HomeScreenViewModel,
    navController: NavController,
) {
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
            isLoading = isLoggingOut.value
        ) {
            homeScreenViewModel.signOut()
            navigateToLoginScreenWithNoPresetValues(navController)
        }
    }
}

private fun navigateToLoginScreenWithNoPresetValues(navController: NavController) {
    navController.navigate(
        Screen.LoginScreen.withArgs(LoginDetailsHolder().toJson().toString())
    )
}

@Composable
private fun FirstNameField(user: MutableState<User>) {
    ProfileDetailsField(label = "First Name", profileDetails = user.value.firstName.toString())
}

@Composable
private fun LastNameField(user: MutableState<User>) {
    ProfileDetailsField(label = "Last Name", profileDetails = user.value.lastName.toString())
}

@Composable
private fun EmailField(user: MutableState<User>) {
    ProfileDetailsField(label = "Email", profileDetails = user.value.email.toString())
}

@Composable
private fun ProfileDetailsField(label: String, profileDetails: String) {
    Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        ProfileDetailsFieldLabel(label)
        ProfilesDetailsFieldText(profileDetails)
        Divider(color = Color.Black.copy(0.2f))
    }
}

@Composable
private fun ProfilesDetailsFieldText(profileDetails: String) {
    Text(
        modifier = Modifier.padding(start = 10.dp),
        text = profileDetails,
        style = MaterialTheme.typography.caption,
        fontWeight = FontWeight(525),
        fontSize = 18.sp
    )
}

@Composable
private fun ProfileDetailsFieldLabel(label: String) {
    Text(text = label, fontSize = 11.sp, style = MaterialTheme.typography.caption)
}

@Composable
private fun ProfileSummary(user: MutableState<User>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.primary, MaterialTheme.colors.primaryVariant
                    )
                )
            )
            .padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AccountIcon()
        Spacer(modifier = Modifier.height(20.dp))
        UserFullNameText(user)
    }
}

@Composable
private fun UserFullNameText(user: MutableState<User>) {
    Text(
        text = "${user.value.firstName} ${user.value.lastName}",
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White
    )
}

@Composable
private fun AccountIcon() {
    Icon(
        painter = painterResource(id = R.drawable.account_circle),
        contentDescription = "Account circle icon",
        modifier = Modifier.size(125.dp),
        tint = Color.White
    )
}