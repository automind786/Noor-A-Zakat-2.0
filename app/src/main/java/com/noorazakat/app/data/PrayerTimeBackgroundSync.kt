package com.noorazakat.app.data

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.noorazakat.app.core.AppText
import com.noorazakat.app.widget.HijriWidgetProvider
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

/** Stores only a user-approved, one-time location fix; it never requests background location. */
data class SavedPrayerLocation(val latitude: Double, val longitude: Double, val method: Int)

object PrayerTimeBackgroundSync {
    private const val PREFS = "noor_prayer_settings"
    private const val WORK_NAME = "noor_prayer_times_refresh"

    fun savedLocation(context: Context): SavedPrayerLocation? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.getBoolean("enabled", false)) return null
        val latitude = prefs.getString("latitude", null)?.toDoubleOrNull() ?: return null
        val longitude = prefs.getString("longitude", null)?.toDoubleOrNull() ?: return null
        if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) return null
        return SavedPrayerLocation(latitude, longitude, prefs.getInt("method", 1))
    }

    fun savedMethod(context: Context): Int = savedLocation(context)?.method ?: 1

    fun updateMethod(context: Context, method: Int) {
        require(method in setOf(1, 3, 4))
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.getBoolean("enabled", false)) {
            prefs.edit().putInt("method", method).apply()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "noor_prayer_times_refresh_now", androidx.work.ExistingWorkPolicy.REPLACE,
                androidx.work.OneTimeWorkRequestBuilder<PrayerTimesRefreshWorker>().build()
            )
        }
    }

    fun clearLocation(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork("noor_prayer_times_refresh_now")
        context.getSharedPreferences("noor_widget_cache", Context.MODE_PRIVATE).edit().remove("next_prayer_line").apply()
        HijriWidgetProvider.refreshAll(context)
    }

    fun saveLocation(context: Context, latitude: Double, longitude: Double, method: Int, language: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("latitude", latitude.toString())
            .putString("longitude", longitude.toString())
            .putInt("method", method)
            .putString("language", language)
            .putBoolean("enabled", true)
            .apply()
        schedule(context)
    }

    fun updateLanguage(context: Context, language: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("language", language).apply()
    }

    fun schedule(context: Context) {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val request = PeriodicWorkRequestBuilder<PrayerTimesRefreshWorker>(12, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request
        )
    }
}

class PrayerTimesRefreshWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = try {
        val prefs = applicationContext.getSharedPreferences("noor_prayer_settings", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("enabled", false)) return Result.success()
        val lat = prefs.getString("latitude", null)?.toDoubleOrNull() ?: return Result.success()
        val lon = prefs.getString("longitude", null)?.toDoubleOrNull() ?: return Result.success()
        val method = prefs.getInt("method", 1)
        val language = prefs.getString("language", "hi") ?: "hi"
        val zone = ZoneId.systemDefault()
        val now = LocalDateTime.now(zone)
        val prayerTimes = PrayerTimesRepository.fetch(lat, lon, LocalDate.now(zone), method = method, school = 1)
        val names = listOf(
            AppText.get(language, "fajr") to prayerTimes.fajr,
            AppText.get(language, "dhuhr") to prayerTimes.dhuhr,
            AppText.get(language, "asr") to prayerTimes.asr,
            AppText.get(language, "maghrib") to prayerTimes.maghrib,
            AppText.get(language, "isha") to prayerTimes.isha
        )
        val next = names.mapNotNull { (name, value) ->
            runCatching {
                var candidate = now.toLocalDate().atTime(LocalTime.parse(value))
                if (!candidate.isAfter(now)) candidate = candidate.plusDays(1)
                name to candidate
            }.getOrNull()
        }.minByOrNull { it.second }
        val line = if (next != null) {
            "${AppText.get(language, "next_prayer")}: ${next.first} ${next.second.format(DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT))}"
        } else {
            "${AppText.get(language, "next_prayer")}: ${AppText.get(language, "fajr")}"
        }
        applicationContext.getSharedPreferences("noor_widget_cache", Context.MODE_PRIVATE)
            .edit().putString("next_prayer_line", line).putLong("prayer_updated_at", System.currentTimeMillis()).apply()
        HijriWidgetProvider.refreshAll(applicationContext)
        Result.success()
    } catch (_: Exception) {
        Result.retry()
    }
}
