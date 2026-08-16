package com.henrisusanto.rentipro.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.henrisusanto.rentipro.core.database.entity.RentalEntity
import com.henrisusanto.rentipro.core.database.entity.RentalExtensionEntity
import com.henrisusanto.rentipro.core.database.entity.RentalUnitEntity

data class ActiveRentalWithUnit(
    @Embedded val rental: RentalEntity,
    @Relation(
        parentColumn = "unitId",
        entityColumn = "id",
    )
    val unit: RentalUnitEntity,
)

data class HistoryRentalWithDetails(
    @Embedded val rental: RentalEntity,
    @Relation(
        parentColumn = "unitId",
        entityColumn = "id",
    )
    val unit: RentalUnitEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "rentalId",
    )
    val extensions: List<RentalExtensionEntity>,
)
