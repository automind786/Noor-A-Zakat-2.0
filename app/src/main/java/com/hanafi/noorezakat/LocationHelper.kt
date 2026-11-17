package com.hanafi.noorezakat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import java.util.Currency
import java.util.Locale

object LocationHelper {

    class Result(val country: String, val source: String)

    fun hasPermission(ctx: Context): Boolean =
        ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun lastKnownLocation(ctx: Context): Location? {
        if (!hasPermission(ctx)) return null
        val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
        var best: Location? = null
        for (provider in lm.getProviders(true)) {
            val l = try { lm.getLastKnownLocation(provider) } catch (_: Exception) { null }
            if (l != null && (best == null || l.time > best.time)) {
                best = l
            }
        }
        return best
    }

    @Suppress("DEPRECATION")
    private fun countryFromGeocoder(ctx: Context, loc: Location): String? {
        if (!Geocoder.isPresent()) return null
        return try {
            Geocoder(ctx, Locale.ENGLISH).getFromLocation(loc.latitude, loc.longitude, 1)
                ?.firstOrNull()?.countryCode?.takeIf { it.length == 2 }?.uppercase()
        } catch (_: Exception) {
            null
        }
    }

    fun detectCountry(ctx: Context): Result? {
        val loc = lastKnownLocation(ctx)
        if (loc != null) {
            val cc = countryFromGeocoder(ctx, loc)
            if (cc != null) return Result(cc, "location")
        }
        val tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        val iso = tm?.networkCountryIso?.takeIf { it.length == 2 } ?: tm?.simCountryIso?.takeIf { it.length == 2 }
        if (iso != null) return Result(iso.uppercase(), "network")

        val region = Locale.getDefault().country
        return if (region.length == 2) Result(region.uppercase(), "locale") else null
    }

    private fun localeOf(cc: String): Locale = Locale.Builder().setRegion(cc).build()

    fun currencyFor(cc: String): String = try {
        Currency.getInstance(localeOf(cc)).currencyCode
    } catch (_: Exception) {
        ""
    }

    fun countryName(cc: String): String = try {
        localeOf(cc).getDisplayCountry(Locale.ENGLISH).ifEmpty { cc }
    } catch (_: Exception) {
        cc
    }
}
