package com.example.progressupdatedemo.presentation.component.button

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NegativePositiveButtonRow(
    negativeButtonLabel: String,
    onNegativeButtonClicked: () -> Unit,
    positiveButtonLabel: String,
    onPositiveButtonClicked: () -> Unit,
    isPositiveButtonLoading: Boolean,
) {
    Row {
        AppButton(
            modifier = Modifier
                .weight(1f, true)
                .height(46.dp), text = negativeButtonLabel
        ) {
            onNegativeButtonClicked.invoke()
        }
        Spacer(modifier = Modifier.width(7.dp))
        AppButton(
            modifier = Modifier
                .weight(1f, true)
                .height(46.dp),
            text = positiveButtonLabel,
            isLoading = isPositiveButtonLoading
        ) {
            onPositiveButtonClicked.invoke()
        }
    }
}