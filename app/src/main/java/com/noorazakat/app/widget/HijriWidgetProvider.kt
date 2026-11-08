package com.noorazakat.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.noorazakat.app.MainActivity
import com.noorazakat.app.R
import com.noorazakat.app.core.AppText
import com.noorazakat.app.core.HijriCalendar
import com.noorazakat.app.data.IslamicEventsFeed
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField

class HijriWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { id -> updateOne(context, manager, id) }
    }

    override fun onAppWidgetOptionsChanged(context: Context, manager: AppWidgetManager, appWidgetId: Int, newOptions: android.os.Bundle) {
        updateOne(context, manager, appWidgetId)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_TOGGLE) {
            val id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            if (id != AppWidgetManager.INVALID_APPWIDGET_ID) {
                val prefs = context.getSharedPreferences("noor_widget_state", Context.MODE_PRIVATE)
                val key = "expanded_$id"
                prefs.edit().putBoolean(key, !prefs.getBoolean(key, false)).apply()
                val manager = AppWidgetManager.getInstance(context)
                updateOne(context, manager, id)
            }
            return
        }
        super.onReceive(context, intent)
        if (intent.action == Intent.ACTION_DATE_CHANGED || intent.action == Intent.ACTION_TIMEZONE_CHANGED ||
            intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            refreshAll(context)
        }
    }

    companion object {
        private const val ACTION_TOGGLE = "com.noorazakat.app.widget.TOGGLE_DETAILS"

        fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, HijriWidgetProvider::class.java))
            if (ids.isNotEmpty()) HijriWidgetProvider().onUpdate(context, manager, ids)
        }

        private fun updateOne(context: Context, manager: AppWidgetManager, widgetId: Int) {
            val prefs = context.getSharedPreferences("noor_settings", Context.MODE_PRIVATE)
            val language = prefs.getString("language", "hi") ?: "hi"
            val offset = prefs.getInt("hijri_offset", 0)
            val hijri: HijrahDate = HijriCalendar.today(offset)
            val dateText = HijriCalendar.format(hijri, language)
            val month = hijri.get(ChronoField.MONTH_OF_YEAR)
            val day = hijri.get(ChronoField.DAY_OF_MONTH)
            val eventCards = IslamicEventsFeed.findEvents(context, month, day, language)
            val eventCard = eventCards.firstOrNull()
            val eventText = eventCards.joinToString(" • ") { it.title }.ifBlank {
                HijriCalendar.eventName(month, day, language) ?: AppText.get(language, "no_special_day")
            }
            val eventDescription = eventCards.take(3).joinToString("\n") { "${it.title}: ${it.description}" }
                .ifBlank { AppText.get(language, "moon_note") }
            val widgetPrefs = context.getSharedPreferences("noor_widget_state", Context.MODE_PRIVATE)
            val expanded = widgetPrefs.getBoolean("expanded_$widgetId", false)
            val selectedDesign = prefs.getString("widget_design", "auto") ?: "auto"
            val prayerInfo = context.getSharedPreferences("noor_widget_cache", Context.MODE_PRIVATE)
                .getString("next_prayer_line", null)
                ?: when (language) {
                    "ur" -> "نماز کے اوقات دیکھنے کے لیے چھوئیں"
                    "en" -> "Open prayer times in Noor Zakat Amanah"
                    "ar" -> "افتح مواقيت الصلاة في نور الزكاة"
                    else -> "नमाज़ के वक़्त देखने के लिए खोलें"
                }

            val views = RemoteViews(context.packageName, R.layout.hijri_widget).apply {
                setTextViewText(R.id.widget_hijri_date, dateText)
                setTextViewText(R.id.widget_event, eventText)
                setTextViewText(R.id.widget_event_description, eventDescription)
                setTextViewText(R.id.widget_prayer_info, prayerInfo)
                setViewVisibility(R.id.widget_details, if (expanded) View.VISIBLE else View.GONE)

                val background = when (selectedDesign) {
                    "emerald" -> R.drawable.widget_background
                    "ramadan" -> R.drawable.widget_background_ramadan
                    "night" -> R.drawable.widget_background_night
                    "eid" -> R.drawable.widget_background_eid
                    else -> when (eventCard?.theme) {
                        "ramadan" -> R.drawable.widget_background_ramadan
                        "eid", "hajj" -> R.drawable.widget_background_eid
                        "night_sky", "ashura", "miraj", "shab_e_barat" -> R.drawable.widget_background_night
                        else -> when {
                            month == 9 -> R.drawable.widget_background_ramadan
                            (month == 10 && day == 1) || (month == 12 && day == 10) -> R.drawable.widget_background_eid
                            else -> R.drawable.widget_background
                        }
                    }
                }
                setInt(R.id.widget_root, "setBackgroundResource", background)

                val toggleIntent = Intent(context, HijriWidgetProvider::class.java).apply {
                    action = ACTION_TOGGLE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                }
                val togglePending = PendingIntent.getBroadcast(
                    context, 8100 + widgetId, toggleIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setOnClickPendingIntent(R.id.widget_root, togglePending)

                val openApp = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("open_page", "CALENDAR")
                }
                val openPending = PendingIntent.getActivity(
                    context, 9100 + widgetId, openApp,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setOnClickPendingIntent(R.id.widget_open, openPending)
            }
            manager.updateAppWidget(widgetId, views)
        }
    }
}
