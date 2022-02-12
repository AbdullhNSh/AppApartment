package com.testing.appapartment.modle

import android.util.Log
import java.util.*
import java.util.concurrent.TimeUnit

data class OrderAdmin(var name :String = "",var time:Long=0,var image:String = "")


fun getTimeAgo1(duration: Long): String {
    val now = Date()
    Log.e("hzm",duration.toString())
    val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - duration)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - duration)
    val hours = TimeUnit.MILLISECONDS.toHours(now.time - duration)
    val days = TimeUnit.MILLISECONDS.toDays(now.time - duration)
    return if (seconds < 60) {
        "just now"
    } else if (minutes == 1L) {
        "a minute ago"
    } else if (minutes > 1 && minutes < 60) {
        "$minutes minutes ago"
    } else if (hours == 1L) {
        "an hour ago"
    } else if (hours > 1 && hours < 24) {
        "$hours hours ago"
    } else if (days == 1L) {
        "a day ago"
    } else {
        "$days days ago"
    }
}