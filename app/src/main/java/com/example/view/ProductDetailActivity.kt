package com.example.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.model.api.RetrofitInstance
import com.example.model.db.AppDatabase
import com.example.model.repository.ProductRepository
import com.example.view.ui.ProductDetailScreen
import com.example.viewmodel.ProductViewModel
import com.example.viewmodel.ProductViewModelFactory

class ProductDetailActivity : ComponentActivity() {
    private lateinit var productViewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val productId = intent.getIntExtra("productId", -1)
        if (productId == -1) {
            finish()
            return
        }

        initializeViewModel()

        setContent {
            ProductDetailScreen(
                productId = productId,
                viewModel = productViewModel,
                navigateToWebView = { navigateToWebView() }
            )
        }
    }

    private fun initializeViewModel() {
        val database = AppDatabase.getInstance(applicationContext)
        val repository = ProductRepository(
            api = RetrofitInstance.api,
            dao = database.productDao()
        )
        val viewModelFactory = ProductViewModelFactory(application, repository)
        productViewModel = ViewModelProvider(this, viewModelFactory)[ProductViewModel::class.java]
    }

    private fun navigateToWebView() {
        startActivity(Intent(this, WebViewActivity::class.java))
    }
}