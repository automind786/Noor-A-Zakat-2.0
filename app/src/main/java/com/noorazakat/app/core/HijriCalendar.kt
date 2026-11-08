package com.noorazakat.app.core

import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

object HijriCalendar {
    private val monthNames = mapOf(
        "hi" to listOf("मुहर्रम", "सफ़र", "रबीउल-अव्वल", "रबीउस्सानी", "जुमादल ऊला", "जुमादल आख़िरा", "रजब", "शाबान", "रमज़ान", "शव्वाल", "ज़ुल-क़ादा", "ज़ुल-हिज्जा"),
        "ur" to listOf("محرم", "صفر", "ربیع الاول", "ربیع الآخر", "جمادی الاولیٰ", "جمادی الآخرہ", "رجب", "شعبان", "رمضان", "شوال", "ذوالقعدہ", "ذوالحجہ"),
        "en" to listOf("Muharram", "Safar", "Rabi al-Awwal", "Rabi al-Thani", "Jumada al-Ula", "Jumada al-Akhirah", "Rajab", "Sha'ban", "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"),
        "ar" to listOf("مُحَرَّم", "صَفَر", "رَبِيع الأَوَّل", "رَبِيع الآخِر", "جُمَادَى الأُولَى", "جُمَادَى الآخِرَة", "رَجَب", "شَعْبَان", "رَمَضَان", "شَوَّال", "ذُو القَعْدَة", "ذُو الحِجَّة")
    )

    fun today(offsetDays: Int = 0): HijrahDate = HijrahDate.from(LocalDate.now().plusDays(offsetDays.toLong()))

    fun fromGregorian(date: LocalDate, offsetDays: Int = 0): HijrahDate =
        HijrahDate.from(date.plusDays(offsetDays.toLong()))

    fun monthName(month: Int, language: String): String =
        monthNames[language]?.getOrNull(month - 1) ?: monthNames.getValue("hi")[month - 1]

    private fun yearSuffix(language: String): String = when (language) {
        "ur" -> "ھ"
        "ar" -> "هـ"
        "en" -> "AH"
        else -> "हिजरी"
    }

    fun format(date: HijrahDate, language: String): String {
        val day = date.get(ChronoField.DAY_OF_MONTH)
        val month = date.get(ChronoField.MONTH_OF_YEAR)
        val year = date.get(ChronoField.YEAR)
        return "$day ${monthName(month, language)} $year ${yearSuffix(language)}"
    }

    fun monthTitle(date: HijrahDate, language: String): String {
        val month = date.get(ChronoField.MONTH_OF_YEAR)
        val year = date.get(ChronoField.YEAR)
        return "${monthName(month, language)} $year ${yearSuffix(language)}"
    }

    fun monthLength(year: Int, month: Int): Int = HijrahDate.of(year, month, 1).lengthOfMonth()

    fun firstDayGregorian(year: Int, month: Int): LocalDate =
        LocalDate.ofEpochDay(HijrahDate.of(year, month, 1).toEpochDay())

    fun dateInMonth(year: Int, month: Int, day: Int): HijrahDate = HijrahDate.of(year, month, day)

    fun nextMonth(date: HijrahDate): HijrahDate = date.plus(1, ChronoUnit.MONTHS) as HijrahDate

    fun previousMonth(date: HijrahDate): HijrahDate = date.minus(1, ChronoUnit.MONTHS) as HijrahDate

    fun eventName(month: Int, day: Int, language: String): String? {
        val hi = mapOf(
            (1 to 1) to "इस्लामी नया साल (आम कैलेंडर परंपरा)",
            (1 to 10) to "यौम-ए-आशूरा",
            (3 to 12) to "ईद-ए-मीलादुन्नबी ﷺ (स्थानीय परंपरा के अनुसार)",
            (7 to 27) to "शब-ए-मेराज (प्रचलित तारीख)",
            (8 to 15) to "शब-ए-बरात",
            (9 to 1) to "रमज़ान का आरम्भ (चाँद दिखने पर निर्भर)",
            (9 to 27) to "रमज़ान की 27वीं रात (लैलतुल-क़द्र की एक संभावित रात)",
            (10 to 1) to "ईद-उल-फ़ित्र (चाँद दिखने पर निर्भर)",
            (12 to 9) to "यौम-ए-अरफ़ा",
            (12 to 10) to "ईद-उल-अज़्हा (चाँद दिखने पर निर्भर)"
        )
        val ur = mapOf(
            (1 to 1) to "اسلامی نیا سال (عام تقویمی روایت)",
            (1 to 10) to "یومِ عاشورہ",
            (3 to 12) to "عید میلاد النبی ﷺ (مقامی روایت کے مطابق)",
            (7 to 27) to "شبِ معراج (مشہور تاریخ)",
            (8 to 15) to "شبِ براءت",
            (9 to 1) to "رمضان کا آغاز (رویتِ ہلال سے مشروط)",
            (9 to 27) to "رمضان کی ستائیسویں رات (شبِ قدر کی ممکنہ رات)",
            (10 to 1) to "عید الفطر (رویتِ ہلال سے مشروط)",
            (12 to 9) to "یومِ عرفہ",
            (12 to 10) to "عید الاضحیٰ (رویتِ ہلال سے مشروط)"
        )
        val en = mapOf(
            (1 to 1) to "Islamic New Year (common calendar observance)",
            (1 to 10) to "Day of Ashura",
            (3 to 12) to "Mawlid (according to local tradition)",
            (7 to 27) to "Isra and Mi'raj (commonly observed date)",
            (8 to 15) to "Shab-e-Barat",
            (9 to 1) to "Ramadan begins (subject to moon sighting)",
            (9 to 27) to "27th night of Ramadan (a possible Laylat al-Qadr)",
            (10 to 1) to "Eid al-Fitr (subject to moon sighting)",
            (12 to 9) to "Day of Arafah",
            (12 to 10) to "Eid al-Adha (subject to moon sighting)"
        )
        val ar = mapOf(
            (1 to 1) to "رأس السنة الهجرية (وفق التقويم الشائع)",
            (1 to 10) to "يوم عاشوراء",
            (3 to 12) to "المولد النبوي ﷺ (بحسب العرف المحلي)",
            (7 to 27) to "الإسراء والمعراج (التاريخ المشهور)",
            (8 to 15) to "ليلة البراءة",
            (9 to 1) to "بداية رمضان (بحسب رؤية الهلال)",
            (9 to 27) to "ليلة 27 رمضان (ليلة محتملة للقدر)",
            (10 to 1) to "عيد الفطر (بحسب رؤية الهلال)",
            (12 to 9) to "يوم عرفة",
            (12 to 10) to "عيد الأضحى (بحسب رؤية الهلال)"
        )
        val key = month to day
        return when (language) {
            "ur" -> ur[key]
            "en" -> en[key]
            "ar" -> ar[key]
            else -> hi[key]
        }
    }

    fun weekdayLabels(language: String): List<String> = when (language) {
        "ur" -> listOf("اتوار", "پیر", "منگل", "بدھ", "جمعرات", "جمعہ", "ہفتہ")
        "en" -> listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        "ar" -> listOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")
        else -> listOf("रवि", "सोम", "मंगल", "बुध", "गुरु", "शुक्र", "शनि")
    }

    fun gregorianShort(date: LocalDate, language: String): String {
        val months = when (language) {
            "ur" -> listOf("جنوری", "فروری", "مارچ", "اپریل", "مئی", "جون", "جولائی", "اگست", "ستمبر", "اکتوبر", "نومبر", "دسمبر")
            "ar" -> listOf("يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو", "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر")
            "en" -> listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            else -> listOf("जन॰", "फ़र॰", "मार्च", "अप्रैल", "मई", "जून", "जुल॰", "अग॰", "सित॰", "अक्टू॰", "नव॰", "दिस॰")
        }
        val monthText = months[date.monthValue - 1]
        return when (language) {
            "ur", "ar" -> "${date.dayOfMonth} $monthText ${date.year}"
            else -> "${date.dayOfMonth} $monthText ${date.year}"
        }
    }
}
