package com.sanmiaderibigbe.newalbumnotify.data.local

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull

@Entity(tableName = "song_list_table")
data class LocalSong(

    @NonNull
    @PrimaryKey
    var id: Int? = 0,

    val songName: String?,

    val artistName: String?,

    val pictureURL: String?,

    val releaseDate: String?


)


