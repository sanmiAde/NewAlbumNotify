package com.sanmiaderibigbe.newalbumnotify.data

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.net.Uri
import android.provider.MediaStore
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class Repository(private val application: Application) {

    /**
     * @return Return list of artists on users phone.
     */
     fun getArtistList() : LiveData<List<String>> {

        val artistList = mutableListOf<String>()
        val artistListLiveData : MutableLiveData<List<String>> = MutableLiveData<List<String>>()
        doAsync {
            val artistUri: Uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
            val artistCursor = application.contentResolver.query(
                artistUri,
                arrayOf(MediaStore.Audio.Albums.ARTIST),
                null,
                null,
                MediaStore.Audio.ArtistColumns.ARTIST
            )

            if (artistCursor.moveToFirst()) {
                while (!artistCursor.isAfterLast) {
                    val artist = artistCursor.getString(artistCursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST))
                    when (artist) {
                        "<unknown>" -> {

                        }
                        else -> {
                            artistList.add(artist)
                        }
                    }

                    artistCursor.moveToNext()
                }
            }
            artistCursor.close()

            uiThread {
                artistListLiveData.value = artistList
            }

        }
        return artistListLiveData
    }
}