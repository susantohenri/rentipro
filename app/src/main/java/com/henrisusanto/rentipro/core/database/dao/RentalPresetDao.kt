package com.henrisusanto.rentipro.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.henrisusanto.rentipro.core.database.entity.RentalPresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RentalPresetDao {

    @Query("SELECT * FROM rental_presets ORDER BY sortOrder ASC, durationMinutes ASC")
    fun observeAll(): Flow<List<RentalPresetEntity>>

    @Query("SELECT * FROM rental_presets ORDER BY sortOrder ASC, durationMinutes ASC")
    suspend fun getAll(): List<RentalPresetEntity>

    @Query("SELECT * FROM rental_presets WHERE id = :id")
    suspend fun getById(id: Long): RentalPresetEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(preset: RentalPresetEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(presets: List<RentalPresetEntity>): List<Long>

    @Update
    suspend fun update(preset: RentalPresetEntity)

    @Delete
    suspend fun delete(preset: RentalPresetEntity)

    @Query("SELECT COUNT(*) FROM rental_presets")
    suspend fun count(): Int
}
