package com.sanmiaderibigbe.newalbumnotify.ui

import android.Manifest
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.sanmiaderibigbe.newalbumnotify.R
import com.sanmiaderibigbe.newalbumnotify.data.remote.NetWorkState
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


    private fun observeNetworkState() {
        viewModel.getNeworkstate().observe(this, Observer { network: NetWorkState? ->

            when (network) {
                is NetWorkState.NotLoaded -> {
                    Toast.makeText(this, "Not loaded", Toast.LENGTH_SHORT).show()
                }

                is NetWorkState.Loading -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                    showProgressBar()
                }

                is NetWorkState.Success -> {
                    hideProgressBar()
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                }

                is NetWorkState.Error -> {
                    val netWorkError: NetWorkState.Error = network
                    Toast.makeText(this, netWorkError.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private  fun showProgressBar(){
        progressBar.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
    }

    private  fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
        artists.visibility = View.VISIBLE
    }


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private  fun getNewSongs(){
        observeNetworkState()
        viewModel.getNewSongsOnline().observe(this, Observer { it ->
            Log.d("main", it.toString())
            artists.text = it?.size.toString()
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
