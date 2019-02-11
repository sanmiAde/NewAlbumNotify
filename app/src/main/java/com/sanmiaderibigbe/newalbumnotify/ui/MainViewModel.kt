package com.sanmiaderibigbe.newalbumnotify.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.sanmiaderibigbe.newalbumnotify.data.Repository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository : Repository = Repository(application)

    fun getArtistList() : LiveData<List<String>> {
        return  repository.getArtistList()
    }
}