package com.example.contactbook.database.converters

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDateTime


class Converters {
    @TypeConverter
    fun dateTimeToString(dateTime: LocalDateTime): String? {
        return dateTime?.toString()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun stringToDateTime(string: String?): LocalDateTime {
        return LocalDateTime.parse(string)
    }
}