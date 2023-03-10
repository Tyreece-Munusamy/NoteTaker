package com.example.progressupdatedemo.presentation.screen.home

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.progressupdatedemo.R
import com.example.progressupdatedemo.core.constants.Constants
import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.presentation.component.top_bar.CenteredTextTopAppBar

@Composable
fun HomeScreen(
    navController: NavController = NavController(LocalContext.current),
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    selectedTab: String,
) {
    val selectedTabState = remember { mutableStateOf(selectedTab) }
    var notesIconButtonLabel: @Composable () -> Unit = { Box {} }
    var favouritesIconButtonLabel: @Composable () -> Unit = { Box {} }
    var profileIconButtonLabel: @Composable () -> Unit = { Box {} }
    val tabTitle: String
    val isFloatingActionButtonVisibleState = remember { mutableStateOf(true) }
    var topBar: @Composable () -> Unit = { Box { } }
    val isLoadingUserData = remember(homeScreenViewModel.getUserUseCaseResponseState.value) {
        homeScreenViewModel.getUserUseCaseResponseState.value is Response.Loading
    }

    BackHandler(true) { }
    when (selectedTabState.value) {
        Constants.HOME_SCREEN_NOTES_TAB -> {
            tabTitle = "Notes"
            notesIconButtonLabel = { BottomBarIconButtonLabel(tabTitle) }
            isFloatingActionButtonVisibleState.value = true
            topBar = { BasicTabTopBar(tabTitle) }
        }

        Constants.HOME_SCREEN_FAVOURITES_TAB -> {
            tabTitle = "Favourites"
            favouritesIconButtonLabel = { BottomBarIconButtonLabel(tabTitle) }
            isFloatingActionButtonVisibleState.value = false
            topBar = { BasicTabTopBar(tabTitle) }
        }

        Constants.HOME_SCREEN_PROFILE_TAB -> {
            tabTitle = "Profile"
            profileIconButtonLabel = { BottomBarIconButtonLabel(tabTitle) }
            isFloatingActionButtonVisibleState.value = false
            topBar = {
                ProfileTabTopBar(tabTitle, navController, isLoadingUserData)
            }
        }
    }

    HomeScreenScaffold(
        topBar,
        isFloatingActionButtonVisibleState.value,
        navController,
        selectedTabState,
        notesIconButtonLabel,
        favouritesIconButtonLabel,
        profileIconButtonLabel,
    )
}

@Composable
private fun BottomBarIconButtonLabel(text: String) {
    Text(text = text, fontSize = 14.sp, color = Color(0xFFd5d4ee))
}

@Composable
private fun BasicTabTopBar(tabTitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TopBarTitle(tabTitle)
    }
}

@Composable
private fun TopBarTitle(tabTitle: String) {
    Text(
        text = tabTitle, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White
    )
}

@Composable
private fun ProfileTabTopBar(
    tabTitle: String,
    navController: NavController,
    isLoadingUserData: Boolean,
) {
    CenteredTextTopAppBar(title = { TopBarTitle(tabTitle) },
        actions = { EditProfileIconButton(navController, isLoadingUserData) })
}

@Composable
private fun EditProfileIconButton(
    navController: NavController,
    isLoadingUserData: Boolean,
) {
    IconButton(onClick = {
        navigateToUpdateProfileScreen(navController)
    }) {
        if (!isLoadingUserData) AccountEditIcon() else Box {}
    }
}

private fun navigateToUpdateProfileScreen(navController: NavController) {
    navController.navigate(Screen.UpdateProfileScreen.route)
}

@Composable
private fun AccountEditIcon() {
    Icon(
        painter = painterResource(R.drawable.account_edit),
        contentDescription = "Account Edit icon",
        modifier = Modifier.size(32.dp),
        tint = Color.White
    )
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun HomeScreenScaffold(
    topBar: @Composable () -> Unit,
    isFloatingActionButtonVisible: Boolean,
    navController: NavController,
    selectedTabState: MutableState<String>,
    notesIconButtonLabel: @Composable () -> Unit,
    favouritesIconButtonLabel: @Composable () -> Unit,
    profileIconButtonLabel: @Composable () -> Unit,
) {
    Scaffold(topBar = {
        HomeScreenTopBar(topBar)
    }, floatingActionButton = {
        if (isFloatingActionButtonVisible) CreateNoteFloatingActionButton(navController)
        else Box {}
    }, bottomBar = {
        HomeScreenBottomBar(
            selectedTabState,
            notesIconButtonLabel,
            favouritesIconButtonLabel,
            profileIconButtonLabel
        )
    }) {
        TabContent(
            selectedTabState.value,
            navController,
        )
    }
}

@Composable
private fun HomeScreenTopBar(topBar: @Composable () -> Unit) {
    TopAppBar(elevation = 2.dp) {
        topBar.invoke()
    }
}

@Composable
private fun CreateNoteFloatingActionButton(navController: NavController) {
    FloatingActionButton(
        modifier = Modifier.size(65.dp),
        onClick = { navigateToCreateNoteScreen(navController) },
        backgroundColor = MaterialTheme.colors.primary
    ) {
        PlusIcon()
    }
}

private fun navigateToCreateNoteScreen(navController: NavController) {
    navController.navigate(Screen.CreateNoteScreen.route)
}

@Composable
private fun PlusIcon() {
    Icon(
        painter = painterResource(id = R.drawable.plus),
        contentDescription = "Add icon",
        tint = Color.White
    )
}

@Composable
private fun HomeScreenBottomBar(
    selectedTabState: MutableState<String>,
    notesIconButtonLabel: @Composable () -> Unit,
    favouritesIconButtonLabel: @Composable () -> Unit,
    profileIconButtonLabel: @Composable () -> Unit,
) {
    BottomAppBar(modifier = Modifier.height(58.dp), backgroundColor = Color(0xFF432fbf)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarIconButton(
                selectedTabState = selectedTabState,
                tabOnIconButtonClicked = Constants.HOME_SCREEN_NOTES_TAB,
                modifier = Modifier.weight(1f),
                iconId = R.drawable.notebook,
                iconButtonLabel = notesIconButtonLabel
            )
            BottomBarIconButton(
                selectedTabState = selectedTabState,
                tabOnIconButtonClicked = Constants.HOME_SCREEN_FAVOURITES_TAB,
                modifier = Modifier.weight(1f),
                iconId = R.drawable.heart,
                iconButtonLabel = favouritesIconButtonLabel
            )
            BottomBarIconButton(
                selectedTabState = selectedTabState,
                tabOnIconButtonClicked = Constants.HOME_SCREEN_PROFILE_TAB,
                modifier = Modifier.weight(1f),
                iconId = R.drawable.account,
                iconButtonLabel = profileIconButtonLabel
            )
        }
    }
}

@Composable
fun BottomBarIconButton(
    selectedTabState: MutableState<String>,
    tabOnIconButtonClicked: String,
    modifier: Modifier,
    iconId: Int,
    iconButtonLabel: @Composable () -> Unit,
) {
    IconButton(onClick = {
        selectedTabState.value = tabOnIconButtonClicked
    }) {
        Column(
            modifier = modifier
                .fillMaxHeight()
                .padding(7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TabIconById(id = iconId)
            Spacer(modifier = Modifier.height(1.dp))
            iconButtonLabel.invoke()
        }
    }
}

@Composable
private fun TabIconById(id: Int, color: Color = Color(0xFFd5d4ee)) {
    Icon(
        painter = painterResource(id = id), contentDescription = "Account icon", tint = color
    )
}

@Composable
private fun TabContent(
    selectedTab: String,
    navController: NavController,
) {
    when (selectedTab) {
        Constants.HOME_SCREEN_NOTES_TAB -> NotesTabContent(
            navController
        )
        Constants.HOME_SCREEN_FAVOURITES_TAB -> {
            FavouritesTabContent(
                navController
            )
        }
        Constants.HOME_SCREEN_PROFILE_TAB -> ProfileTabContent(
            navController
        )
    }
}

