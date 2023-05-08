package com.macreai.storyapp.model.remote.response

import com.google.gson.annotations.SerializedName

data class SendStoryResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
