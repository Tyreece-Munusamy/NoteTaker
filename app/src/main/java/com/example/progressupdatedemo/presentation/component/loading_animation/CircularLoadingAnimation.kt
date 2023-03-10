package com.example.progressupdatedemo.presentation.component.loading_animation

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import com.example.progressupdatedemo.presentation.component.column.ColumnWithCenteredContent

@Composable
fun CircularLoadingAnimation() {
    ColumnWithCenteredContent {
        CircularProgressIndicator()
    }
}

