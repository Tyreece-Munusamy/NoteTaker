package com.example.progressupdatedemo.presentation.component.button

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = 16.sp,
    backgroundColor: Color = MaterialTheme.colors.primary,
    text: String,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        onClick = { onClick.invoke() },
        modifier = modifier.width(110.dp).height(48.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            ButtonText(text, fontSize, fontWeight)
        }
    }
}

@Composable
private fun ButtonText(
    text: String,
    fontSize: TextUnit,
    fontWeight: FontWeight,
) {
    Text(
        text = text,
        color = Color.White,
        fontSize = fontSize,
        style = MaterialTheme.typography.caption,
        fontWeight = fontWeight
    )
}

@Composable
private fun CircularProgressIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp
    )
}
