package com.example.contactbook.util

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.contactbook.MainActivity
import com.example.contactbook.R

private val CONTACT_ID = "contact_id"

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, contactId: Int) {

    val bundle = Bundle()
    bundle.putInt(CONTACT_ID, contactId)

    val contentIntent = Intent(applicationContext, MainActivity::class.java)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        contactId,
        contentIntent,
        PendingIntent.FLAG_ONE_SHOT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.call_notification_channel_id)
    )   .setSmallIcon(R.drawable.schedule_icon_24)
        .setLargeIcon(vectorToBitmap(R.drawable.schedule_icon_256, applicationContext))
        .setContentTitle(applicationContext.getString(R.string.call_notification_title))
        .setContentText(messageBody)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setContentIntent(contentPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    notify(contactId, builder.build())
}

fun vectorToBitmap(drawableId: Int, context: Context): Bitmap? {
    val drawable = getDrawable(context, drawableId) ?: return null
    val bitmap = createBitmap(
        drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    ) ?: return null
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}


