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
import androidx.core.location.LocationManagerCompat
import java.util.Currency
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * Sirf APPROXIMATE (coarse) location se ye pata lagata hai ki aap kis mulk mein hain, taaki sahi currency mein
 * sone/chandi ka bhav dikhaya ja sake. Location kahin save ya server par nahi bheji jati.
 */
object LocationHelper {

    class Result(val country: String, val source: String)

    fun hasPermission(ctx: Context): Boolean =
        ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun currentLocation(ctx: Context): Location? {
        if (!hasPermission(ctx)) return null
        val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null

        // 1) Phone ki pichli maloom location (turant mil jati hai)
        var best: Location? = null
        for (p in lm.getProviders(true)) {
            val l: Location? = try { lm.getLastKnownLocation(p) } catch (e: Exception) { null }
            if (l != null && (best == null || l.time > best.time)) best = l
        }
        if (best != null) return best

        // 2) Ek taaza fix (zyada se zyada 7 second intezar)
        val provider = when {
            lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            lm.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            else -> return null
        }
        val ref = AtomicReference<Location?>(null)
        val latch = CountDownLatch(1)
        try {
            LocationManagerCompat.getCurrentLocation(lm, provider, null, ContextCompat.getMainExecutor(ctx)) { loc ->
                ref.set(loc)
                latch.countDown()
            }
            latch.await(7, TimeUnit.SECONDS)
        } catch (e: Exception) {
            // location nahi mili - neeche SIM/network se try hoga
        }
        return ref.get()
    }

    @Suppress("DEPRECATION")
    private fun countryFromGeocoder(ctx: Context, loc: Location): String? {
        if (!Geocoder.isPresent()) return null
        return try {
            Geocoder(ctx, Locale.ENGLISH).getFromLocation(loc.latitude, loc.longitude, 1)
                ?.firstOrNull()?.countryCode?.takeIf { it.length == 2 }?.uppercase()
        } catch (e: Exception) {
            null
        }
    }

    /** Background thread par chalayein. null = mulk pata nahi chala. */
    fun detectCountry(ctx: Context): Result? {
        val loc = try { currentLocation(ctx) } catch (e: Exception) { null }
        if (loc != null) {
            val cc = countryFromGeocoder(ctx, loc)
            if (cc != null) return Result(cc, "location")
        }
        // Location se na mile to SIM/network ka mulk (iske liye koi permission nahi chahiye)
        val tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        val iso = tm?.networkCountryIso?.takeIf { it.length == 2 } ?: tm?.simCountryIso?.takeIf { it.length == 2 }
        if (iso != null) return Result(iso.uppercase(), "network")
        // Aakhri raasta: phone ki region setting (kam bharosemand, app ise currency badalne ke liye nahi mangta)
        val region = Locale.getDefault().country
        return if (region.length == 2) Result(region.uppercase(), "locale") else null
    }

    private fun localeOf(cc: String): Locale = Locale.Builder().setRegion(cc).build()

    fun currencyFor(cc: String): String = try {
        Currency.getInstance(localeOf(cc)).currencyCode
    } catch (e: Exception) {
        ""
    }

    fun countryName(cc: String): String = try {
        localeOf(cc).getDisplayCountry(Locale.ENGLISH).ifEmpty { cc }
    } catch (e: Exception) {
        cc
    }
}
