package com.sanmiaderibigbe.newalbumnotify.data.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Song(
    @SerializedName("songName") @Expose val songName: String?,
    @SerializedName("artistName") @Expose val artistName: String?,
    @SerializedName("pictureURL") @Expose  val pictureURL: String?,
    @SerializedName("releaseDate") @Expose  val releaseDate: String?
)