package com.sanmiaderibigbe.newalbumnotify.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {

        private const val BASE_URL: String = "https://sanmi-test-example.herokuapp.com/"

        internal fun initRetrofitInstance(): NewSongsInterface {

            val retrofit: Retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

            return retrofit.create(NewSongsInterface::class.java)

        }
    }
