package com.henrisusanto.rentipro.core.data

import com.henrisusanto.rentipro.core.alarm.AlarmScheduler
import com.henrisusanto.rentipro.core.database.dao.RentalDao
import com.henrisusanto.rentipro.core.database.dao.RentalExtensionDao
import com.henrisusanto.rentipro.core.database.dao.RentalUnitDao
import com.henrisusanto.rentipro.core.database.entity.RentalEntity
import com.henrisusanto.rentipro.core.database.entity.RentalExtensionEntity
import com.henrisusanto.rentipro.core.database.entity.RentalPresetEntity
import com.henrisusanto.rentipro.core.database.model.ActiveRentalWithUnit
import com.henrisusanto.rentipro.core.database.model.HistoryRentalWithDetails
import com.henrisusanto.rentipro.core.model.RentalStatus
import com.henrisusanto.rentipro.core.model.UnitStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Calendar

class RentalRepository(
    private val rentalDao: RentalDao,
    private val rentalExtensionDao: RentalExtensionDao,
    private val rentalUnitDao: RentalUnitDao,
    private val settingsRepository: SettingsRepository,
    private val alarmScheduler: AlarmScheduler,
) {

    fun observeActiveRentals(): Flow<List<RentalEntity>> = rentalDao.observeActive()

    fun observeActiveRentalsWithUnits(): Flow<List<ActiveRentalWithUnit>> =
        rentalDao.observeActiveWithUnits()

    fun observeRental(id: Long): Flow<RentalEntity?> = rentalDao.observeById(id)

    fun observeHistory(): Flow<List<HistoryRentalWithDetails>> = rentalDao.observeHistory()

    fun observeTodayCompletedCount(): Flow<Int> {
        val (start, end) = todayBounds()
        return rentalDao.observeTodayCompletedCount(start, end)
    }

    fun observeTodayRevenue(): Flow<Int> {
        val (start, end) = todayBounds()
        return rentalDao.observeTodayRevenue(start, end)
    }

    suspend fun getRental(id: Long): RentalEntity? = rentalDao.getById(id)

    suspend fun getActiveRentalForUnit(unitId: Long): RentalEntity? =
        rentalDao.getActiveByUnitId(unitId)

    suspend fun hasActiveRental(unitId: Long): Boolean =
        rentalDao.countActiveByUnitId(unitId) > 0

    suspend fun startRental(
        unitId: Long,
        preset: RentalPresetEntity,
        nowMillis: Long = System.currentTimeMillis(),
    ): Long {
        val durationMillis = preset.durationMinutes * 60_000L
        val rental = RentalEntity(
            unitId = unitId,
            presetId = preset.id,
            durationMinutes = preset.durationMinutes,
            price = preset.price,
            startedAt = nowMillis,
            scheduledEndAt = nowMillis + durationMillis,
        )
        val rentalId = rentalDao.insert(rental)
        rentalUnitDao.updateStatus(unitId, UnitStatus.RENTED, nowMillis)
        alarmScheduler.schedule(rental.copy(id = rentalId), settingsRepository.dueSoonMinutes.first())
        return rentalId
    }

    suspend fun extendRental(
        rental: RentalEntity,
        preset: RentalPresetEntity,
        nowMillis: Long = System.currentTimeMillis(),
    ) {
        val addedMillis = preset.durationMinutes * 60_000L
        rentalExtensionDao.insert(
            RentalExtensionEntity(
                rentalId = rental.id,
                presetId = preset.id,
                addedDurationMinutes = preset.durationMinutes,
                addedPrice = preset.price,
                extendedAt = nowMillis,
            ),
        )
        val updated = rental.copy(
            durationMinutes = rental.durationMinutes + preset.durationMinutes,
            price = rental.price + preset.price,
            scheduledEndAt = rental.scheduledEndAt + addedMillis,
            dueSoonNotified = false,
            overdueNotified = false,
        )
        rentalDao.update(updated)
        alarmScheduler.schedule(updated, settingsRepository.dueSoonMinutes.first())
    }

    suspend fun pauseRental(rental: RentalEntity, nowMillis: Long = System.currentTimeMillis()) {
        if (rental.isPaused) return
        alarmScheduler.cancel(rental.id)
        rentalDao.update(
            rental.copy(
                isPaused = true,
                pausedAt = nowMillis,
            ),
        )
    }

    suspend fun resumeRental(rental: RentalEntity, nowMillis: Long = System.currentTimeMillis()) {
        if (!rental.isPaused || rental.pausedAt == null) return
        val pauseDuration = nowMillis - rental.pausedAt
        val updated = rental.copy(
            isPaused = false,
            pausedAt = null,
            scheduledEndAt = rental.scheduledEndAt + pauseDuration,
        )
        rentalDao.update(updated)
        alarmScheduler.schedule(updated, settingsRepository.dueSoonMinutes.first())
    }

    suspend fun returnRental(rental: RentalEntity, nowMillis: Long = System.currentTimeMillis()) {
        if (rental.status != RentalStatus.ACTIVE) return
        alarmScheduler.cancel(rental.id)
        rentalDao.finalizeRental(rental.id, RentalStatus.COMPLETED, nowMillis)
        rentalUnitDao.updateStatus(rental.unitId, UnitStatus.AVAILABLE, nowMillis)
    }

    suspend fun deleteActiveRental(rental: RentalEntity, nowMillis: Long = System.currentTimeMillis()) {
        if (rental.status != RentalStatus.ACTIVE) return
        alarmScheduler.cancel(rental.id)
        rentalDao.finalizeRental(rental.id, RentalStatus.DELETED, nowMillis)
        rentalUnitDao.updateStatus(rental.unitId, UnitStatus.AVAILABLE, nowMillis)
    }

    suspend fun markUnitOverdue(unitId: Long, nowMillis: Long = System.currentTimeMillis()) {
        rentalUnitDao.updateStatus(unitId, UnitStatus.OVERDUE, nowMillis)
    }

    suspend fun syncUnitStatusFromRental(rental: RentalEntity, nowMillis: Long = System.currentTimeMillis()) {
        if (rental.status != RentalStatus.ACTIVE) return
        val status = if (nowMillis >= rental.scheduledEndAt && !rental.isPaused) {
            UnitStatus.OVERDUE
        } else {
            UnitStatus.RENTED
        }
        rentalUnitDao.updateStatus(rental.unitId, status, nowMillis)
    }

    suspend fun updateRental(rental: RentalEntity) {
        rentalDao.update(rental)
    }

    /**
     * Re-arms alarms for every active rental. Idempotent — used on app start
     * and after device boot (alarms do not survive a reboot).
     */
    suspend fun rescheduleAllAlarms() {
        val dueSoonMinutes = settingsRepository.dueSoonMinutes.first()
        rentalDao.observeActive().first().forEach { rental ->
            alarmScheduler.schedule(rental, dueSoonMinutes)
        }
    }

    private fun todayBounds(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val end = calendar.timeInMillis
        return start to end
    }
}
