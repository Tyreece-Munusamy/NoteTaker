package com.example.progressupdatedemo.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
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
import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.model.User
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.presentation.component.button.AppButton
import com.example.progressupdatedemo.presentation.component.column.ColumnWithCenteredContent
import com.example.progressupdatedemo.presentation.component.loading_animation.CircularLoadingAnimation

@Composable
fun ProfileTabContent(
    navController: NavController,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val isLoadingUserData = remember(homeScreenViewModel.getUserUseCaseResponseState.value) {
        homeScreenViewModel.getUserUseCaseResponseState.value is Response.Loading
    }
    val getUserUseCaseResponse = homeScreenViewModel.getUserUseCaseResponseState.value
    val onSignOut: () -> Unit = { homeScreenViewModel.signOut() }

    if (isLoadingUserData) {
        CircularLoadingAnimation()
    } else {
        UserProfileOrErrorMessage(getUserUseCaseResponse, navController, onSignOut)
    }
}

@Composable
fun UserProfileOrErrorMessage(
    getUserUseCaseResponse: Response<User>,
    navController: NavController,
    onSignOut: () -> Unit
) {
    if (getUserUseCaseResponse.message != null || getUserUseCaseResponse.data == null) {
        ErrorLoadingUserDataMessage(getUserUseCaseResponse.message)
    } else {
        val user = getUserUseCaseResponse.data!!
        UserProfile(user, navController, onSignOut)
    }
}

@Composable
private fun ErrorLoadingUserDataMessage(errorMessage: String?) {
    ColumnWithCenteredContent {
        Text(text = errorMessage ?: "Error retrieving user data!")
    }
}

@Composable
private fun UserProfile(user: User, navController: NavController, onSignOut: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top
    ) {
        Divider(color = Color.White, thickness = 1.dp)

        ProfileSummary(user)

        Spacer(modifier = Modifier.height(15.dp))

        FirstNameField(user.firstName!!)

        LastNameField(user.lastName!!)

        EmailField(user.email!!)

        Spacer(modifier = Modifier.height(5.dp))

        LogOutButton(navController, onSignOut)
    }
}

@Composable
private fun ProfileSummary(user: User) {
    val brushColours = listOf(
        MaterialTheme.colors.primary, MaterialTheme.colors.primaryVariant
    )
    val brush = Brush.verticalGradient(brushColours)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(brush)
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AccountIcon()
        Spacer(modifier = Modifier.height(20.dp))
        UserFullNameText(user)
    }
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

@Composable
private fun UserFullNameText(user: User) {
    Text(
        text = "${user.firstName} ${user.lastName}",
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White
    )
}

@Composable
private fun FirstNameField(firstName: String) {
    ProfileDetailField(label = "First Name", profileDetail = firstName)
}

@Composable
private fun LastNameField(lastName: String) {
    ProfileDetailField(label = "Last Name", profileDetail = lastName)
}

@Composable
private fun EmailField(email: String) {
    ProfileDetailField(label = "Email", profileDetail = email)
}

@Composable
private fun ProfileDetailField(label: String, profileDetail: String) {
    Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        ProfileDetailsFieldLabel(label)
        ProfileDetailFieldText(profileDetail)
        Divider(color = Color.Black.copy(0.2f))
    }
}

@Composable
private fun ProfileDetailFieldText(profileDetails: String) {
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
private fun LogOutButton(
    navController: NavController,
    onSignOut: () -> Unit,
) {
    val onLogOutButtonClicked: () -> Unit = {
        onSignOut.invoke()
        navigateToLoginScreen(navController)
    }

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
        ) {
            onLogOutButtonClicked.invoke()
        }
    }
}

private fun navigateToLoginScreen(navController: NavController) {
    navController.navigate(Screen.LoginScreen.route)
}