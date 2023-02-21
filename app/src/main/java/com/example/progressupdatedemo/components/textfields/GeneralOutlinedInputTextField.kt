package com.example.progressupdatedemo.components.textfields

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun GeneralOutlinedInputTextField(
    modifier: Modifier = Modifier,
    textState: MutableState<String>,
    labelId: String = "Text",
    icon: @Composable () -> Unit = {},
    enabled: Boolean = true,
    isSingleLine: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text,
    onAction: KeyboardActions = KeyboardActions.Default,
) {
    OutlinedInputTextField(
        modifier = modifier,
        valueState = textState,
        labelId = labelId,
        icon = {
            icon.invoke()
        },
        enabled = enabled,
        keyboardType = keyboardType,
        imeAction = imeAction,
        isSingleLine = isSingleLine,
        onAction = onAction
    )
}