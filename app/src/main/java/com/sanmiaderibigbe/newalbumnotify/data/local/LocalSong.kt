package com.sanmiaderibigbe.newalbumnotify.data.local

import android.arch.persistence.room.Entity
import android.support.annotation.NonNull
import java.util.*

@Entity(tableName = "song_list_table", primaryKeys = ["songName","artistName"])
data class LocalSong(
    @NonNull
    val songName: String,
    @NonNull
    val artistName: String,

    val pictureURL: String?,

    val releaseDate: String?,

    val date: Date
)


