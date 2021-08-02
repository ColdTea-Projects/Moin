package de.coldtea.moin.services.model

data class AuthorizationResponse(
    val state: String?,
    val code: String?, val error: String?
)