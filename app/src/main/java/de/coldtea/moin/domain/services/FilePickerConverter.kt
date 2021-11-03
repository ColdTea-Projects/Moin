package de.coldtea.moin.domain.services

import android.content.ContentResolver
import android.net.Uri
import de.coldtea.moin.domain.model.mp3.MP3Object

object FilePickerConverter {

    fun getMP3Object(contentResolver: ContentResolver, uri: Uri): MP3Object{
        val cur = contentResolver.query(uri, null, null, null, null)
        cur?.moveToFirst()

        return MP3Object(
            uri = uri,
            displayName = cur?.getString(2)?:""
        )
    }

    fun stringToUri(uriString: String) : Uri = Uri.parse(uriString)
}