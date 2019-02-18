package com.sanmiaderibigbe.newalbumnotify.data

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.sanmiaderibigbe.newalbumnotify.data.local.LocalSong
import com.sanmiaderibigbe.newalbumnotify.data.local.LocalSongDao
import com.sanmiaderibigbe.newalbumnotify.data.remote.NetWorkState
import com.sanmiaderibigbe.newalbumnotify.data.remote.NewSongList
import com.sanmiaderibigbe.newalbumnotify.data.remote.RetrofitInstance
import com.sanmiaderibigbe.newalbumnotify.data.remote.Song
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class Repository(private val application: Application) {

    private val networkState: MutableLiveData<NetWorkState> = MutableLiveData()
    private val TAG = "Respository"
    private val localDao: LocalSongDao
    private  var songsBeingReleased: List<LocalSong> = emptyList()
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

            localSongDtos.add(
                LocalSong(
                    it.songName!!,
                    it.artistName!!,
                    it.pictureURL,
                    it.releaseDate,
                    convertDateStringToDateObject(it.releaseDate!!)
                )
            )

        }

        Log.d(TAG, localSongDtos.size.toString())
        return localSongDtos

    }

    private fun convertDateStringToDateObject(dateString: String): Date {
        val format = SimpleDateFormat("d MMMM yyyy")
        return format.parse(dateString)
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

    fun getNetworkState(): LiveData<NetWorkState> {
        return networkState
    }

    fun getOfflineArtists(): LiveData<List<LocalSong>> {
        return localDao.loadAllSongs()
    }

//    fun getReleasedSongList() : List<LocalSong> {
//        return  someTask(localDao).doInBackground()
//    }

//    class someTask(val songDao: LocalSongDao) : AsyncTask<Void, Void, List<LocalSong>>() {
//        public override fun doInBackground(vararg params: Void?):List<LocalSong> {
//            // ...
//            val list = songDao.loadedSongPublishedOnCurrentDate().toMutableList()
//            val currentDateString = convertDateToString(Date()).toLowerCase()
//            return list.filter { it.releaseDate?.toLowerCase() == currentDateString }
//        }
//
//        override fun onPreExecute() {
//            super.onPreExecute()
//            // ...
//        }
//
//
//
//        private fun convertDateToString(date: Date): String {
//            val dateFormat = SimpleDateFormat("d MMMM yyyy")
//            return dateFormat.format(date)
//        }
//    }




    fun getSongsReleasedOnToday(): List<LocalSong> {
        GetSongBeingReleasedToday(localDao).execute()
        return songsBeingReleased
    }

    private fun returnReleasedSongList(songList: List<LocalSong>?){
        songsBeingReleased =  songList!!
    }


    fun getSongPublishedOnCurrentDate(): List<LocalSong>? {

        var releasedSongList: List<LocalSong>? = emptyList()
         doAsync {
            val list = localDao.loadedSongPublishedOnCurrentDate().toMutableList()
            val currentDateString = convertDateToString(Date()).toLowerCase()
            val filteredList = list.filter { it.releaseDate?.toLowerCase() == currentDateString }
             releasedSongList = filteredList
            application.runOnUiThread {

                Toast.makeText(application, releasedSongList.toString(), Toast.LENGTH_LONG).show()
            }

//            val publishedObserver: Observer<List<LocalSong>> = Observer { localSongs -> TODO("Do something with list") }
//            localDao.loadAllSongs().observeForever(publishedObserver)
        }

        return releasedSongList

    }

        private fun convertDateToString(date: Date): String {
            val dateFormat = SimpleDateFormat("d MMMM yyyy")
            return dateFormat.format(date)
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

    inner class GetSongBeingReleasedToday(private val doa: LocalSongDao) : AsyncTask<String, String, List<LocalSong>>(){
        override fun doInBackground(vararg params: String?): List<LocalSong> {
            val list = doa.loadedSongPublishedOnCurrentDate().toMutableList()
            val currentDateString = convertDateToString(Date()).toLowerCase()
            return  list.filter { it.releaseDate?.toLowerCase() == currentDateString }
        }

        private fun convertDateToString(date: Date): String {
            val dateFormat = SimpleDateFormat("d MMMM yyyy")
            return dateFormat.format(date)
        }

        /**
         *
         * Runs on the UI thread after [.doInBackground]. The
         * specified result is the value returned by [.doInBackground].
         *
         *
         * This method won't be invoked if the task was cancelled.
         *
         * @param result The result of the operation computed by [.doInBackground].
         *
         * @see .onPreExecute
         *
         * @see .doInBackground
         *
         * @see .onCancelled
         */
        override fun onPostExecute(result: List<LocalSong>?) {
            super.onPostExecute(result)
            returnReleasedSongList(result!!)

        }
    }
}


