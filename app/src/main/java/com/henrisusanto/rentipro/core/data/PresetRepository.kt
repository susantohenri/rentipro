package com.henrisusanto.rentipro.core.data

import com.henrisusanto.rentipro.core.database.dao.RentalPresetDao
import com.henrisusanto.rentipro.core.database.entity.RentalPresetEntity
import kotlinx.coroutines.flow.Flow

class PresetRepository(
    private val rentalPresetDao: RentalPresetDao,
) {

    fun observePresets(): Flow<List<RentalPresetEntity>> = rentalPresetDao.observeAll()

    suspend fun getPresets(): List<RentalPresetEntity> = rentalPresetDao.getAll()

    suspend fun getPreset(id: Long): RentalPresetEntity? = rentalPresetDao.getById(id)

    suspend fun createPreset(
        durationMinutes: Int,
        price: Int,
        sortOrder: Int? = null,
    ): Long {
        val order = sortOrder ?: (rentalPresetDao.getAll().maxOfOrNull { it.sortOrder } ?: -1) + 1
        return rentalPresetDao.insert(
            RentalPresetEntity(
                durationMinutes = durationMinutes,
                price = price,
                sortOrder = order,
            ),
        )
    }

    suspend fun createPresets(presets: List<Pair<Int, Int>>): List<Long> {
        val entities = presets.mapIndexed { index, (duration, price) ->
            RentalPresetEntity(
                durationMinutes = duration,
                price = price,
                sortOrder = index,
            )
        }
        return rentalPresetDao.insertAll(entities)
    }

    suspend fun updatePreset(preset: RentalPresetEntity) {
        rentalPresetDao.update(preset)
    }

    suspend fun deletePreset(preset: RentalPresetEntity) {
        rentalPresetDao.delete(preset)
    }

    suspend fun hasAnyPresets(): Boolean = rentalPresetDao.count() > 0
}
