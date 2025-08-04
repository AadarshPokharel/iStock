package com.istock.inventorymanager.ui.navigation
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.istock.inventorymanager.data.model.InventoryItem
import com.istock.inventorymanager.ui.screen.AddEditItemScreen
import com.istock.inventorymanager.ui.screen.CategoriesScreen
import com.istock.inventorymanager.ui.screen.CategoryInventoryScreen
import com.istock.inventorymanager.ui.screen.InventoryScreen
import com.istock.inventorymanager.ui.screen.NotificationScreen
import com.istock.inventorymanager.ui.screen.ShoppingListScreen
import com.istock.inventorymanager.ui.viewmodel.CategoryViewModel
import com.istock.inventorymanager.ui.viewmodel.InventoryViewModel
import com.istock.inventorymanager.ui.viewmodel.NotificationViewModel

sealed class Screen(
        val route: String,
        val title: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Categories : Screen("categories", "Categories", Icons.Default.Folder)
    object Inventory : Screen("inventory", "Inventory", Icons.Default.Inventory2)
    object ShoppingList : Screen("shopping_list", "Shopping List", Icons.Default.ShoppingCart)
    object AddEditItem : Screen("add_edit_item", "Add/Edit Item", Icons.Default.Inventory2)
    object Notifications : Screen("notifications", "Notifications", Icons.Default.Notifications)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val items = listOf(Screen.Categories, Screen.Inventory, Screen.ShoppingList, Screen.Notifications)

    var currentEditingItem by remember { mutableStateOf<InventoryItem?>(null) }
    var showAddEditScreen by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar (containerColor = Color(0xFF9E3B2F)) //color for bottom bar
            {

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected =
                            currentDestination?.hierarchy?.any {
                                it.route == screen.route
                            } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },

                        // This gives color to the icons and text inside the bottom bar
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFF9E3B2), // Color for the icon when item is selected
                            selectedTextColor = Color(0xFFF9E3B2), // Color for the text when item is selected
                            indicatorColor = Color.White.copy(alpha = 0.2f),
                            unselectedIconColor = Color(0xFFF9E3B2), // Color for the icon when item is not selected
                            unselectedTextColor = Color.White  // Color for the text when item is not selected
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        if (showAddEditScreen) {
            val categoryViewModel: CategoryViewModel = hiltViewModel()
            val inventoryViewModel: InventoryViewModel = hiltViewModel()
            val categories by categoryViewModel.categories.collectAsState()

            AddEditItemScreen(
                item = currentEditingItem,
                categories = categories,
                onSave = { item ->
                    if (currentEditingItem == null) {
                        inventoryViewModel.addItem(item)
                    } else {
                        inventoryViewModel.updateItem(item)
                    }
                    showAddEditScreen = false
                    currentEditingItem = null
                },
                onCancel = {
                    showAddEditScreen = false
                    currentEditingItem = null
                }
            )
        } else {
            NavHost(
                navController = navController,
                startDestination = Screen.Categories.route,
                modifier = modifier.padding(innerPadding)
            ) {
                composable(Screen.Inventory.route) {
                    InventoryScreen(
                        onAddItem = {
                            currentEditingItem = null
                            showAddEditScreen = true
                        },
                        onEditItem = { item ->
                            currentEditingItem = item
                            showAddEditScreen = true
                        }
                    )
                }
                composable(Screen.ShoppingList.route) { ShoppingListScreen() }
                composable(Screen.Categories.route){ CategoriesScreen(navController = navController) }
                composable("categoryInventory/{categoryId}") { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getString("categoryId")?.toLongOrNull()
                    if (categoryId != null) {
                        CategoryInventoryScreen(
                            categoryId = categoryId,
                            navController = navController
                        )
                    } else {
                        // Handle invalid category ID case
                        Text("Invalid Category ID")
                    }
                }
                composable(Screen.Notifications.route) {
                    val notificationViewModel: NotificationViewModel = hiltViewModel()
                    val notifications by notificationViewModel.notifications.collectAsState()
                    NotificationScreen(
                        notifications = notifications,
                        onNotificationClick = { notification ->
                            notificationViewModel.markAsRead(notification.id)
                        },
                        onClearAll = { notificationViewModel.clearAll() }
                    )
                }
                composable("settings")
                {
                   com.istock.inventorymanager.ui.screen.SettingsScreen(navController = navController)
                }
            }
        }
    }
}
