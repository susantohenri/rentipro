package com.henrisusanto.rentipro.core.data

import com.henrisusanto.rentipro.core.database.dao.RentalUnitDao
import com.henrisusanto.rentipro.core.database.entity.RentalUnitEntity
import com.henrisusanto.rentipro.core.model.UnitStatus
import kotlinx.coroutines.flow.Flow

class UnitRepository(
    private val rentalUnitDao: RentalUnitDao,
) {

    fun observeAllUnits(): Flow<List<RentalUnitEntity>> = rentalUnitDao.observeAll()

    fun observeUnit(id: Long): Flow<RentalUnitEntity?> = rentalUnitDao.observeById(id)

    fun observeUnitsByStatus(status: UnitStatus): Flow<List<RentalUnitEntity>> =
        rentalUnitDao.observeByStatus(status)

    fun observeAvailableUnits(): Flow<List<RentalUnitEntity>> = rentalUnitDao.observeAvailable()

    suspend fun getAllUnits(): List<RentalUnitEntity> = rentalUnitDao.getAll()

    suspend fun getUnit(id: Long): RentalUnitEntity? = rentalUnitDao.getById(id)

    suspend fun createUnit(name: String, nowMillis: Long = System.currentTimeMillis()): Long {
        return rentalUnitDao.insert(
            RentalUnitEntity(
                name = name,
                status = UnitStatus.AVAILABLE,
                createdAt = nowMillis,
                updatedAt = nowMillis,
            ),
        )
    }

    suspend fun createUnits(names: List<String>, nowMillis: Long = System.currentTimeMillis()): List<Long> {
        val units = names.map { name ->
            RentalUnitEntity(
                name = name,
                status = UnitStatus.AVAILABLE,
                createdAt = nowMillis,
                updatedAt = nowMillis,
            )
        }
        return rentalUnitDao.insertAll(units)
    }

    suspend fun renameUnit(id: Long, name: String, nowMillis: Long = System.currentTimeMillis()) {
        rentalUnitDao.updateName(id, name, nowMillis)
    }

    suspend fun updateUnitStatus(id: Long, status: UnitStatus, nowMillis: Long = System.currentTimeMillis()) {
        rentalUnitDao.updateStatus(id, status, nowMillis)
    }

    suspend fun deleteUnit(unit: RentalUnitEntity) {
        rentalUnitDao.delete(unit)
    }

    suspend fun hasAnyUnits(): Boolean = rentalUnitDao.count() > 0
}
