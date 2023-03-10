package com.example.progressupdatedemo.presentation.component.alert_dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progressupdatedemo.R
import com.example.progressupdatedemo.presentation.component.button.AppButton

@Composable
fun AppAlertDialog(
    alertDialogState: MutableState<Boolean>,
    isLoading: Boolean,
    title: String,
    message: @Composable () -> Unit,
    confirmButtonText: String,
    onConfirmButtonClicked: () -> Unit,
    dismissButtonText: String,
    onDismissButtonClicked: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        androidx.compose.material.AlertDialog(onDismissRequest = { alertDialogState.value = false },
            title = {
                AlertDialogTitle(
                    title = title
                )
            },
            text = {
                message.invoke()
            },
            confirmButton = {
                AlertDialogButton(
                    buttonText = confirmButtonText,
                    onButtonClicked = { onConfirmButtonClicked.invoke() },
                    isLoading = isLoading
                )
            },
            dismissButton = {
                AlertDialogButton(buttonText = dismissButtonText,
                    onButtonClicked = { onDismissButtonClicked.invoke() })
            })
    }
}

@Composable
private fun AlertDialogTitle(title: String) {
    Row(
        horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically
    ) {
        AlertIcon()
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = " $title", fontSize = 16.sp)
    }
}

@Composable
private fun AlertIcon() {
    Icon(
        painter = painterResource(id = R.drawable.alert), contentDescription = "Alert icon"
    )
}

@Composable
private fun AlertDialogButton(
    buttonText: String,
    onButtonClicked: () -> Unit,
    isLoading: Boolean = false,
) {
    AppButton(
        modifier = Modifier.width(95.dp), isLoading = isLoading, text = buttonText
    ) {
        onButtonClicked.invoke()
    }
}