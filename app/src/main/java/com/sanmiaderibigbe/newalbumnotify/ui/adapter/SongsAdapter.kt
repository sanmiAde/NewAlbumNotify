package com.sanmiaderibigbe.newalbumnotify.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sanmiaderibigbe.newalbumnotify.data.local.LocalSong
import com.sanmiaderibigbe.newalbumnotify.databinding.SongListItemBinding

class SongsAdapter(context: Context) : RecyclerView.Adapter<SongsAdapter.ViewHolder>() {


    private var songList: List<LocalSong>? = null

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val binding = SongListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun setTodoList(songs : List<LocalSong>?) {
        songList = songs
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int =  songList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        val song: LocalSong? = songList?.get(p1)
        holder.newSongsBinding.song = song
    }

    inner class ViewHolder(val newSongsBinding: SongListItemBinding) : RecyclerView.ViewHolder(newSongsBinding.root)

}