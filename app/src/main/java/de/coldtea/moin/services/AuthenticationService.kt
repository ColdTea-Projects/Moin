package de.coldtea.moin.services

import android.content.Intent
import android.net.Uri
import android.util.Base64
import de.coldtea.moin.BuildConfig.ROOT_URL_SPOTIFY_AUTH
import de.coldtea.moin.services.SpotifyService.CLIENT_ID
import de.coldtea.moin.services.SpotifyService.REDIRECT_URI
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

class AuthenticationService {
    private val encoding = Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
    val state = UUID.randomUUID().toString()

    private fun generateCodeVerifier() : String {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(64)
        secureRandom.nextBytes(bytes)

        return Base64.encodeToString(bytes, encoding)
    }

    private fun generateCodeChallenge(codeVerifier: String): String{
        val bytes = codeVerifier.toByteArray()
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(bytes)
        val digest = messageDigest.digest()

        return Base64.encodeToString(digest, encoding)
    }

    fun getAuthorizationIntent(): Intent = Uri.parse(ROOT_URL_SPOTIFY_AUTH + "authorize").buildUpon()
        .appendQueryParameter("response_type", "code")
        .appendQueryParameter("client_id", CLIENT_ID)
        .appendQueryParameter("redirect_uri", REDIRECT_URI)
        .appendQueryParameter("state", state)
        .appendQueryParameter("code_challenge", generateCodeChallenge(generateCodeVerifier()))
        .appendQueryParameter("code_challenge_method", "S256")
        .build().let {
            Intent(Intent.ACTION_VIEW, it)
        }

}