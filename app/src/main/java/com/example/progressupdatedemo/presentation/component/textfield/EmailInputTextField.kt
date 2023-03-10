package com.example.progressupdatedemo.presentation.component.textfield

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.progressupdatedemo.R

@Composable
fun EmailInputTextField(
    modifier: Modifier = Modifier,
    emailState: MutableState<String>,
    labelId: String = "Email",
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default,
) {
    OutlinedInputTextField(
        modifier = modifier,
        valueState = emailState,
        labelId = labelId,
        icon = {
            Icon(painterResource(id = R.drawable.email), "Email icon")
        },
        enabled = enabled,
        keyboardType = KeyboardType.Email,
        imeAction = imeAction,
        isSingleLine = true,
        onAction = onAction
    )
}