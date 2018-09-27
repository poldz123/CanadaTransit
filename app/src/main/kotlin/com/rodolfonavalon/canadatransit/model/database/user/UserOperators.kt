package com.rodolfonavalon.canadatransit.model.database.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rodolfonavalon.canadatransit.model.database.transit.Operator

@Entity(indices = [
            Index(value = ["operatorOneStopId"], unique = true)
        ],
        foreignKeys = [
            ForeignKey(entity = Operator::class,
                    parentColumns = ["operatorOneStopId"],
                    childColumns = ["operatorOneStopId"],
                    onUpdate = ForeignKey.CASCADE)
        ])
data class UserOperators(
        val operatorOneStopId: String
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

