package com.henrisusanto.rentipro.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.henrisusanto.rentipro.core.model.RentalStatus
import com.henrisusanto.rentipro.core.model.UnitStatus

@Entity(tableName = "rental_units")
data class RentalUnitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val status: UnitStatus = UnitStatus.AVAILABLE,
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(
    tableName = "rental_presets",
    indices = [Index(value = ["sortOrder"])],
)
data class RentalPresetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val durationMinutes: Int,
    val price: Int,
    val sortOrder: Int,
)

@Entity(
    tableName = "rentals",
    indices = [
        Index(value = ["unitId"]),
        Index(value = ["status"]),
        Index(value = ["scheduledEndAt"]),
    ],
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = RentalUnitEntity::class,
            parentColumns = ["id"],
            childColumns = ["unitId"],
            onDelete = androidx.room.ForeignKey.RESTRICT,
        ),
    ],
)
data class RentalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val unitId: Long,
    val presetId: Long?,
    val durationMinutes: Int,
    val price: Int,
    val startedAt: Long,
    val scheduledEndAt: Long,
    val returnedAt: Long? = null,
    val status: RentalStatus = RentalStatus.ACTIVE,
    val isPaused: Boolean = false,
    val pausedAt: Long? = null,
    val dueSoonNotified: Boolean = false,
    val overdueNotified: Boolean = false,
)

@Entity(
    tableName = "rental_extensions",
    indices = [Index(value = ["rentalId"])],
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = RentalEntity::class,
            parentColumns = ["id"],
            childColumns = ["rentalId"],
            onDelete = androidx.room.ForeignKey.CASCADE,
        ),
    ],
)
data class RentalExtensionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rentalId: Long,
    val presetId: Long?,
    val addedDurationMinutes: Int,
    val addedPrice: Int,
    val extendedAt: Long,
)
