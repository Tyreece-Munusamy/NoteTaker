package com.example.progressupdatedemo.components.textfields

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
fun NameInputTextField(
    modifier: Modifier = Modifier,
    nameState: MutableState<String>,
    label: String = "Full name",
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    isSingleLine: Boolean = true,
    onAction: KeyboardActions = KeyboardActions.Default,
) {
    OutlinedInputTextField(
        modifier = modifier,
        valueState = nameState,
        labelId = label,
        enabled = enabled,
        keyboardType = KeyboardType.Text,
        imeAction = imeAction,
        isSingleLine = isSingleLine,
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.account),
                contentDescription = "Account icon"
            )
        },
        onAction = onAction
    )
}