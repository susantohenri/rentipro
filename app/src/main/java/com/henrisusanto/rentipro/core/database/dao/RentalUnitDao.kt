package com.henrisusanto.rentipro.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.henrisusanto.rentipro.core.database.entity.RentalUnitEntity
import com.henrisusanto.rentipro.core.model.UnitStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface RentalUnitDao {

    @Query("SELECT * FROM rental_units ORDER BY name ASC")
    fun observeAll(): Flow<List<RentalUnitEntity>>

    @Query("SELECT * FROM rental_units ORDER BY name ASC")
    suspend fun getAll(): List<RentalUnitEntity>

    @Query("SELECT * FROM rental_units WHERE id = :id")
    fun observeById(id: Long): Flow<RentalUnitEntity?>

    @Query("SELECT * FROM rental_units WHERE id = :id")
    suspend fun getById(id: Long): RentalUnitEntity?

    @Query("SELECT * FROM rental_units WHERE status = :status ORDER BY name ASC")
    fun observeByStatus(status: UnitStatus): Flow<List<RentalUnitEntity>>

    @Query("SELECT * FROM rental_units WHERE status = 'AVAILABLE' ORDER BY name ASC")
    fun observeAvailable(): Flow<List<RentalUnitEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(unit: RentalUnitEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(units: List<RentalUnitEntity>): List<Long>

    @Update
    suspend fun update(unit: RentalUnitEntity)

    @Query("UPDATE rental_units SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: UnitStatus, updatedAt: Long)

    @Query("UPDATE rental_units SET name = :name, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateName(id: Long, name: String, updatedAt: Long)

    @Delete
    suspend fun delete(unit: RentalUnitEntity)

    @Query("SELECT COUNT(*) FROM rental_units")
    suspend fun count(): Int
}
