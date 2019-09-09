package com.example.avjindersinghsekhon.minimaltodo.model

import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Locale

/**
 * Created on 2019-09-09, 22:26.
 * @author Akram Shokri
 */
fun SimpleDateFormat.convertToLocalDateTime(utcDateTime: String) : String{
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    format.timeZone = TimeZone.getTimeZone("UTC")
    val date = format.parse(utcDateTime)
    val toFormat = SimpleDateFormat("dd/MM/yyyy KK:mm aa", Locale.getDefault())
    toFormat.timeZone = TimeZone.getDefault()

    return toFormat.format(date)
}