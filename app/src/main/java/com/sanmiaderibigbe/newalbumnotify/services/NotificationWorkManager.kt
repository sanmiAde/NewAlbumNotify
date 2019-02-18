package com.sanmiaderibigbe.newalbumnotify.services

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sanmiaderibigbe.newalbumnotify.data.Repository
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.NotificationCompat
import com.sanmiaderibigbe.newalbumnotify.R
import com.sanmiaderibigbe.newalbumnotify.data.local.LocalSong
import android.content.Context.NOTIFICATION_SERVICE
import android.support.v4.content.ContextCompat.getSystemService





class NotificationWorkManager(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val TAG: String = "NotificationWorker"
    private val newReleaseId = 0;
    private val appContext = applicationContext
    private val application = appContext.applicationContext as Application
    private val repository = Repository.getRepository(application)
    private val notificationManager = NotificationManagerCompat.from(application)
    private val messageChannel = "new release channel"
    /**
     * Override this method to do your actual background processing.  This method is called on a
     * background thread - you are required to **synchronously** do your work and return the
     * [androidx.work.ListenableWorker.Result] from this method.  Once you return from this
     * method, the Worker is considered to have finished what its doing and will be destroyed.  If
     * you need to do your work asynchronously on a thread of your own choice, see
     * [ListenableWorker].
     *
     *
     * A Worker is given a maximum of ten minutes to finish its execution and return a
     * [androidx.work.ListenableWorker.Result].  After this time has expired, the Worker will
     * be signalled to stop.
     *
     * @return The [androidx.work.ListenableWorker.Result] of the computation; note that
     * dependent work will not execute if you use
     * [androidx.work.ListenableWorker.Result.failure] or
     * [androidx.work.ListenableWorker.Result.failure]
     */
    override fun doWork(): Result {


        return try {
            val songList = repository.getSongsReleasedOnToday()
            Log.d(TAG, songList.toString())
           if(songList.isNotEmpty()){
               initNotification(application,songList)
           }

            Result.success()
        } catch (throwable: Throwable) {
            Log.d(TAG, throwable.message)
            return Result.failure()
        }
    }


    private fun createMessagesNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "New Release "
            val channel = NotificationChannel(
                messageChannel,
                name,
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager =
                applicationContext.getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initNotification(application: Application, songList: List<LocalSong>) {
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        createMessagesNotificationChannel()


        var text = "${songList.size} are being released today. You should check it out."

        var builder = NotificationCompat.Builder(
            applicationContext, messageChannel
        ).setSmallIcon(com.sanmiaderibigbe.newalbumnotify.R.drawable.notification_icon_background)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).build()


       notificationManager.notify(newReleaseId, builder)

    }
}