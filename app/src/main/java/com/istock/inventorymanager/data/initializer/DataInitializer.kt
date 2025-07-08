package com.istock.inventorymanager.data.initializer

import com.istock.inventorymanager.data.model.Category
import com.istock.inventorymanager.data.model.InventoryItem
import com.istock.inventorymanager.data.model.ShoppingListItem
import com.istock.inventorymanager.data.repository.CategoryRepository
import com.istock.inventorymanager.data.repository.InventoryRepository
import com.istock.inventorymanager.data.repository.ShoppingListRepository
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class DataInitializer
@Inject
constructor(
        private val categoryRepository: CategoryRepository,
        private val inventoryRepository: InventoryRepository,
        private val shoppingListRepository: ShoppingListRepository
) {

    suspend fun initializeData() {
        // Check if data already exists
        val existingCategories = categoryRepository.getAllCategories().first()
        if (existingCategories.isNotEmpty()) return

        // Initialize categories
        val categories =
                listOf(
                        Category(name = "Kitchen", description = "Kitchen items and appliances"),
                        Category(name = "Bedroom", description = "Bedroom essentials"),
                        Category(name = "Bathroom", description = "Bathroom supplies"),
                        Category(
                                name = "Living Room",
                                description = "Living room furniture and items"
                        ),
                        Category(name = "Garage", description = "Tools and garage equipment"),
                        Category(
                                name = "Store Room",
                                description = "Storage and miscellaneous items"
                        )
                )

        val categoryIds = mutableMapOf<String, Long>()

        // Insert categories and store their IDs
        categories.forEach { category ->
            val id = categoryRepository.insertCategory(category)
            categoryIds[category.name] = id
        }

        // Initialize sample inventory items
        val calendar = Calendar.getInstance()

        // Kitchen items
        val kitchenId = categoryIds["Kitchen"]!!
        val kitchenItems =
                listOf(
                        InventoryItem(
                                name = "Apple",
                                description = "Fresh red apples",
                                categoryId = kitchenId,
                                quantity = 12,
                                minStockLevel = 5,
                                expirationDate =
                                        Date(
                                                calendar.timeInMillis + 7 * 24 * 60 * 60 * 1000
                                        ), // 7 days
                                price = 0.50
                        ),
                        InventoryItem(
                                name = "Milk",
                                description = "1 gallon whole milk",
                                categoryId = kitchenId,
                                quantity = 2,
                                minStockLevel = 1,
                                expirationDate =
                                        Date(
                                                calendar.timeInMillis + 5 * 24 * 60 * 60 * 1000
                                        ), // 5 days
                                price = 3.99
                        ),
                        InventoryItem(
                                name = "Juice",
                                description = "Orange juice",
                                categoryId = kitchenId,
                                quantity = 3,
                                minStockLevel = 2,
                                expirationDate =
                                        Date(
                                                calendar.timeInMillis + 10 * 24 * 60 * 60 * 1000
                                        ), // 10 days
                                price = 4.50
                        ),
                        InventoryItem(
                                name = "Refrigerator",
                                description = "Samsung smart refrigerator",
                                categoryId = kitchenId,
                                quantity = 1,
                                minStockLevel = 1,
                                warrantyDate =
                                        Date(
                                                calendar.timeInMillis + 365 * 24 * 60 * 60 * 1000
                                        ), // 1 year
                                price = 1299.99
                        )
                )

        // Bathroom items
        val bathroomId = categoryIds["Bathroom"]!!
        val bathroomItems =
                listOf(
                        InventoryItem(
                                name = "Paper Towels",
                                description = "Absorbent paper towels",
                                categoryId = bathroomId,
                                quantity = 6,
                                minStockLevel = 3,
                                price = 12.99
                        ),
                        InventoryItem(
                                name = "Trash Can",
                                description = "Small bathroom trash can",
                                categoryId = bathroomId,
                                quantity = 1,
                                minStockLevel = 1,
                                price = 25.99
                        )
                )

        // Bedroom items
        val bedroomId = categoryIds["Bedroom"]!!
        val bedroomItems =
                listOf(
                        InventoryItem(
                                name = "Dishes",
                                description = "Ceramic dinner plates set",
                                categoryId = bedroomId,
                                quantity = 8,
                                minStockLevel = 4,
                                price = 45.99
                        )
                )

        // Insert all inventory items
        (kitchenItems + bathroomItems + bedroomItems).forEach { item ->
            inventoryRepository.insertItem(item)
        }

        // Initialize sample shopping list items
        val shoppingItems =
                listOf(
                        ShoppingListItem(
                                name = "Paper Towels",
                                quantity = 2,
                                priority = 3, // High priority
                                notes = "Running low, need to restock"
                        ),
                        ShoppingListItem(
                                name = "Refrigerator",
                                quantity = 1,
                                priority = 2, // Medium priority
                                notes = "Check warranty status"
                        ),
                        ShoppingListItem(
                                name = "Trash Can",
                                quantity = 1,
                                priority = 1, // Low priority
                        ),
                        ShoppingListItem(
                                name = "Dishes",
                                quantity = 4,
                                priority = 2, // Medium priority
                                isCompleted = true
                        )
                )

        shoppingItems.forEach { item -> shoppingListRepository.insertShoppingItem(item) }
    }
}
