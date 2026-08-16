package com.henrisusanto.rentipro.core.database

import androidx.room.TypeConverter
import com.henrisusanto.rentipro.core.model.RentalStatus
import com.henrisusanto.rentipro.core.model.UnitStatus

class Converters {

    @TypeConverter
    fun fromUnitStatus(status: UnitStatus): String = status.name

    @TypeConverter
    fun toUnitStatus(value: String): UnitStatus = UnitStatus.valueOf(value)

    @TypeConverter
    fun fromRentalStatus(status: RentalStatus): String = status.name

    @TypeConverter
    fun toRentalStatus(value: String): RentalStatus = RentalStatus.valueOf(value)
}
