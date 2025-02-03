package com.example.view.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.model.entities.Product
import com.example.viewmodel.ProductViewModel

@Composable
fun ProductDetailScreen(
    productId: Int,
    viewModel: ProductViewModel,
    navigateToWebView: () -> Unit) {
    // Collect the product from DB as a Flow, converting it to State
    val productState by viewModel.getProductFlow(productId).collectAsState(initial = null)

    when (val product = productState) {
        null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading...")
            }
        }
        else -> {
            ProductContent(
                product = product,
                viewModel = viewModel,
                navigateToWebView = navigateToWebView
            )
        }
    }
}


@Composable
fun ProductContent(
    product: Product,
    viewModel: ProductViewModel,
    navigateToWebView: () -> Unit
) {
    val header by viewModel.header.collectAsState()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val (scrollableContent, footer) = createRefs()

        Column(
            modifier = Modifier
                .constrainAs(scrollableContent) {
                    top.linkTo(parent.top)
                    bottom.linkTo(footer.top) // Stop scrolling above the footer
                    height = Dimension.fillToConstraints
                }
                .verticalScroll(rememberScrollState())
                .then(
                    if (product.marked) Modifier.background(markedColor)
                    else Modifier
                )
        ) {
            Header(header = header)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .border(3.dp, Color.Black),
                    contentAlignment = Alignment.Center,
                ) {
                    ProductImageFromUrl(
                        product = product,
                        modifier = Modifier.size(128.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        color = if (product.marked) Color.Blue else Color.Black,
                        modifier = Modifier.padding(bottom = 4.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "Preis: ${product.price.value} ${product.price.currency}",
                        modifier = Modifier.padding(bottom = 8.dp),
                        style = TextStyle(
                            fontSize = 18.sp
                        )
                    )
                    RatingBar(rating = product.rating, starSize = 30)
                }

                val formattedDate = formatTimestamp(product.releaseDate)

                Text(
                    text = formattedDate,
                    modifier = Modifier.align(Alignment.Bottom),
                    color = Color.Gray,
                )
            }

            Text(
                text = product.description,
                modifier = Modifier.padding(bottom = 16.dp),
                color = Color.Gray,
                style = TextStyle(
                    fontSize = 20.sp
                )
            )

            Button(
                onClick = {
                    viewModel.toggleMark(product)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(text = if (product.marked) "Vergessen" else "Vormerken")
            }

            Text(
                text = "Beschreibung",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
            Text(
                text = product.longDescription,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                color = Color.Gray,
                style = TextStyle(
                    fontSize = 20.sp
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(footer) {
                    bottom.linkTo(parent.bottom) // Fixed at the bottom
                }
        ) {
            Footer(navigateToWebView)
        }
    }
}