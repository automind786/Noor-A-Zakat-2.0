package com.noorazakat.app.core

import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HijriCalendarTest {
    @Test
    fun monthNamesAreLocalized() {
        assertEquals("मुहर्रम", HijriCalendar.monthName(1, "hi"))
        assertEquals("محرم", HijriCalendar.monthName(1, "ur"))
        assertEquals("Muharram", HijriCalendar.monthName(1, "en"))
        assertEquals("مُحَرَّم", HijriCalendar.monthName(1, "ar"))
    }

    @Test
    fun yearSuffixFollowsSelectedLanguage() {
        val sample = HijrahDate.of(1448, 1, 1)
        assertTrue(HijriCalendar.format(sample, "hi").endsWith("हिजरी"))
        assertTrue(HijriCalendar.format(sample, "ur").endsWith("ھ"))
        assertTrue(HijriCalendar.format(sample, "en").endsWith("AH"))
        assertTrue(HijriCalendar.format(sample, "ar").endsWith("هـ"))
    }

    @Test
    fun monthLengthIsWithinExpectedTabularRange() {
        (1..12).forEach { month ->
            val length = HijriCalendar.monthLength(1448, month)
            assertTrue("Unexpected month length for $month: $length", length in 29..30)
        }
    }

    @Test
    fun localOffsetMovesHijriDateByRequestedDays() {
        val date = LocalDate.of(2026, 11, 7)
        val base = HijriCalendar.fromGregorian(date)
        val plus = HijriCalendar.fromGregorian(date, 1)
        assertEquals(base.plus(1, ChronoUnit.DAYS), plus)
    }

    @Test
    fun eventNamesAreQualifiedAsObservances() {
        assertTrue(HijriCalendar.eventName(9, 27, "en")!!.contains("possible Laylat al-Qadr"))
        assertTrue(HijriCalendar.eventName(10, 1, "hi")!!.contains("चाँद दिखने पर निर्भर"))
    }
}
