package com.example.progressupdatedemo.components.textfields

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.progressupdatedemo.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    searchState: MutableState<String>,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager,
) {
    OutlinedInputTextField(modifier = Modifier.padding(
        top = 10.dp, start = 8.dp, end = 9.dp, bottom = 0.dp
    ), valueState = searchState, labelId = "Search", icon = {
        MagnifyingGlassIcon()
    }, imeAction = ImeAction.Done, onAction = KeyboardActions {
        keyboardController?.hide()
        focusManager.clearFocus()
    }, isSingleLine = true
    )
}

@Composable
private fun MagnifyingGlassIcon() {
    Icon(
        painter = painterResource(id = R.drawable.magnify),
        contentDescription = "Magnifying glass icon"
    )
}