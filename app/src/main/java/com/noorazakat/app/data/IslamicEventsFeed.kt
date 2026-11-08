package com.noorazakat.app.data

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.noorazakat.app.BuildConfig
import com.noorazakat.app.widget.HijriWidgetProvider
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

/** Remote editorial feed for local Hijri observances. Moon sighting is never inferred from this feed. */
data class IslamicEventCard(
    val id: String,
    val title: String,
    val description: String,
    val theme: String,
    val moonSightingDependent: Boolean,
    val sourceName: String,
    val sourceUrl: String
)

object IslamicEventsFeed {
    private const val PREFS = "noor_events_cache"
    private const val KEY_JSON = "feed_json"
    private const val UNIQUE_WORK = "noor_islamic_events_refresh"

    fun schedule(context: Context) {
        if (BuildConfig.ISLAMIC_EVENTS_FEED_URL.isBlank()) return
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val request = PeriodicWorkRequestBuilder<IslamicEventsSyncWorker>(12, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK, ExistingPeriodicWorkPolicy.KEEP, request
        )
    }

    fun syncNow(context: Context): Boolean {
        val endpoint = BuildConfig.ISLAMIC_EVENTS_FEED_URL.trim()
        if (endpoint.isBlank()) return false
        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Cache-Control", "no-cache")
            setRequestProperty("User-Agent", "Noor-A-Zakat-Android")
        }
        try {
            val status = connection.responseCode
            if (status !in 200..299) return false
            val json = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val root = JSONObject(json)
            require(root.optInt("schemaVersion") == 1) { "Unsupported events feed schema" }
            val events = root.optJSONArray("events") ?: return false
            require(events.length() in 1..500) { "Invalid event count" }
            val ids = mutableSetOf<String>()
            val allowedThemes = setOf("default", "new_year", "ashura", "mawlid", "ramadan", "eid", "hajj", "night_sky", "miraj", "shab_e_barat", "urs", "badr", "green_gold")
            for (i in 0 until events.length()) {
                val event = events.getJSONObject(i)
                require(event.optInt("month") in 1..12) { "Invalid Hijri month" }
                require(event.optInt("day") in 1..30) { "Invalid Hijri day" }
                val id = event.optString("id").trim()
                require(id.matches(Regex("[a-z0-9_]{2,64}")) && ids.add(id)) { "Missing or duplicate event id" }
                require(event.optString("theme", "default") in allowedThemes) { "Unknown event theme" }
                val titles = event.optJSONObject("title") ?: error("Event title is missing")
                val descriptions = event.optJSONObject("description") ?: error("Event descriptions are missing")
                require(listOf("hi", "ur", "en", "ar").all { titles.optString(it).isNotBlank() && descriptions.optString(it).isNotBlank() }) {
                    "Every event must have all four localized titles and descriptions"
                }
                val uri = URL(event.optString("sourceUrl"))
                require(uri.protocol == "https" && uri.host in setOf("www.dawateislami.net", "dawateislami.net", "data2.dawateislami.net")) {
                    "Event source must be an approved HTTPS reference"
                }
                require(event.optString("sourceName").isNotBlank()) { "Source name is required" }
            }
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(KEY_JSON, json).putLong("updated_at", System.currentTimeMillis()).apply()
            HijriWidgetProvider.refreshAll(context)
            return true
        } finally {
            connection.disconnect()
        }
    }

    private fun cachedJson(context: Context): String? {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_JSON, null)?.let { return it }
        return runCatching {
            context.assets.open("islamic-events.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
        }.getOrNull()
    }

    fun findEvents(context: Context, month: Int, day: Int, language: String): List<IslamicEventCard> {
        val raw = cachedJson(context) ?: return emptyList()
        return runCatching {
            val events = JSONObject(raw).getJSONArray("events")
            val lang = if (language in listOf("hi", "ur", "en", "ar")) language else "hi"
            buildList {
                for (i in 0 until events.length()) {
                    val event = events.getJSONObject(i)
                    if (event.optInt("month") == month && event.optInt("day") == day) {
                        val titles = event.optJSONObject("title") ?: continue
                        val descriptions = event.optJSONObject("description") ?: JSONObject()
                        val sourceUrl = event.optString("sourceUrl")
                        val parsed = runCatching { URL(sourceUrl) }.getOrNull() ?: continue
                        if (parsed.protocol != "https" || parsed.host !in setOf("www.dawateislami.net", "dawateislami.net", "data2.dawateislami.net")) continue
                        add(IslamicEventCard(
                            id = event.getString("id"),
                            title = titles.optString(lang, titles.optString("hi")),
                            description = descriptions.optString(lang, descriptions.optString("hi")),
                            theme = event.optString("theme", "default"),
                            moonSightingDependent = event.optBoolean("moonSightingDependent", true),
                            sourceName = event.optString("sourceName", "Dawat-e-Islami"),
                            sourceUrl = sourceUrl
                        ))
                    }
                }
            }
        }.getOrDefault(emptyList())
    }

    fun findEvent(context: Context, month: Int, day: Int, language: String): IslamicEventCard? =
        findEvents(context, month, day, language).firstOrNull()

    fun eventDays(context: Context, month: Int): Set<Int> {
        val raw = cachedJson(context) ?: return emptySet()
        return runCatching {
            val events = JSONObject(raw).getJSONArray("events")
            buildSet {
                for (i in 0 until events.length()) {
                    val event = events.getJSONObject(i)
                    if (event.optInt("month") == month) add(event.optInt("day"))
                }
            }
        }.getOrDefault(emptySet())
    }

    fun lastUpdated(context: Context): Long = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong("updated_at", 0L)
}

class IslamicEventsSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = try {
        if (IslamicEventsFeed.syncNow(applicationContext)) Result.success() else Result.retry()
    } catch (_: Exception) {
        Result.retry()
    }
}
