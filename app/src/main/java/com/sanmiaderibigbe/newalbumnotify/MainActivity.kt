package com.sanmiaderibigbe.newalbumnotify

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity() {


    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS: Int = 2324

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermission()


    }

    private fun getArtistList() {
        doAsync {
            val artistUri: Uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
            val artistCursor = contentResolver.query(
                artistUri,
                arrayOf(MediaStore.Audio.Albums.ARTIST),
                null,
                null,
                MediaStore.Audio.ArtistColumns.ARTIST
            )

            val artistList = mutableListOf<String>()

            if (artistCursor.moveToFirst()) {
                while (!artistCursor.isAfterLast) {
                    val artist =
                        artistCursor.getString(artistCursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST))
                    artistList.add(artist)
                    artistCursor.moveToNext()
                }
            }
            artistCursor.close()

            uiThread {
                Log.d("Artisit", artistList.toString())
                artistList.forEach {
                    artists.append("$it \n")
                }
            }
        }
    }


    fun getPermission(){
      // Here, thisActivity is the current activity
      if (ContextCompat.checkSelfPermission(this,
              Manifest.permission.READ_EXTERNAL_STORAGE)
          != PackageManager.PERMISSION_GRANTED) {

          // Permission is not granted
          // Should we show an explanation?
          if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                  Manifest.permission.READ_EXTERNAL_STORAGE)) {
              // Show an explanation to the user *asynchronously* -- don't block
              // this thread waiting for the user's response! After the user
              // sees the explanation, try again to request the permission.
          } else {
              // No explanation needed, we can request the permission.
              ActivityCompat.requestPermissions(this,
                  arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                  MY_PERMISSIONS_REQUEST_READ_CONTACTS)

              // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
              // app-defined int constant. The callback method gets the
              // result of the request.
          }
      } else {
          // Permission has already been granted
          getArtistList()
      }
  }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, do your work....
                    getArtistList()
                } else {
                    // permission denied
                    // Disable the functionality that depends on this permission.
                }
                return
            }
        }// other 'case' statements for other permssions
    }
}
