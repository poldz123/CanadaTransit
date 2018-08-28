package com.rodolfonavalon.canadatransit.model.database.transit

import android.arch.persistence.room.*
import android.support.annotation.NonNull
import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import com.rodolfonavalon.canadatransit.model.database.converter.room.TransitLandConverter
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
        @SerializedName("onestop_id") val feedOneStopId: String,
        @JsonAdapter(OperatorOneStopIdAdapter::class) val operatorOneStopId: String,

        @SerializedName("name") val name: String?,
        @SerializedName("created_at") val createdAt: DateTime,
        @SerializedName("updated_at") val updatedAt: DateTime,
        @SerializedName("url") val url: String,
        @SerializedName("feed_format") val feedFormat: String,
        @SerializedName("license_use_without_attribution") val licenseUseWithoutAttribution: String,
        @SerializedName("license_create_derived_product") val licenseCreatedDerivedProduct: String,
        @SerializedName("license_redistribute") val licenseRedistribute: String,
        @SerializedName("license_name") val licenseName: String?,
        @SerializedName("license_url") val licenseUrl: String?,
        @SerializedName("license_attribution_text") val licenseAttributionText: String?,
        @SerializedName("last_fetched_at") val lastFetchAt: DateTime,
        @SerializedName("last_imported_at") val lastImportedAt: DateTime,
        @SerializedName("import_status") val importStatus: String,
        @SerializedName("active_feed_version") val activeFeedVersion: String?,
        @SerializedName("feed_versions_url") val feedVersionUrl: String,
        @SerializedName("feed_versions_count") val feedVersionCount: Int,
        @SerializedName("created_or_updated_in_changeset_id") val createdOrUpdatedInChangesetId: Int,
        @SerializedName("import_level_of_active_feed_version") val importLevelActiveFeedVersion: Int,
        @SerializedName("feed_versions") val feedVersion: List<String>,
        @SerializedName("changesets_imported_from_this_feed") val changesetsImportedFromThisFeed: List<Int>,
        @SerializedName("operators_in_feed") val operatorsInFeed: List<OperatorInFeed>
)

class OperatorInFeed(
        @SerializedName("gtfs_agency_id") val gtfsAgencyId: String?,
        @SerializedName("operator_onestop_id") val operatorOneStopId: String,
        @SerializedName("feed_onestop_id") val feedOneStopId: String,
        @SerializedName("operator_url") val operatorUrl: String,
        @SerializedName("feed_url") val feedUrl: String
)

class OperatorOneStopIdAdapter : TypeAdapter<String>() {
    override fun write(gsonOut: JsonWriter, value: String?) {
        DebugUtil.assertTrue(false, "OperatorOneStopIdAdapter does not support writing to json. ")
        gsonOut.nullValue()
    }

    override fun read(reader: JsonReader): String {
        // Read the full operator feed json object
        reader.beginObject()
        while (reader.hasNext()) {
            val objectElementName = reader.nextName()
            // Read the array that contains the operator one stop id
            if (objectElementName == "operators_in_feed") {
                reader.beginArray()
                while (reader.hasNext()) {
                    val arrayElementName = reader.nextName()
                    // Once we get it, we should apply the string to the field of this adapter
                    if (arrayElementName == "operator_onestop_id") {
                        return reader.nextString()
                    }
                }
                reader.endArray()
                break
            }
        }
        reader.endObject()
        DebugUtil.assertTrue(false, "Operator Feed operator-one-stop-id was not found, this seems to be a bug within the api.")
        return ""
    }
}
