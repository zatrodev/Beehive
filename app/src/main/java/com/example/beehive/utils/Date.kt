package com.example.beehive.utils

import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date

fun getDaysDifferenceFromNow(specificDate: Date): Long {
    val specificLocalDate = specificDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val currentLocalDate = LocalDate.now()

    return ChronoUnit.DAYS.between(currentLocalDate, specificLocalDate)
}

fun addDaysToDate(date: Date, daysToAdd: Long): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.add(Calendar.DAY_OF_YEAR, daysToAdd.toInt())
    return calendar.time
}