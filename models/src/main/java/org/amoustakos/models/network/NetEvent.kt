package org.amoustakos.models.network


data class NetEvent<out T>(

        val errorCode: Int? = null,
        val hasError: Boolean = true,
        val errorMessage: String? = null,
        val item: T? = null

)
