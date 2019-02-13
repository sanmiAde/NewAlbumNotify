package com.sanmiaderibigbe.newalbumnotify.data

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.sanmiaderibigbe.newalbumnotify.data.local.LocalSong
import com.sanmiaderibigbe.newalbumnotify.data.local.LocalSongDao
import com.sanmiaderibigbe.newalbumnotify.data.remote.NetWorkState
import com.sanmiaderibigbe.newalbumnotify.data.remote.NewSongList
import com.sanmiaderibigbe.newalbumnotify.data.remote.RetrofitInstance
import com.sanmiaderibigbe.newalbumnotify.data.remote.Song
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository(private val application: Application) {

    val networkState: MutableLiveData<NetWorkState> = MutableLiveData()
    private val TAG = "Respository"
    private val localDao: LocalSongDao

    init {
        val db = AppDatabase.getDatabase(application, false)
        localDao = db.localSongDao()
    }

    fun initApiCall(): LiveData<List<LocalSong>> {

        networkState.value = NetWorkState.NotLoaded

        val service = RetrofitInstance.initRetrofitInstance()
        val call: Call<NewSongList> = service.getNewSongs()


        networkCall(call)

        return getData()
    }

    private fun getData(): LiveData<List<LocalSong>> {
        return localDao.loadAllSongs()
    }

    private fun networkCall(call: Call<NewSongList>) {
        networkState.value = NetWorkState.Loading

        call.enqueue(object : Callback<NewSongList> {
            /**
             * Invoked when a network exception occurred talking to the server or when an unexpected
             * exception occurred creating the request or processing the response.
             */
            override fun onFailure(call: Call<NewSongList>, t: Throwable) {
                Log.e(TAG, t.message)
                networkState.value = NetWorkState.Error(t.message)
            }

            /**
             * Invoked for a received HTTP response.
             *
             *
             * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
             * Call [Response.isSuccessful] to determine if the response indicates success.
             */
            override fun onResponse(call: Call<NewSongList>, response: Response<NewSongList>) {
                when {
                    response?.isSuccessful -> {
                        networkState.value = NetWorkState.Success

                        doAsync {
                            localDao.insertSongs(convertToDatabaseDto(response.body()?.newSong!!))
                        }
                    }
                    else -> {
                        networkState.value = NetWorkState.Error(response.message())

                    }
                }
            }
        })
    }

    private fun convertToDatabaseDto(remoteSongs: List<Song>): List<LocalSong> {

        val localSongDtos = mutableListOf<LocalSong>()

        remoteSongs.forEach {

            localSongDtos.add(LocalSong(0, it.artistName, it.songName, it.pictureURL, it.releaseDate))

        }

        return localSongDtos

    }


    /**
     * @return Return list of artists on users phone.
     */
    fun getArtistList(): LiveData<List<String>> {

        val artistList = mutableListOf<String>()
        val artistListLiveData: MutableLiveData<List<String>> = MutableLiveData<List<String>>()
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
                    val artist =
                        artistCursor.getString(artistCursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST))
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

    companion object {
        private var instance: Repository? = null

        @Synchronized
        fun getRepository(application: Application): Repository {
            if (instance == null) {
                instance = Repository(application)
            }
            return instance!!
        }
    }
}