package com.example.view.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.model.entities.Header
import com.example.model.entities.Product
import java.text.SimpleDateFormat
import java.util.Locale

val markedColor = Color(0xFFD1C4E9)

@Composable
fun RatingBar(
    rating: Double,
    stars: Int = 5,
    starSize: Int = 24,
    starsColor: Color = Color(0xFFFFA500),
    borderColor: Color = Color(0xFFFFA500)
) {
    Row {
        for (i in 1..stars) {
            val fillType = when {
                rating >= i -> Icons.Filled.Star
                rating >= i - 0.5 -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Filled.StarOutline
            }

            Box(
                modifier = Modifier.size(starSize.dp)
            ) {
                Icon(
                    imageVector = fillType,
                    contentDescription = null,
                    tint = starsColor,
                    modifier = Modifier.matchParentSize()
                )

                Icon(
                    imageVector = Icons.Filled.StarOutline,
                    contentDescription = null,
                    tint = borderColor,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}

@Composable
fun ProductImageFromUrl(
    product: Product,
    modifier: Modifier = Modifier,
    imageSize: Dp = 64.dp
) {
    val backgroundColor = try {
        Color(android.graphics.Color.parseColor("#${product.colorCode}"))
    } catch (e: IllegalArgumentException) {
        when (product.color.lowercase()) {
            "blue" -> Color.Blue
            "red" -> Color.Red
            "green" -> Color.Green
            "yellow" -> Color.Yellow
            "gray" -> Color.Gray
            else -> Color.LightGray
        }
    }

    Box(
        modifier = modifier
            .size(imageSize)
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
    ) {
        AsyncImage(
            model = product.imageURL,
            contentDescription = "Product Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun Footer(onClick: () -> Unit) {
    Text(
        text = "© 2016 Check24",
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() },
        textAlign = TextAlign.Center,
        color = Color.Gray
    )
}

@Composable
fun Header(
    header: Header?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Blue)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = header?.headerTitle ?: "App Title...",
            color = Color.White
        )
        Text(
            text = header?.headerDescription ?: "Subtitle...",
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

fun formatTimestamp(timeInSecond: Long): String {
    val formattedDate = SimpleDateFormat(
        "dd.MM.yyyy",
        Locale.getDefault()
    ).format(timeInSecond * 1000L)
    return formattedDate
}