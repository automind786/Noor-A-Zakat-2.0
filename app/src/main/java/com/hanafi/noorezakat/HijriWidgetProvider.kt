package com.hanafi.noorezakat

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import java.time.LocalDate
import java.util.Locale

class HijriWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { updateOne(context, manager, it) }
        HijriWidgetScheduler.schedule(context)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        HijriWidgetScheduler.schedule(context)
    }

    override fun onDisabled(context: Context) {
        HijriWidgetScheduler.cancel(context)
        super.onDisabled(context)
    }

    companion object {
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, HijriWidgetProvider::class.java))
            ids.forEach { updateOne(context, manager, it) }
        }

        private fun updateOne(context: Context, manager: AppWidgetManager, id: Int) {
            val day = HijriSync.fetchDay(LocalDate.now()) ?: return
            val views = RemoteViews(context.packageName, R.layout.widget_hijri)
            views.setTextViewText(R.id.widgetHijri, "${day.hijriDay} ${day.hijriMonthName} ${day.hijriYear} AH")
            views.setTextViewText(R.id.widgetGregorian, LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy", Locale.getDefault())))
            val event = day.holidays.firstOrNull()
            views.setTextViewText(R.id.widgetSpecial, event ?: "No special Islamic day today")
            views.setTextViewText(R.id.widgetStatus, "Online • ${day.method}")
            manager.updateAppWidget(id, views)
        }
    }
}

object HijriWidgetScheduler {
    private const val ACTION = "com.hanafi.noorezakat.UPDATE_HIJRI_WIDGET"

    fun schedule(context: Context) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = android.content.Intent(context, HijriWidgetUpdateReceiver::class.java).setAction(ACTION)
        val pi = android.app.PendingIntent.getBroadcast(
            context, 4123, intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        val next = java.util.Calendar.getInstance().apply {
            add(java.util.Calendar.DATE, 1)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 7)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
        alarm.setInexactRepeating(android.app.AlarmManager.RTC, next, 24L * 60L * 60L * 1000L, pi)
    }

    fun cancel(context: Context) {
        val intent = android.content.Intent(context, HijriWidgetUpdateReceiver::class.java).setAction(ACTION)
        val pi = android.app.PendingIntent.getBroadcast(
            context, 4123, intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        (context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager).cancel(pi)
    }
}
