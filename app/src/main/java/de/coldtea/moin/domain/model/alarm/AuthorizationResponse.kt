package de.coldtea.moin.domain.model.alarm

data class AuthorizationResponse(
    val state: String?,
    val code: String?, val error: String?
)