package com.zafar.covid19tracking.model

import com.google.gson.annotations.SerializedName

data class Sources (

	@SerializedName("url") val url : String,
	@SerializedName("name") val name : String
)