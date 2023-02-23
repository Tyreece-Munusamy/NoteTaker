package com.example.progressupdatedemo.utils

import com.google.firebase.Timestamp
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

fun timestampToFormattedDate(timestamp: Timestamp): String {
    // Feb 16, 2023 09:53
    val simpleDateFormat = SimpleDateFormat("EEE dd, yyyy HH:mm", Locale.ENGLISH)
    val date = timestamp.toDate()
    return simpleDateFormat.format(date).toString()
}

fun <A> String.fromJson(type: Class<A>): A = Gson().fromJson(this, type)

fun <A> A.toJson(): String? = Gson().toJson(this)

fun String.isValid(): Boolean = this.trim().isNotEmpty()