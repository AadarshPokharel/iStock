package com.istock.inventorymanager.di

import android.content.Context
import androidx.room.Room
import com.istock.inventorymanager.data.dao.CategoryDao
import com.istock.inventorymanager.data.dao.InventoryItemDao
import com.istock.inventorymanager.data.dao.ShoppingListDao
import com.istock.inventorymanager.data.database.InventoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideInventoryDatabase(@ApplicationContext context: Context): InventoryDatabase {
        return InventoryDatabase.getDatabase(context)
    }

    @Provides
    fun provideCategoryDao(database: InventoryDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideInventoryItemDao(database: InventoryDatabase): InventoryItemDao =
            database.inventoryItemDao()

    @Provides
    fun provideShoppingListDao(database: InventoryDatabase): ShoppingListDao =
            database.shoppingListDao()
}
