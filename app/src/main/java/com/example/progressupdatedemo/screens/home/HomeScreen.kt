package com.example.progressupdatedemo.screens.home

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.progressupdatedemo.DoublePressToExit
import com.example.progressupdatedemo.R
import com.example.progressupdatedemo.components.CenterTopAppBar
import com.example.progressupdatedemo.components.ColumnWithCenteredContent
import com.example.progressupdatedemo.components.NoNotesIcon
import com.example.progressupdatedemo.components.textfields.GeneralOutlinedInputTextField
import com.example.progressupdatedemo.data.DataOrException
import com.example.progressupdatedemo.models.Note
import com.example.progressupdatedemo.models.NoteList
import com.example.progressupdatedemo.models.User
import com.example.progressupdatedemo.navigation.ApplicationScreens
import com.example.progressupdatedemo.utils.toJson

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController = NavController(LocalContext.current),
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    tab: String,
) {
    val isLoading = remember(homeScreenViewModel.user.value, homeScreenViewModel.notes.value) {
        homeScreenViewModel.user.value.loading == true && homeScreenViewModel.notes.value.loading == true
    }
    val selectedIcon = remember {
        mutableStateOf(tab)
    }
    var notesLabel: @Composable () -> Unit = { Box {} }
    var favouritesLabel: @Composable () -> Unit = { Box {} }
    var profileLabel: @Composable () -> Unit = { Box {} }

    val screenTitle: String

    val showFloatingActionButton = remember {
        mutableStateOf(true)
    }

    val user = remember {
        mutableStateOf(User("","","",""))
    }
    var topBar: @Composable () -> Unit = { Box { } }

    when (selectedIcon.value) {
        "notes" -> {
            notesLabel = { Text(text = "Notes", fontSize = 14.sp, color = Color(0xFFd5d4ee)) }
            showFloatingActionButton.value = true
            screenTitle = "Notes"
            topBar = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = screenTitle, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
        "favourites" -> {
            favouritesLabel =
                { Text(text = "Favourites", fontSize = 14.sp, color = Color(0xFFd5d4ee)) }
            showFloatingActionButton.value = false
            screenTitle = "Favourites"
            topBar = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = screenTitle, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
        "profile" -> {
            profileLabel = { Text(text = "Profile", fontSize = 14.sp, color = Color(0xFFd5d4ee)) }
            showFloatingActionButton.value = false
            screenTitle = "Profile"
            topBar = {
                CenterTopAppBar(title = {
                    Text(
                        text = screenTitle,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }, actions = {
                    IconButton(onClick = {
                        navController.navigate(
                            "${ApplicationScreens.UpdateProfileScreen.name}/${
                                user.value.toJson().toString()
                            }"
                        )
                    }, enabled = !isLoading) {
                        if (!isLoading) {
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
                })
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(elevation = 2.dp) {
            topBar.invoke()
        }
    }, floatingActionButton = {
        if (showFloatingActionButton.value) FloatingActionButton(navController) else Box {}
    }, bottomBar = {
        BottomAppBar(modifier = Modifier.height(58.dp), backgroundColor = Color(0xFF432fbf)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    selectedIcon.value = "notes"
                }) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.notebook),
                            contentDescription = "Notebook icon",
                            tint = Color(0xFFd5d4ee)
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        notesLabel.invoke()
                    }
                }
                IconButton(onClick = {
                    selectedIcon.value = "favourites"
                }) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.heart),
                            contentDescription = "Heart icon",
                            tint = Color(0xFFd5d4ee)
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        favouritesLabel.invoke()
                    }
                }
                IconButton(onClick = {
                    selectedIcon.value = "profile"
                }) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.account),
                            contentDescription = "Account icon",
                            tint = Color(0xFFd5d4ee)
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        profileLabel.invoke()
                    }
                }
            }
        }
    }) {
        if (isLoading) {
            ColumnWithCenteredContent {
                CircularProgressIndicator()
            }
        } else {
            val notesState: MutableState<List<Note?>> = remember {
                mutableStateOf(emptyList())
            }

            val loadedNotes = homeScreenViewModel.notes.value
            user.value = homeScreenViewModel.user.value.data!!

            when (selectedIcon.value) {
                "notes" -> HomeScreenContent(
                    navController = navController, notesState, loadedNotes
                )
                "favourites" -> {
                    FavouritesScreenContent(
                        navController = navController, loadedNotes
                    )
                }
                "profile" -> ProfileScreenContent(navController, homeScreenViewModel, user)
            }

        }
    }
}

@Composable
private fun FloatingActionButton(navController: NavController) {
    FloatingActionButton(
        modifier = Modifier.size(65.dp),
        onClick = { navController.navigate(ApplicationScreens.CreateNoteScreen.name) },
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Icon(
            painter = painterResource(id = R.drawable.plus),
            contentDescription = "Add icon",
            tint = Color.White
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreenContent(
    navController: NavController,
    notesState: MutableState<List<Note?>>,
    loadedNotes: DataOrException<NoteList, Boolean, Exception>,
) {
    val searchState = remember {
        mutableStateOf("")
    }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    if (loadedNotes.loading == true) {
        ColumnWithCenteredContent {
            CircularProgressIndicator()
        }
    } else {
        notesState.value = loadedNotes.data?.notes!!
        if (notesState.value.isEmpty()) {
            ColumnWithCenteredContent {
                NoNotesIcon()
                Spacer(modifier = Modifier.height(100.dp))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 38.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                GeneralOutlinedInputTextField(modifier = Modifier.padding(
                    top = 10.dp, start = 8.dp, end = 9.dp, bottom = 0.dp
                ), textState = searchState, labelId = "Search", {
                    Icon(
                        painter = painterResource(id = R.drawable.magnify),
                        contentDescription = "Magnifying glass icon"
                    )
                }, imeAction = ImeAction.Done, onAction = KeyboardActions {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                })
                if (searchState.value.isNotEmpty()) {
                    notesState.value = loadedNotes.data?.notes?.filter { note ->
                        note.title!!.lowercase().contains(searchState.value.lowercase())
                    }!!
                }
                LazyColumn(
                    modifier = Modifier.padding(
                        start = 18.dp, end = 18.dp, bottom = 18.dp
                    )
                ) {
                    items(notesState.value) { note ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .padding(bottom = 15.dp)
                                .clickable {
                                    val jsonNote = note.toJson()!!
                                    navController.navigate("${ApplicationScreens.NoteDetailsScreen.name}/$jsonNote/notes")
                                },
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
                            elevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = note?.title.toString())
                                    Text(
                                        text = note?.message.toString(),
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color.Black.copy(alpha = 0.5f),
                                        fontSize = 13.sp,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}