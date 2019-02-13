package com.sanmiaderibigbe.newalbumnotify.ui.adapter

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class SongListDecoration (private val largePadding: Int, private val smallPadding: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = smallPadding
        outRect.right = smallPadding
        outRect.top = largePadding
        outRect.bottom = largePadding
    }
}
