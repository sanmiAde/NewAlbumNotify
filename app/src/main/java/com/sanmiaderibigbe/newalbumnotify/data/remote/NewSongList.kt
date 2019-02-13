package com.sanmiaderibigbe.newalbumnotify.data.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class NewSongList(@SerializedName("newSong") @Expose  val newSong: List<Song>? = null)
