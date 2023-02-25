package com.example.progressupdatedemo.screens.home

import android.annotation.SuppressLint
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
import com.example.progressupdatedemo.components.columns.ColumnWithCenteredContent
import com.example.progressupdatedemo.components.customTopBar.CenterTopAppBar
import com.example.progressupdatedemo.data.DataOrException
import com.example.progressupdatedemo.domain.models.NoteList
import com.example.progressupdatedemo.domain.models.User
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.utils.Constants
import com.example.progressupdatedemo.utils.toJson

@Composable
fun HomeScreen(
    navController: NavController = NavController(LocalContext.current),
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    tab: String,
) {
    val isLoadingNotesAndUserData = homeScreenViewModel.isLoadingNotesAndUserData.value
    val selectedTab = remember {
        mutableStateOf(tab)
    }
    var notesLabel: @Composable () -> Unit = { Box {} }
    var favouritesLabel: @Composable () -> Unit = { Box {} }
    var profileLabel: @Composable () -> Unit = { Box {} }
    val tabTitle: String
    val isFloatingActionButtonVisible = remember {
        mutableStateOf(true)
    }
    val user = remember {
        mutableStateOf(User("", "", "", ""))
    }
    var topBar: @Composable () -> Unit = { Box { } }

    when (selectedTab.value) {
        Constants.HOME_SCREEN_NOTES_TAB -> {
            notesLabel = { BottomBarLabel("Notes") }
            isFloatingActionButtonVisible.value = true
            tabTitle = "Notes"
            topBar = { NotesOrFavouritesTabTopBar(tabTitle) }
        }

        Constants.HOME_SCREEN_FAVOURITES_TAB -> {
            favouritesLabel = { BottomBarLabel("Favourites") }
            isFloatingActionButtonVisible.value = false
            tabTitle = "Favourites"
            topBar = { NotesOrFavouritesTabTopBar(tabTitle) }
        }

        Constants.HOME_SCREEN_PROFILE_TAB -> {
            profileLabel = { BottomBarLabel(text = "Profile") }
            isFloatingActionButtonVisible.value = false
            tabTitle = "Profile"
            topBar = {
                ProfileTabTopBar(tabTitle, navController, user, isLoadingNotesAndUserData)
            }
        }
    }

    HomeScreenScaffold(
        topBar,
        isFloatingActionButtonVisible,
        navController,
        selectedTab,
        notesLabel,
        favouritesLabel,
        profileLabel,
        isLoadingNotesAndUserData,
        homeScreenViewModel,
        user,
    )
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun HomeScreenScaffold(
    topBar: @Composable () -> Unit,
    isFloatingActionButtonVisible: MutableState<Boolean>,
    navController: NavController,
    selectedTab: MutableState<String>,
    notesLabel: @Composable () -> Unit,
    favouritesLabel: @Composable () -> Unit,
    profileLabel: @Composable () -> Unit,
    isLoadingNotesAndUserData: Boolean,
    homeScreenViewModel: HomeScreenViewModel,
    user: MutableState<User>,
) {
    Scaffold(topBar = {
        HomeScreenTopBarWithElevation(topBar)
    }, floatingActionButton = {
        if (isFloatingActionButtonVisible.value) CreateNoteFloatingActionButton(navController)
        else Box {}
    }, bottomBar = {
        HomeScreenBottomBar(selectedTab, notesLabel, favouritesLabel, profileLabel)
    }) {
        if (isLoadingNotesAndUserData) {
            LoadingAnimation()
        } else {
            val noteListOrException = homeScreenViewModel.notes.value
            val userOrException = homeScreenViewModel.user.value
            user.value = homeScreenViewModel.user.value.data!!

            TabContent(
                selectedTab, navController, noteListOrException, homeScreenViewModel, userOrException
            )
        }
    }
}

@Composable
private fun TabContent(
    selectedTab: MutableState<String>,
    navController: NavController,
    loadedNotes: DataOrException<NoteList, Boolean, Exception>,
    homeScreenViewModel: HomeScreenViewModel,
    userOrException: DataOrException<User, Boolean, Exception>,
) {
    when (selectedTab.value) {
        Constants.HOME_SCREEN_NOTES_TAB -> NotesTabContent(
            navController, loadedNotes
        )
        Constants.HOME_SCREEN_FAVOURITES_TAB -> {
            FavouritesTabContent(
                navController, loadedNotes
            )
        }
        Constants.HOME_SCREEN_PROFILE_TAB -> ProfileTabContent(
            navController, homeScreenViewModel, userOrException
        )
    }
}

@Composable
private fun LoadingAnimation() {
    ColumnWithCenteredContent {
        CircularProgressIndicator()
    }
}

@Composable
private fun HomeScreenBottomBar(
    selectedTab: MutableState<String>,
    notesLabel: @Composable () -> Unit,
    favouritesLabel: @Composable () -> Unit,
    profileLabel: @Composable () -> Unit,
) {
    BottomAppBar(modifier = Modifier.height(58.dp), backgroundColor = Color(0xFF432fbf)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarIconButton(
                selectedTab = selectedTab,
                tabOnIconClicked = Constants.HOME_SCREEN_NOTES_TAB,
                modifier = Modifier.weight(1f),
                iconId = R.drawable.notebook,
                notesLabel = notesLabel
            )
            BottomBarIconButton(
                selectedTab = selectedTab,
                tabOnIconClicked = Constants.HOME_SCREEN_FAVOURITES_TAB,
                modifier = Modifier.weight(1f),
                iconId = R.drawable.heart,
                notesLabel = favouritesLabel
            )
            BottomBarIconButton(
                selectedTab = selectedTab,
                tabOnIconClicked = Constants.HOME_SCREEN_PROFILE_TAB,
                modifier = Modifier.weight(1f),
                iconId = R.drawable.account,
                notesLabel = profileLabel
            )
        }
    }
}

@Composable
fun BottomBarIconButton(
    selectedTab: MutableState<String>,
    tabOnIconClicked: String,
    modifier: Modifier,
    iconId: Int,
    notesLabel: @Composable () -> Unit,
) {
    IconButton(onClick = {
        selectedTab.value = tabOnIconClicked
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
            notesLabel.invoke()
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
private fun HomeScreenTopBarWithElevation(topBar: @Composable () -> Unit) {
    TopAppBar(elevation = 2.dp) {
        topBar.invoke()
    }
}

@Composable
private fun ProfileTabTopBar(
    screenTitle: String,
    navController: NavController,
    user: MutableState<User>,
    isLoadingNotesAndUserData: Boolean,
) {
    CenterTopAppBar(title = {
        TopBarTitle(screenTitle)
    }, actions = {
        EditProfileIconButton(navController, user, isLoadingNotesAndUserData)
    })
}


@Composable
private fun EditProfileIconButton(
    navController: NavController,
    user: MutableState<User>,
    isLoadingNotesAndUserData: Boolean,
) {
    IconButton(onClick = {
        navController.navigate(
            Screen.UpdateProfileScreen.withArgs(user.value.toJson().toString())
        )
    }, enabled = !isLoadingNotesAndUserData) {
        if (!isLoadingNotesAndUserData) {
            Icon(
                painter = painterResource(id = R.drawable.account_edit),
                contentDescription = "Logout icon",
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        } else {
            Box {}
        }
    }
}

@Composable
private fun NotesOrFavouritesTabTopBar(screenTitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TopBarTitle(screenTitle)
    }
}

@Composable
private fun TopBarTitle(screenTitle: String) {
    Text(
        text = screenTitle, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White
    )
}

@Composable
private fun BottomBarLabel(text: String) {
    Text(text = text, fontSize = 14.sp, color = Color(0xFFd5d4ee))
}

@Composable
private fun CreateNoteFloatingActionButton(navController: NavController) {
    FloatingActionButton(
        modifier = Modifier.size(65.dp),
        onClick = { navController.navigate(Screen.CreateNoteScreen.route) },
        backgroundColor = MaterialTheme.colors.primary
    ) {
        PlusIcon()
    }
}

@Composable
private fun PlusIcon() {
    Icon(
        painter = painterResource(id = R.drawable.plus),
        contentDescription = "Add icon",
        tint = Color.White
    )
}

