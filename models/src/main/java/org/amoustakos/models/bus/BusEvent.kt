package org.amoustakos.models.bus

data class BusEvent<out T>(
        val hasError: Boolean = true,
        val errorMessage: String? = null,
        val item: T? = null
)