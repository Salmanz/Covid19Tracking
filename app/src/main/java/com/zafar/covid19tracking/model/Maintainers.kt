package com.zafar.covid19tracking.model

import com.google.gson.annotations.SerializedName

data class Maintainers (

	@SerializedName("name") val name : String,
	@SerializedName("github") val github : String,
	@SerializedName("flag") val flag : String
)