package com.rodolfonavalon.canadatransit.model.database.transit

import android.arch.persistence.room.*
import com.rodolfonavalon.canadatransit.model.database.converter.room.TransitLandConverter
import com.squareup.moshi.Json
import org.joda.time.DateTime

@Entity(indices = [
            (Index(value = ["feedOneStopId"], unique = true)),
            (Index(value = ["operatorOneStopId"], unique = true))
        ],
        foreignKeys = [
                ForeignKey(entity = Operator::class,
                        parentColumns = ["operatorOneStopId"],
                        childColumns = ["operatorOneStopId"],
                        onDelete = ForeignKey.CASCADE)
        ])
@TypeConverters(TransitLandConverter::class)
data class OperatorFeed(
        @PrimaryKey
        @field:Json(name = "onestop_id") val feedOneStopId: String,
        val operatorOneStopId: String,

        @field:Json(name ="name") val name: String?,
        @field:Json(name ="created_at") val createdAt: DateTime,
        @field:Json(name ="updated_at") val updatedAt: DateTime,
        @field:Json(name ="url") val url: String,
        @field:Json(name ="feed_format") val feedFormat: String,
        @field:Json(name ="license_use_without_attribution") val licenseUseWithoutAttribution: String,
        @field:Json(name ="license_create_derived_product") val licenseCreatedDerivedProduct: String,
        @field:Json(name ="license_redistribute") val licenseRedistribute: String,
        @field:Json(name ="license_name") val licenseName: String?,
        @field:Json(name ="license_url") val licenseUrl: String?,
        @field:Json(name ="license_attribution_text") val licenseAttributionText: String?,
        @field:Json(name ="last_fetched_at") val lastFetchAt: DateTime,
        @field:Json(name ="last_imported_at") val lastImportedAt: DateTime,
        @field:Json(name ="import_status") val importStatus: String,
        @field:Json(name ="active_feed_version") val activeFeedVersion: String?,
        @field:Json(name ="feed_versions_url") val feedVersionUrl: String,
        @field:Json(name ="feed_versions_count") val feedVersionCount: Int,
        @field:Json(name ="created_or_updated_in_changeset_id") val createdOrUpdatedInChangesetId: Int,
        @field:Json(name ="import_level_of_active_feed_version") val importLevelActiveFeedVersion: Int,
        @field:Json(name ="feed_versions") val feedVersion: List<String>,
        @field:Json(name ="changesets_imported_from_this_feed") val changesetsImportedFromThisFeed: List<Int>,
        @field:Json(name ="operators_in_feed") val operatorsInFeed: List<OperatorInFeed>
)

class OperatorInFeed(
        @field:Json(name ="gtfs_agency_id") val gtfsAgencyId: String?,
        @field:Json(name ="operator_onestop_id") val operatorOneStopId: String,
        @field:Json(name ="feed_onestop_id") val feedOneStopId: String,
        @field:Json(name ="operator_url") val operatorUrl: String,
        @field:Json(name ="feed_url") val feedUrl: String
)
//
//class OperatorOneStopIdAdapter : TypeAdapter<String>() {
//    override fun write(gsonOut: JsonWriter, value: String?) {
//        DebugUtil.assertTrue(false, "OperatorOneStopIdAdapter does not support writing to json. ")
//        gsonOut.nullValue()
//    }
//
//    override fun read(reader: JsonReader): String {
//        // Read the full operator feed json object
//        reader.beginObject()
//        while (reader.hasNext()) {
//            val objectElementName = reader.nextName()
//            // Read the array that contains the operator one stop id
//            if (objectElementName == "operators_in_feed") {
//                reader.beginArray()
//                while (reader.hasNext()) {
//                    val arrayElementName = reader.nextName()
//                    // Once we get it, we should apply the string to the field of this adapter
//                    if (arrayElementName == "operator_onestop_id") {
//                        return reader.nextString()
//                    }
//                }
//                reader.endArray()
//                break
//            }
//        }
//        reader.endObject()
//        DebugUtil.assertTrue(false, "Operator Feed operator-one-stop-id was not found, this seems to be a bug within the api.")
//        return ""
//    }
//}
