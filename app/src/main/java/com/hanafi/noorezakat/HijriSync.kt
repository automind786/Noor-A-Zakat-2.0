package com.hanafi.noorezakat

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

/**
 * Online Hijri calendar sync.
 *
 * Uses AlAdhan's documented Islamic calendar endpoint with UAQ by default.
 * Important: a calculated Hijri date is not the same thing as a local moon-sighting
 * announcement. The UI therefore exposes a manual correction/authority setting.
 */
object HijriSync {
    private const val BASE = "https://api.aladhan.com/v1"
    private const val CONNECT_MS = 7000
    private const val READ_MS = 7000

    data class Day(
        val gregorian: String,
        val hijriDay: Int,
        val hijriMonth: Int,
        val hijriMonthName: String,
        val hijriYear: Int,
        val holidays: List<String>,
        val method: String
    )

    fun fetchDay(date: LocalDate, method: String = "UAQ"): Day? {
        val dd = "%02d-%02d-%04d".format(Locale.US, date.dayOfMonth, date.monthValue, date.year)
        val url = "$BASE/gToH/$dd?calendarMethod=$method"
        val root = getJson(url) ?: return null
        if (root.optInt("code") != 200) return null
        return parseDay(root.optJSONObject("data") ?: return null)
    }

    fun fetchGregorianMonth(year: Int, month: Int, method: String = "UAQ"): List<Day> {
        val url = "$BASE/gToHCalendar/$month/$year?calendarMethod=$method"
        val root = getJson(url) ?: return emptyList()
        val arr = root.optJSONArray("data") ?: return emptyList()
        val out = ArrayList<Day>(arr.length())
        for (i in 0 until arr.length()) {
            parseDay(arr.optJSONObject(i))?.let(out::add)
        }
        return out
    }

    fun fetchSpecialDays(): List<Pair<Int, Int>> {
        val root = getJson("$BASE/specialDays") ?: return emptyList()
        val arr = root.optJSONArray("data") ?: return emptyList()
        return buildList {
            for (i in 0 until arr.length()) {
                val o = arr.optJSONObject(i) ?: continue
                add(o.optInt("month") to o.optInt("day"))
            }
        }
    }

    private fun parseDay(o: JSONObject): Day? {
        val h = o.optJSONObject("hijri") ?: return null
        val g = o.optJSONObject("gregorian") ?: return null
        val gd = g.optString("date")
        val parts = h.optString("date").split("-")
        if (parts.size != 3) return null
        return Day(
            gregorian = gd,
            hijriDay = parts[0].toIntOrNull() ?: return null,
            hijriMonth = parts[1].toIntOrNull() ?: return null,
            hijriMonthName = h.optJSONObject("month")?.optString("en").orEmpty(),
            hijriYear = parts[2].toIntOrNull() ?: return null,
            holidays = buildList {
                h.optJSONArray("holidays")?.let { a ->
                    for (i in 0 until a.length()) add(a.optString(i))
                }
                h.optJSONArray("adjustedHolidays")?.let { a ->
                    for (i in 0 until a.length()) add(a.optString(i))
                }
            }.distinct(),
            method = h.optString("method", "UAQ")
        )
    }

    private fun getJson(urlString: String): JSONObject? {
        var conn: HttpURLConnection? = null
        return try {
            conn = URL(urlString).openConnection() as HttpURLConnection
            conn.connectTimeout = CONNECT_MS
            conn.readTimeout = READ_MS
            conn.requestMethod = "GET"
            conn.setRequestProperty("Accept", "application/json")
            conn.setRequestProperty("User-Agent", "Noor-A-Zakat/2.3")
            if (conn.responseCode !in 200..299) return null
            BufferedReader(InputStreamReader(conn.inputStream, Charsets.UTF_8)).use { r ->
                JSONObject(r.readText())
            }
        } catch (_: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }
}
