package com.henrisusanto.rentipro.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.henrisusanto.rentipro.core.database.dao.RentalDao
import com.henrisusanto.rentipro.core.database.dao.RentalExtensionDao
import com.henrisusanto.rentipro.core.database.dao.RentalPresetDao
import com.henrisusanto.rentipro.core.database.dao.RentalUnitDao
import com.henrisusanto.rentipro.core.database.entity.RentalEntity
import com.henrisusanto.rentipro.core.database.entity.RentalExtensionEntity
import com.henrisusanto.rentipro.core.database.entity.RentalPresetEntity
import com.henrisusanto.rentipro.core.database.entity.RentalUnitEntity

@Database(
    entities = [
        RentalUnitEntity::class,
        RentalPresetEntity::class,
        RentalEntity::class,
        RentalExtensionEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class RentiproDatabase : RoomDatabase() {

    abstract fun rentalUnitDao(): RentalUnitDao
    abstract fun rentalPresetDao(): RentalPresetDao
    abstract fun rentalDao(): RentalDao
    abstract fun rentalExtensionDao(): RentalExtensionDao

    companion object {
        private const val DATABASE_NAME = "rentipro.db"

        fun create(context: Context): RentiproDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                RentiproDatabase::class.java,
                DATABASE_NAME,
            ).build()
    }
}
