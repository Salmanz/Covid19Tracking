package com.zafar.covid19tracking.model

data class CoronaEntity(
    val data_source: String = "",
    val data_source_name: String = "",
    val info: DataInfo
) {
    data class DataInfo(
        var country: String? = "",
        val case_actives: Long? = 0,
        var case_confirms: Long? = 0,
        var case_deaths: Long? = 0,
        var case_recovered: Long? = 0,
        val flags: String? = "",
        var longitude: Double? = 0.0,
        val latitude: Double? = 0.0
    )
}
