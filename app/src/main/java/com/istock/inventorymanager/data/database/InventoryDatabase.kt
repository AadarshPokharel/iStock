
package com.istock.inventorymanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.istock.inventorymanager.data.dao.CategoryDao
import com.istock.inventorymanager.data.dao.InventoryItemDao
import com.istock.inventorymanager.data.dao.ShoppingListDao
import com.istock.inventorymanager.data.model.Category
import com.istock.inventorymanager.data.model.InventoryItem
import com.istock.inventorymanager.data.model.ShoppingListItem

@Database(
        entities = [Category::class, InventoryItem::class, ShoppingListItem::class],
        version = 2,
        exportSchema = false
)
@TypeConverters(Converters::class)
abstract class InventoryDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun inventoryItemDao(): InventoryItemDao
    abstract fun shoppingListDao(): ShoppingListDao

    companion object {
        @Volatile private var INSTANCE: InventoryDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    ALTER TABLE inventory_items ADD COLUMN imagePath TEXT
                """)
                database.execSQL("""
                    ALTER TABLE inventory_items ADD COLUMN barcode TEXT
                """)
                database.execSQL("""
                    ALTER TABLE inventory_items ADD COLUMN location TEXT NOT NULL DEFAULT ''
                """)
                database.execSQL("""
                    ALTER TABLE inventory_items ADD COLUMN notes TEXT NOT NULL DEFAULT ''
                """)
            }
        }

        fun getDatabase(context: Context): InventoryDatabase {
            return INSTANCE
                    ?: synchronized(this) {
                        val instance =
                                Room.databaseBuilder(
                                                context.applicationContext,
                                                InventoryDatabase::class.java,
                                                "inventory_database"
                                        )
                                        .addMigrations(MIGRATION_1_2)
                                        .build()
                        INSTANCE = instance
                        instance
                    }
        }
    }
}
