package com.example.progressupdatedemo.presentation.component.loading_animation

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progressupdatedemo.R

@Preview(showBackground = true)
@Composable
fun LoadingShimmerEffect() {

    //These colors will be used on the brush. The lightest color should be in the middle

    val gradient = listOf(
        Color.LightGray.copy(alpha = 0.9f), //darker grey (90% opacity)
        Color.LightGray.copy(alpha = 0.3f), //lighter grey (30% opacity)
        Color.LightGray.copy(alpha = 0.9f)
    )

    val transition = rememberInfiniteTransition() // animate infinite times

    val translateAnimation = transition.animateFloat( //animate the transition
        initialValue = 0f, targetValue = 1000f, animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000, // duration for the animation
                easing = LinearEasing
            )
        )
    )
    val brush = linearGradient(
        colors = gradient, start = Offset(200f, 200f), end = Offset(
            x = translateAnimation.value, y = translateAnimation.value
        )
    )
    Column {
        Spacer(modifier = Modifier.height(5.dp))
        Card(
            modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp, bottom = 0.dp),
            elevation = 2.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(54.dp)
                    .fillMaxWidth()
                    .border(1.dp, color = Color.LightGray)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.magnify),
                        contentDescription = "Magnifying glass icon",
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {}
                    Text(
                        text = "Search", color = Color.LightGray, fontSize = 18.sp
                    )
                }
            }
        }
        ShimmerNoteCard(brush = brush)
        ShimmerNoteCard(brush = brush)
        ShimmerNoteCard(brush = brush)
    }
}

@Composable
fun ShimmerNoteCard(brush: Brush) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 15.dp, end = 15.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .fillMaxWidth(fraction = 0.5f)
                        .background(brush)
                )

                Spacer(modifier = Modifier.height(8.dp)) //creates an empty space between
                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .fillMaxWidth(fraction = 0.9f)
                        .background(brush)
                )
            }
        }
    }
}