package com.example.contactbook.util

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.contactbook.MainActivity
import com.example.contactbook.R

private val CONTACT_ID = "contact_id"

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, contactId: Int) {

    val bundle = Bundle()
    bundle.putInt(CONTACT_ID, contactId)

    val contentPendingIntent = NavDeepLinkBuilder(applicationContext)
        .setComponentName(MainActivity::class.java)
        .setGraph(R.navigation.nav_graph)
        .setDestination(R.id.pageContainerFragment)
        .setArguments(bundle)
        .createPendingIntent()

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.call_notification_channel_id)
    )   .setSmallIcon(R.drawable.schedule_icon_24)
        .setContentTitle(applicationContext.getString(R.string.call_notification_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    notify(contactId, builder.build())
}


