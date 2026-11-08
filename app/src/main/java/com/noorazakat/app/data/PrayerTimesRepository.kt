package com.noorazakat.app.data

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Network-only prayer time adapter. The app must always show the selected method/school. */
data class PrayerTimes(
    val date: LocalDate,
    val timeZone: String,
    val method: String,
    val asrSchool: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String
)

object PrayerTimesRepository {
    private val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ROOT)

    /** method=1 is University of Islamic Sciences, Karachi; school=1 is Hanafi Asr. */
    fun fetch(
        latitude: Double,
        longitude: Double,
        date: LocalDate = LocalDate.now(),
        method: Int = 1,
        school: Int = 1
    ): PrayerTimes {
        require(latitude in -90.0..90.0) { "Latitude out of range" }
        require(longitude in -180.0..180.0) { "Longitude out of range" }
        require(method in 0..23 || method == 99) { "Unsupported calculation method" }
        require(school in 0..1) { "Unsupported Asr school" }

        val url = URL(
            "https://api.aladhan.com/v1/timings/${date.format(dateFormat)}" +
                "?latitude=$latitude&longitude=$longitude&method=$method&school=$school&iso8601=false"
        )
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 12_000
            readTimeout = 12_000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "Noor-A-Zakat-Android")
        }
        try {
            val status = connection.responseCode
            val body = (if (status in 200..299) connection.inputStream else connection.errorStream)
                ?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
                ?: throw IllegalStateException("Prayer service returned an empty response")
            if (status !in 200..299) throw IllegalStateException("Prayer service error HTTP $status")
            val root = JSONObject(body)
            if (root.optInt("code") != 200) throw IllegalStateException(root.optString("status", "Prayer service error"))
            val data = root.getJSONObject("data")
            val timings = data.getJSONObject("timings")
            val meta = data.getJSONObject("meta")
            fun time(key: String): String = timings.optString(key).substringBefore(" ").also {
                require(it.matches(Regex("\\d{2}:\\d{2}"))) { "Invalid $key time from prayer service" }
            }
            return PrayerTimes(
                date = date,
                timeZone = meta.optString("timezone", ""),
                method = if (method == 1) "University of Islamic Sciences, Karachi" else "AlAdhan method $method",
                asrSchool = if (school == 1) "Hanafi" else "Shafi / Standard",
                fajr = time("Fajr"),
                sunrise = time("Sunrise"),
                dhuhr = time("Dhuhr"),
                asr = time("Asr"),
                maghrib = time("Maghrib"),
                isha = time("Isha")
            )
        } finally {
            connection.disconnect()
        }
    }
}
