package com.example.progressupdatedemo.components.icons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progressupdatedemo.R
import com.example.progressupdatedemo.core.utils.TestTags

@Composable
fun ApplicationIcon(modifier: Modifier = Modifier, color: Color = Color.Black) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.notebook),
            contentDescription = "",
            modifier = Modifier
                .size(80.dp)
                .testTag(TestTags.SPLASH_SCREEN_APPLICATION_ICON_ICON),
            tint = color
        )
        Text(
            text = "Note Taker",
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.testTag(TestTags.SPLASH_SCREEN_APPLICATION_ICON_TEXT)
        )
    }
}