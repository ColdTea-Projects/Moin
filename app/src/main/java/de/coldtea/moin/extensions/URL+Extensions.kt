package de.coldtea.moin.extensions

import android.net.Uri
import de.coldtea.moin.domain.model.alarm.AuthorizationResponse

fun String.convertToAuthorizationResponse(): AuthorizationResponse? {
    val uri = Uri.parse(this)

    val state = uri.getQueryParameter("state")
    val code = uri.getQueryParameter("code")
    val error = uri.getQueryParameter("error")

    if (state == null) return null

    return AuthorizationResponse(state,code,error)
}