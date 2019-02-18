package com.sanmiaderibigbe.newalbumnotify.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.sanmiaderibigbe.newalbumnotify.data.Repository
import com.sanmiaderibigbe.newalbumnotify.data.local.LocalSong
import com.sanmiaderibigbe.newalbumnotify.data.remote.NetWorkState
import com.sanmiaderibigbe.newalbumnotify.services.NotificationWorkManager

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository : Repository = Repository.getRepository(application)
    private val workManager: WorkManager = WorkManager.getInstance()

    fun getArtistsOnPhoneList() : LiveData<List<String>> {
        return  repository.getArtistList()
    }

    fun getNewSongsOnline() : LiveData<List<LocalSong>>{
        return  repository.initApiCall()
    }

    fun getNeworkstate() : LiveData<NetWorkState> {
        return  repository.getNetworkState()
    }

    fun getData(): LiveData<List<LocalSong>>  {
        return  repository.getOfflineArtists()
    }

    fun getArtistReleasedOnCurrentDate() : List<LocalSong>?{

        return   repository.getSongsReleasedOnToday()
    }

    fun initNotificationWorker(){
        workManager.enqueue(OneTimeWorkRequest.from(NotificationWorkManager::class.java))
    }
}