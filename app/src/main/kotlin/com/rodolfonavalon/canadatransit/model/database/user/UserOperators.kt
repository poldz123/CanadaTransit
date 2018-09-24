package com.rodolfonavalon.canadatransit.model.database.user

import android.arch.persistence.room.*
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

