package com.macreai.storyapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class PublicResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
