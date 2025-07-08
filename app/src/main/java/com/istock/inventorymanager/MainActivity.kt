package com.istock.inventorymanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.istock.inventorymanager.data.initializer.DataInitializer
import com.istock.inventorymanager.ui.navigation.InventoryNavigation
import com.istock.inventorymanager.ui.theme.IStockInventoryManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var dataInitializer: DataInitializer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize sample data
        lifecycleScope.launch { dataInitializer.initializeData() }

        enableEdgeToEdge()
        setContent {
            IStockInventoryManagerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    InventoryNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
