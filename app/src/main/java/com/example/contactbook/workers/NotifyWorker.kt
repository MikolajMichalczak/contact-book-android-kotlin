package com.example.contactbook.workers

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.contactbook.R
import com.example.contactbook.database.ContactsRoomDatabase
import com.example.contactbook.network.RepoApi
import com.example.contactbook.util.sendNotification


class NotifyWorker (ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    companion object{
        private const val TAG = "NotifyWorker"
        private const val ID = "worker_id_data"
        private const val NAME = "worker_name_data"
        private const val WORKER_ERROR = "Error occurred from worker"
    }



    override fun doWork(): Result {

        val appContext = applicationContext
        val contactId = inputData.getInt(ID, 0)
        val contactName = inputData.getString(NAME)

        return try {
            sendNotification(appContext, contactName, contactId)
            val outputData = createOutputDataInt(contactId)

            Result.success(outputData)
        } catch (throwable: Throwable) {
            val outputData = createOutputDataString(WORKER_ERROR)
            Result.failure(outputData)
        }
    }

    private fun sendNotification(context: Context, name: String?, contactId: Int): Int {
        val text = String.format(context.getString(R.string.call_notification_subtitle, name))
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(text, context, contactId)
        return contactId
    }

    private fun createOutputDataInt(contactId: Int): Data {
        return Data.Builder()
            .putInt(ID, contactId)
            .build()
    }

    private fun createOutputDataString(workerError: String): Data {
        return Data.Builder()
            .putString(WORKER_ERROR, workerError)
            .build()
    }
}