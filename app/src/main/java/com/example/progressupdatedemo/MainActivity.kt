package com.example.progressupdatedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.progressupdatedemo.components.ColumnWithCenteredContent
import com.example.progressupdatedemo.navigation.ApplicationNavigation
import com.example.progressupdatedemo.ui.theme.ProgressUpdateDemoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProgressUpdateDemoTheme {
                NoteApp()
            }
        }
    }
}

@Composable
fun NoteApp() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        ColumnWithCenteredContent {
            ApplicationNavigation()
        }
    }
}

