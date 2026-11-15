package com.hanafi.noorezakat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class HijriWidgetUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == "com.hanafi.noorezakat.UPDATE_HIJRI_WIDGET") {
            Thread { HijriWidgetProvider.updateAll(context) }.start()
        }
    }
}
