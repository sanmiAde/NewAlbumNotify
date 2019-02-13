package com.sanmiaderibigbe.newalbumnotify.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.sanmiaderibigbe.newalbumnotify.data.Repository
import com.sanmiaderibigbe.newalbumnotify.data.local.LocalSong
import com.sanmiaderibigbe.newalbumnotify.data.remote.NetWorkState

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository : Repository = Repository.getRepository(application)

    fun getArtistsOnPhoneList() : LiveData<List<String>> {
        return  repository.getArtistList()
    }

    fun getNewSongsOnline() : LiveData<List<LocalSong>>{
        return  repository.initApiCall()
    }

    fun getNeworkstate() : LiveData<NetWorkState> {
        return  repository.getNetworkState()
    }
}