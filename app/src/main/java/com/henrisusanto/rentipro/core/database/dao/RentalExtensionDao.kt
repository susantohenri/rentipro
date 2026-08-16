package com.henrisusanto.rentipro.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.henrisusanto.rentipro.core.database.entity.RentalExtensionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RentalExtensionDao {

    @Query("SELECT * FROM rental_extensions WHERE rentalId = :rentalId ORDER BY extendedAt ASC")
    fun observeByRentalId(rentalId: Long): Flow<List<RentalExtensionEntity>>

    @Query("SELECT * FROM rental_extensions WHERE rentalId = :rentalId ORDER BY extendedAt ASC")
    suspend fun getByRentalId(rentalId: Long): List<RentalExtensionEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(extension: RentalExtensionEntity): Long
}
