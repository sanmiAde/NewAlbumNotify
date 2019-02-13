package com.sanmiaderibigbe.newalbumnotify.ui

import android.Manifest
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.sanmiaderibigbe.newalbumnotify.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private  lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        getPermission()

        getNewSongs()
    }

    private fun getPermission(){
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
                  Companion.MY_PERMISSIONS_REQUEST_READ_CONTACTS
              )

              // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
              // app-defined int constant. The callback method gets the
              // result of the request.
          }
      } else {
          // Permission has already been granted
          //getArtistList()
      }
  }


    private fun getArtistList() {
        viewModel.getArtistsOnPhoneList().observe(this, Observer { artist ->
            Log.d("Art", artist.toString())
            artist?.forEach {
                artists.append("$it \n")
            }
        })
    }

    private  fun getNewSongs(){
        viewModel.getNewSongsOnline().observe(this, Observer { songs ->
            songs?.forEach {
                artists.append("$it.toString() \n")
            }
        })
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Companion.MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, do your work....
                    //getArtistList()

                } else {
                    // permission denied
                    // Disable the functionality that depends on this permission.
                }
                return
            }
        }// other 'case' statements for other permssions
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_READ_CONTACTS: Int = 2324
    }
}
