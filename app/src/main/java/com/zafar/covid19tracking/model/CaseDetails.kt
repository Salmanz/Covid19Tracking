package com.zafar.covid19tracking.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CaseDetails(
    @get:Exclude
    var id: String? = null,
    var area: String = "",
    var active: String = "",
    var recovered: String = "",
    var death: String = "",
    @get:Exclude
    var isDeleted: Boolean = false
) : Parcelable {
//    @Parcelize
//    data class CaseDetails(val id: String, val area: String, val active: String, val recovered: String, val death: String) : Parcelable

    override fun equals(other: Any?): Boolean {
        return if (other is CaseDetails) {
            other.id == id
        } else false
    }



    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
    }
}