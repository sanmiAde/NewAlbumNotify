package com.sanmiaderibigbe.newalbumnotify.data.local

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface LocalSongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSongs(localSongs: List<LocalSong>)



    @Query("SELECT * FROM  song_list_table ORDER BY releaseDate ASC")
    fun loadAllSongs(): LiveData<List<LocalSong>>
}