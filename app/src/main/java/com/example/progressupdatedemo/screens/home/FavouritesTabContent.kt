package com.example.progressupdatedemo.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.progressupdatedemo.R
import com.example.progressupdatedemo.components.ColumnWithCenteredContent
import com.example.progressupdatedemo.components.icons.NoFavouritesIcon
import com.example.progressupdatedemo.components.textfields.OutlinedInputTextField
import com.example.progressupdatedemo.data.DataOrException
import com.example.progressupdatedemo.models.Note
import com.example.progressupdatedemo.models.NoteList
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.utils.toJson

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FavouritesTabContent(
    navController: NavController,
    loadedNotes: DataOrException<NoteList, Boolean, Exception>,
) {
    val favouriteNotes = remember {
        mutableStateOf(emptyList<Note>())
    }

    val searchState = remember {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    if (loadedNotes.loading == true) {
        ColumnWithCenteredContent {
            CircularProgressIndicator()
        }
    } else {
        favouriteNotes.value = loadedNotes.data?.notes!!.filter {
            note -> note.isFavourite
        }
        if (favouriteNotes.value.isEmpty()) {
            ColumnWithCenteredContent {
                NoFavouritesIcon()
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
                OutlinedInputTextField(modifier = Modifier.padding(
                    top = 10.dp, start = 8.dp, end = 9.dp, bottom = 0.dp
                ), valueState = searchState, labelId = "Search",icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.magnify),
                        contentDescription = "Magnifying glass icon"
                    )
                }, imeAction = ImeAction.Done, onAction = KeyboardActions {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }, isSingleLine = true)
                if (searchState.value.isNotEmpty()) {
                    favouriteNotes.value = favouriteNotes.value.filter { note ->
                        note.title!!.lowercase().contains(searchState.value.lowercase())
                    }
                }
                LazyColumn(
                    modifier = Modifier.padding(
                        start = 18.dp, end = 18.dp, bottom = 18.dp
                    )
                ) {
                    items(favouriteNotes.value) { note ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .padding(bottom = 15.dp)
                                .clickable {
                                    val jsonNote = note.toJson()!!
                                    navController.navigate(Screen.NoteDetailsScreen.withArgs(jsonNote, "favourites"))
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
                                    Text(text = note.title.toString())
                                    Text(
                                        text = note.message.toString(),
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