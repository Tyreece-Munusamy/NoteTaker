package com.example.progressupdatedemo.presentation.component.button

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthenticationButton(
    label: String,
    isLoading: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppButton(
            modifier = Modifier
                .padding(bottom = 10.dp, start = 10.dp, end = 10.dp, top = 10.dp)
                .height(56.dp)
                .fillMaxWidth(),
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            isLoading = isLoading,
            enabled = isEnabled
        ) {
            onClick.invoke()
        }
    }
}