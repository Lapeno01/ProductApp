package com.example.view.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.model.entities.Product
import com.example.view.ProductDetailActivity
import com.example.view.WebViewActivity
import com.example.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductListView(viewModel: ProductViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val header by viewModel.header.collectAsState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        refreshingOffset = 10.dp,
        onRefresh = { viewModel.refreshProducts() }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Header(header = header)
            val disableFilters = (errorMessage != null || isRefreshing)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Alle", "Verfügbar", "Vorgemerkt").forEach { filter ->
                    Button(
                        onClick = { viewModel.updateFilter(filter) },
                        modifier = Modifier.padding(4.dp),
                        enabled = !disableFilters
                    ) {
                        Text(text = filter)
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when {
                    isRefreshing -> {
                        LoadingScreen()
                    }
                    errorMessage != null -> {
                        ErrorScreen(
                            msg = errorMessage.orEmpty(),
                            onRetry = { viewModel.refreshProducts() }
                        )
                    }
                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(products) { product ->
                                LocalProductItem(
                                    product = product,
                                    onClick = {
                                        val intent = Intent(context, ProductDetailActivity::class.java)
                                            .apply {
                                                putExtra("productId", product.id)
                                            }
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Footer {
                context.startActivity(Intent(context, WebViewActivity::class.java))
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.Blue,
            strokeWidth = 4.dp
        )
    }
}

@Composable
fun LocalProductItem(
    product: Product,
    onClick: (Product) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp)
            .padding(vertical = 8.dp, horizontal = 10.dp)
            .background(if (product.marked) markedColor else Color.White)
            .border(1.dp, Color.Black)
            .clickable { onClick(product) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (product.available) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .border(1.dp, Color.Black)
            ) {
                ProductImageFromUrl(
                    product = product,
                    modifier = Modifier.size(64.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier
            .weight(1f)
            .padding(vertical = 8.dp)
        ) {
            Text(
                text = product.name,
                color = if (product.marked) Color.Blue else Color.Black,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
            Text(
                text = product.description,
                maxLines = 2,
                color = Color.Black.copy(alpha = 0.5f),
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Text(
                text = "Preis: ${product.price.value} ${product.price.currency}",
                style = TextStyle(fontStyle = FontStyle.Italic)
            )
            RatingBar(rating = product.rating)
        }

        if (!product.available) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .border(1.dp, Color.Black)
            ) {
                ProductImageFromUrl(
                    product = product,
                    modifier = Modifier.size(64.dp)
                )
            }
        } else {
            val formattedDate = formatTimestamp(product.releaseDate)

            Text(
                text = formattedDate,
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(top = 8.dp, end = 8.dp),
                color = Color.Gray,
            )
        }
    }
}


@Composable
fun ErrorScreen(
    msg: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, Color.LightGray)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Color.Red, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "!",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Probleme",
            style = TextStyle(
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = msg,
            style = TextStyle(
                color = Color.Gray,
                fontSize = 16.sp
            ),
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth(0.5f)
        ) {
            Text(
                text = "Neuladen",
                style = TextStyle(fontSize = 16.sp)
            )
        }
    }
}
