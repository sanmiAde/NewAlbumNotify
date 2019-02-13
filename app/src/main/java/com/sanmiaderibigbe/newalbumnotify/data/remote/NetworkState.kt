package com.sanmiaderibigbe.newalbumnotify.data.remote


sealed class NetWorkState {
    object Loading : NetWorkState()
    object Success : NetWorkState()
    object NotLoaded : NetWorkState()
    data class Error(val errorMessage: String?) : NetWorkState()
}