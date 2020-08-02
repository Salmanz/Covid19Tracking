package com.zafar.covid19tracking.model

import com.google.gson.annotations.SerializedName

data class CountryInfo (

	@SerializedName("country") val country : String,
	@SerializedName("url") val url : String,
	@SerializedName("maintainers") val maintainers : List<Maintainers>,
	@SerializedName("sources") val sources : List<Sources>,
	@SerializedName("state") val state : String,
	@SerializedName("cases") val cases : Int,
	@SerializedName("recovered") val recovered : Int,
	@SerializedName("deaths") val deaths : Int,
	@SerializedName("active") val active : Int,
	@SerializedName("rating") val rating : Double,
	@SerializedName("coordinates") val coordinates : List<Double>,
	@SerializedName("tz") val tz : List<String>,
	@SerializedName("featureId") val featureId : String,
	@SerializedName("population") val population : Int,
	@SerializedName("populationDensity") val populationDensity : Double,
	@SerializedName("countryId") val countryId : String,
	@SerializedName("stateId") val stateId : String,
	@SerializedName("name") val name : String,
	@SerializedName("level") val level : String
)