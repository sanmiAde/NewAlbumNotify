package com.sanmiaderibigbe.newalbumnotify.data.remote

import retrofit2.Call
import retrofit2.http.GET

interface NewSongsInterface {
    @GET("/")
    fun getNewSongs(): Call<NewSongList>
}