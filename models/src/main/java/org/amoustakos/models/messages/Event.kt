package org.amoustakos.models.messages

data class Event<out T>(

		val hasError: Boolean = true,
		val errorMessage: String? = null,
		val item: T? = null

)