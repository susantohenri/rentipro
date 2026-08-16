package com.henrisusanto.rentipro.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.henrisusanto.rentipro.core.database.entity.RentalEntity
import com.henrisusanto.rentipro.core.database.model.ActiveRentalWithUnit
import com.henrisusanto.rentipro.core.database.model.HistoryRentalWithDetails
import com.henrisusanto.rentipro.core.model.RentalStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface RentalDao {

    @Query("SELECT * FROM rentals WHERE status = 'ACTIVE' ORDER BY scheduledEndAt ASC")
    fun observeActive(): Flow<List<RentalEntity>>

    @Transaction
    @Query("SELECT * FROM rentals WHERE status = 'ACTIVE' ORDER BY scheduledEndAt ASC")
    fun observeActiveWithUnits(): Flow<List<ActiveRentalWithUnit>>

    @Query("SELECT * FROM rentals WHERE id = :id")
    fun observeById(id: Long): Flow<RentalEntity?>

    @Query("SELECT * FROM rentals WHERE id = :id")
    suspend fun getById(id: Long): RentalEntity?

    @Query("SELECT * FROM rentals WHERE unitId = :unitId AND status = 'ACTIVE' LIMIT 1")
    suspend fun getActiveByUnitId(unitId: Long): RentalEntity?

    @Query("SELECT COUNT(*) FROM rentals WHERE unitId = :unitId AND status = 'ACTIVE'")
    suspend fun countActiveByUnitId(unitId: Long): Int

    @Transaction
    @Query(
        """
        SELECT * FROM rentals
        WHERE status IN ('COMPLETED', 'DELETED')
        ORDER BY COALESCE(returnedAt, startedAt) DESC
        """,
    )
    fun observeHistory(): Flow<List<HistoryRentalWithDetails>>

    @Query(
        """
        SELECT COUNT(*) FROM rentals
        WHERE status = 'COMPLETED'
        AND returnedAt IS NOT NULL
        AND returnedAt >= :startOfDayMillis
        AND returnedAt < :endOfDayMillis
        """,
    )
    fun observeTodayCompletedCount(startOfDayMillis: Long, endOfDayMillis: Long): Flow<Int>

    @Query(
        """
        SELECT COALESCE(SUM(price), 0) FROM rentals
        WHERE status = 'COMPLETED'
        AND returnedAt IS NOT NULL
        AND returnedAt >= :startOfDayMillis
        AND returnedAt < :endOfDayMillis
        """,
    )
    fun observeTodayRevenue(startOfDayMillis: Long, endOfDayMillis: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(rental: RentalEntity): Long

    @Update
    suspend fun update(rental: RentalEntity)

    @Query("UPDATE rentals SET status = :status, returnedAt = :returnedAt WHERE id = :id")
    suspend fun finalizeRental(id: Long, status: RentalStatus, returnedAt: Long)

    @Query("SELECT COUNT(*) FROM rentals WHERE status = 'ACTIVE'")
    suspend fun countActive(): Int
}
