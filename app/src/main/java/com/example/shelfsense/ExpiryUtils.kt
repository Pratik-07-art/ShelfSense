package com.example.shelfsense

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

object ExpiryUtils {

    private val dateFormat =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun getDaysUntilExpiry(expiryDate: String): Long {
        return try {
            val expiry = dateFormat.parse(expiryDate) ?: return 0

            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)

            val expiryCalendar = Calendar.getInstance()
            expiryCalendar.time = expiry
            expiryCalendar.set(Calendar.HOUR_OF_DAY, 0)
            expiryCalendar.set(Calendar.MINUTE, 0)
            expiryCalendar.set(Calendar.SECOND, 0)
            expiryCalendar.set(Calendar.MILLISECOND, 0)

            TimeUnit.MILLISECONDS.toDays(
                expiryCalendar.timeInMillis - today.timeInMillis
            )
        } catch (e: Exception) {
            0
        }
    }

    fun isExpired(item: Item): Boolean {
        return getDaysUntilExpiry(item.expiryDate) < 0
    }

    fun isExpiringSoon(item: Item): Boolean {
        val days = getDaysUntilExpiry(item.expiryDate)
        return days in 0..7
    }

    fun isUpcoming(item: Item): Boolean {
        return getDaysUntilExpiry(item.expiryDate) > 7
    }

    fun isLowStock(item: Item): Boolean {
        return item.quantity <= 2
    }

    fun getExpiryStatus(item: Item): String {
        val days = getDaysUntilExpiry(item.expiryDate)

        return when {
            days < 0 -> "Expired"
            days == 0L -> "Expires Today"
            days == 1L -> "Expires Tomorrow"
            days <= 7 -> "Expires in $days days"
            else -> "Expires in $days days"
        }
    }
}