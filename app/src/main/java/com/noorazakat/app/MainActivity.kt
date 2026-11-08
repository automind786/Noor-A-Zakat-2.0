package com.noorazakat.app
import java.time.LocalDateTime

import android.Manifest
import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.noorazakat.app.core.AppPage
import com.noorazakat.app.core.AppText
import com.noorazakat.app.core.FitrahCalculator
import com.noorazakat.app.core.FitrahFood
import com.noorazakat.app.core.FiqhZakatEngine
import com.noorazakat.app.core.ZakatCalculationInput
import com.noorazakat.app.core.ZakatCalculationResult
import com.noorazakat.app.core.ZakatDecisionStatus
import com.noorazakat.app.core.ZakatHawlBasis
import com.noorazakat.app.core.SilverNisabSource
import com.noorazakat.app.core.GoldPurityCalculator
import com.noorazakat.app.core.AssetOwnershipCalculator
import com.noorazakat.app.core.JewelleryWeightUnit
import com.noorazakat.app.core.HijriCalendar
import com.noorazakat.app.core.VerifiedZakatEstimate
import com.noorazakat.app.core.parseNonNegativeDecimal
import com.noorazakat.app.data.ReferenceCatalog
import com.noorazakat.app.data.MarketRatesRepository
import com.noorazakat.app.data.PrayerTimes
import com.noorazakat.app.data.PrayerTimesRepository
import com.noorazakat.app.data.ZakatDraftVault
import com.noorazakat.app.data.PrayerTimeBackgroundSync
import com.noorazakat.app.data.IslamicEventsFeed
import com.noorazakat.app.data.AssetGuideCatalog
import com.noorazakat.app.data.ReferenceItem
import com.noorazakat.app.widget.HijriWidgetProvider
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.animation.animateContentSize
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import java.time.LocalTime
import java.time.Duration
import java.time.ZoneId
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject

private val Ink = Color(0xFF071A16)
private val DeepGreen = Color(0xFF0D2A22)
private val CardGreen = Color(0xFF102F26)
private val CardGreen2 = Color(0xFF16382D)
private val Gold = Color(0xFFE0C17A)
private val PaleGold = Color(0xFFFFE8B0)
private val Muted = Color(0xFFA8B9B1)
private val SoftWhite = Color(0xFFF5F1E7)
private val Line = Color(0xFF345247)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initial = intent?.getStringExtra("open_page")
        setContent { NoorApplication(initialPage = initial) }
    }
}

@Composable
private fun NoorApplication(initialPage: String?) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("noor_settings", Context.MODE_PRIVATE) }
    var language by remember { mutableStateOf(prefs.getString("language", "hi") ?: "hi") }
    var hijriOffset by remember { mutableIntStateOf(prefs.getInt("hijri_offset", 0)) }
    var page by remember { mutableStateOf(pageFromName(initialPage) ?: AppPage.HOME) }
    var showIntro by remember { mutableStateOf(true) }
    var askWidget by remember { mutableStateOf(false) }
    var referenceToShow by remember { mutableStateOf<ReferenceItem?>(null) }
    var eventsVersion by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(1750)
        showIntro = false
        runCatching { IslamicEventsFeed.schedule(context) }
        val didRefreshEvents = withContext(Dispatchers.IO) {
            runCatching { IslamicEventsFeed.syncNow(context) }.getOrDefault(false)
        }
        if (didRefreshEvents) eventsVersion += 1
        runCatching { if (!prefs.getBoolean("widget_prompt_seen", false)) askWidget = true }
    }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Gold,
        onPrimary = Ink,
        secondary = PaleGold,
        background = Ink,
        surface = DeepGreen,
        onSurface = SoftWhite
    )) {
        val direction = if (language == "ur" || language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
        CompositionLocalProvider(LocalLayoutDirection provides direction) {
            if (showIntro) {
                BismillahIntro(language)
            } else {
                MainScaffold(
                    page = page,
                    language = language,
                    hijriOffset = hijriOffset,
                    eventsVersion = eventsVersion,
                    onNavigate = { page = it },
                    onReference = { referenceToShow = it },
                    onAddWidget = { requestHijriWidget(context, language) },
                    onLanguage = { newLanguage ->
                        language = newLanguage
                        prefs.edit().putString("language", newLanguage).apply()
                        PrayerTimeBackgroundSync.updateLanguage(context, newLanguage)
                        HijriWidgetProvider.refreshAll(context)
                    },
                    onOffset = { value ->
                        hijriOffset = value.coerceIn(-2, 2)
                        prefs.edit().putInt("hijri_offset", hijriOffset).apply()
                        HijriWidgetProvider.refreshAll(context)
                    }
                )
            }
        }
    }

    if (askWidget && !showIntro) {
        AlertDialog(
            onDismissRequest = {
                askWidget = false
                prefs.edit().putBoolean("widget_prompt_seen", true).apply()
            },
            containerColor = CardGreen,
            titleContentColor = PaleGold,
            textContentColor = SoftWhite,
            title = { Text(AppText.get(language, "add_widget"), fontWeight = FontWeight.Bold) },
            text = { Text(AppText.get(language, "widget_prompt") + "\n\n" + AppText.get(language, "widget_help")) },
            confirmButton = {
                TextButton(onClick = {
                    askWidget = false
                    prefs.edit().putBoolean("widget_prompt_seen", true).apply()
                    requestHijriWidget(context, language)
                }) { Text(AppText.get(language, "widget_add"), color = Gold) }
            },
            dismissButton = {
                TextButton(onClick = {
                    askWidget = false
                    prefs.edit().putBoolean("widget_prompt_seen", true).apply()
                }) { Text(AppText.get(language, "later"), color = Muted) }
            }
        )
    }

    referenceToShow?.let { item ->
        ReferenceDialog(item = item, language = language, onDismiss = { referenceToShow = null })
    }
}

private fun pageFromName(value: String?): AppPage? = when (value) {
    "CALENDAR" -> AppPage.CALENDAR
    "ZAKAT" -> AppPage.ZAKAT
    "FITRAH" -> AppPage.FITRAH
    "PRAYER" -> AppPage.PRAYER
    "ASSET_GUIDE" -> AppPage.ASSET_GUIDE
    else -> null
}

private fun requestHijriWidget(context: Context, language: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val manager = AppWidgetManager.getInstance(context)
        if (manager.isRequestPinAppWidgetSupported) {
            val accepted = manager.requestPinAppWidget(
                ComponentName(context, HijriWidgetProvider::class.java),
                null,
                null
            )
            if (!accepted) Toast.makeText(context, AppText.get(language, "widget_help"), Toast.LENGTH_LONG).show()
            return
        }
    }
    Toast.makeText(context, AppText.get(language, "widget_help"), Toast.LENGTH_LONG).show()
}

@Composable
private fun BismillahIntro(language: String) {
    var ready by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(120); ready = true }
    val alpha by animateFloatAsState(if (ready) 1f else 0f, animationSpec = tween(900, easing = FastOutSlowInEasing), label = "introAlpha")
    val scale by animateFloatAsState(if (ready) 1f else 0.88f, animationSpec = tween(1050, easing = FastOutSlowInEasing), label = "introScale")
    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF071A16), Color(0xFF10382B), Color(0xFF071A16)))
        ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(28.dp)) {
            Text("☾", color = Gold, fontSize = 52.sp, modifier = Modifier.alpha(alpha).scale(scale))
            Spacer(Modifier.height(26.dp))
            Text(
                "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيْمِ",
                color = PaleGold,
                fontSize = 31.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().alpha(alpha).scale(scale)
            )
            Spacer(Modifier.height(24.dp))
            Text("نُورُ الزَّكَاة", color = Gold, fontSize = 23.sp, fontWeight = FontWeight.Bold, modifier = Modifier.alpha(alpha))
            Spacer(Modifier.height(8.dp))
            Text("NOOR-A-ZAKAT", color = Muted, fontSize = 13.sp, letterSpacing = 3.sp, modifier = Modifier.alpha(alpha))
            Spacer(Modifier.height(30.dp))
            Box(Modifier.width(112.dp).height(1.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, Gold, Color.Transparent))))
            Spacer(Modifier.height(14.dp))
            Text(AppText.get(language, "intro_tagline"), color = Muted, fontSize = 12.sp, modifier = Modifier.alpha(alpha))
        }
    }
}

@Composable
private fun MainScaffold(
    page: AppPage,
    language: String,
    hijriOffset: Int,
    eventsVersion: Int,
    onNavigate: (AppPage) -> Unit,
    onReference: (ReferenceItem) -> Unit,
    onAddWidget: () -> Unit,
    onLanguage: (String) -> Unit,
    onOffset: (Int) -> Unit
) {
    Scaffold(
        containerColor = Ink,
        contentWindowInsets = WindowInsets.statusBars,
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF0A211B), contentColor = Gold, windowInsets = WindowInsets.navigationBars) {
                val bottomItems = listOf(
                    Triple(AppPage.HOME, AppText.get(language, "home"), Icons.Default.Home),
                    Triple(AppPage.ZAKAT, AppText.get(language, "zakat"), Icons.Default.Calculate),
                    Triple(AppPage.PRAYER, AppText.get(language, "prayer_title"), Icons.Default.NightsStay),
                    Triple(AppPage.CALENDAR, AppText.get(language, "calendar"), Icons.Default.CalendarMonth),
                    Triple(AppPage.MORE, AppText.get(language, "more"), Icons.Default.Menu)
                )
                bottomItems.forEach { (target, label, icon) ->
                    NavigationBarItem(
                        selected = page == target,
                        onClick = { onNavigate(target) },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, fontSize = 10.sp, maxLines = 1) },
                        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                            selectedIconColor = Ink,
                            selectedTextColor = PaleGold,
                            indicatorColor = Gold,
                            unselectedIconColor = Muted,
                            unselectedTextColor = Muted
                        )
                    )
                }
            }
        }
    ) { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            when (page) {
                AppPage.HOME -> HomeScreen(language, hijriOffset, eventsVersion, onNavigate, onReference)
                AppPage.ZAKAT -> ZakatScreen(language, onReference)
                AppPage.FITRAH -> FitrahScreen(language, onReference)
                AppPage.PRAYER -> PrayerScreen(language)
                AppPage.CALENDAR -> CalendarScreen(language, hijriOffset, eventsVersion)
                AppPage.HAWL -> HawlScreen(language, onReference)
                AppPage.REFERENCES -> ReferencesScreen(language, onReference)
                AppPage.MASARIF -> MasarifScreen(language, onReference)
                AppPage.TASBIH -> TasbihScreen(language)
                AppPage.ASSET_GUIDE -> AssetGuideScreen(language, onReference)
                AppPage.SETTINGS -> SettingsScreen(language, hijriOffset, onLanguage, onOffset)
                AppPage.MORE -> MoreScreen(language, onNavigate, onAddWidget)
            }
        }
    }
}

@Composable
private fun ScreenHeader(title: String, subtitle: String? = null, onBack: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Gold) }
            Spacer(Modifier.width(4.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, color = PaleGold, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            if (subtitle != null) Text(subtitle, color = Muted, fontSize = 12.sp)
        }
        Text("☾", color = Gold, fontSize = 26.sp)
    }
}

@Composable
private fun HomeScreen(
    language: String,
    hijriOffset: Int,
    eventsVersion: Int,
    onNavigate: (AppPage) -> Unit,
    onReference: (ReferenceItem) -> Unit
) {
    val context = LocalContext.current
    val today = remember(hijriOffset) { HijriCalendar.today(hijriOffset) }
    val localDate = LocalDate.now()
    val month = today.get(ChronoField.MONTH_OF_YEAR)
    val day = today.get(ChronoField.DAY_OF_MONTH)
    val feedEvents = remember(eventsVersion, month, day, language) { IslamicEventsFeed.findEvents(context, month, day, language) }
    val event = feedEvents.takeIf { it.isNotEmpty() }?.joinToString(" • ") { it.title }
        ?: HijriCalendar.eventName(month, day, language)
    val eventTheme = feedEvents.firstOrNull()?.theme ?: "default"
    var eventExpanded by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 22.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        item {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(AppText.get(language, "brand"), color = PaleGold, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.7.sp)
                        Text(AppText.get(language, "tagline"), color = Muted, fontSize = 12.sp)
                    }
                    Surface(
                        modifier = Modifier.clickable { onNavigate(AppPage.ZAKAT) },
                        color = CardGreen,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Line)
                    ) {
                        Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Gold)
                            Spacer(Modifier.width(6.dp))
                            Text(if (language == "ur") "میرا حساب" else if (language == "en") "My account" else if (language == "ar") "حسابي" else "मेरा हिसाब", color = SoftWhite, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        item {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigate(AppPage.ZAKAT) },
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF173B2E)),
                    border = BorderStroke(1.4.dp, Gold)
                ) {
                    Row(Modifier.fillMaxWidth().padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFF234A38), shape = RoundedCornerShape(16.dp)) {
                            Text("⚖", fontSize = 30.sp, modifier = Modifier.padding(13.dp))
                        }
                        Spacer(Modifier.width(13.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                if (language == "en") "Zakat calculation"
                                else if (language == "ur") "زکوٰۃ کا حساب"
                                else if (language == "ar") "حساب الزكاة"
                                else "ज़कात का हिसाब",
                                color = PaleGold, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                if (language == "en") "Gold • silver • cash • business • debts for review"
                                else if (language == "ur") "سونا • چاندی • نقدی • کاروبار • قرض کا جائزہ"
                                else if (language == "ar") "الذهب • الفضة • النقد • التجارة • مراجعة الديون"
                                else "सोना • चाँदी • नक़द • कारोबार • क़र्ज़ की समीक्षा",
                                color = SoftWhite, fontSize = 10.sp, lineHeight = 15.sp
                            )
                            Text(
                                if (language == "en") "Open guided worksheet  →"
                                else if (language == "ur") "تفصیلی حساب شروع کریں  ←"
                                else if (language == "ar") "ابدأ ورقة الحساب  ←"
                                else "मदवार हिसाब शुरू करें  →",
                                color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 7.dp)
                            )
                        }
                    }
                }
            }
        }
        item {
            Column(Modifier.padding(horizontal = 20.dp)) {
                CardSurface {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        DatePill(label = AppText.get(language, "gregorian"), value = HijriCalendar.gregorianShort(localDate, language), icon = "📅", modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(9.dp))
                        DatePill(label = AppText.get(language, "hijri"), value = HijriCalendar.format(today, language), icon = "☾", modifier = Modifier.weight(1.35f))
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(8.dp).background(Gold, CircleShape))
                        Spacer(Modifier.width(8.dp))
                        Text(AppText.get(language, "moon_note"), color = Muted, fontSize = 11.sp)
                    }
                }
            }
        }
        item {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth().animateContentSize().clickable { eventExpanded = !eventExpanded },
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = eventThemeColor(eventTheme)),
                    border = BorderStroke(1.dp, Line)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(AppText.get(language, "special_day"), color = PaleGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(if (eventExpanded) "⌃" else "⌄", color = Gold, fontSize = 18.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(event ?: AppText.get(language, "no_special_day"), color = SoftWhite, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        if (eventExpanded) {
                            Spacer(Modifier.height(7.dp))
                            if (feedEvents.isEmpty()) {
                                Text(AppText.get(language, "moon_note"), color = SoftWhite, fontSize = 12.sp, lineHeight = 18.sp)
                            } else {
                                feedEvents.forEach { item ->
                                    Text(item.title, color = PaleGold, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 5.dp))
                                    Text(item.description, color = SoftWhite, fontSize = 12.sp, lineHeight = 18.sp)
                                    TextButton(onClick = {
                                        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.sourceUrl))) }
                                    }, contentPadding = PaddingValues(0.dp)) {
                                        Text("📚 ${AppText.get(language, "event_reference")} — ${item.sourceName}", color = Gold, fontSize = 10.sp)
                                    }
                                }
                                if (feedEvents.any { it.moonSightingDependent }) {
                                    Text(AppText.get(language, "moon_note"), color = Muted, fontSize = 10.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 5.dp))
                                }
                            }
                            val eventUpdated = IslamicEventsFeed.lastUpdated(context)
                            if (eventUpdated > 0L) {
                                Text("${AppText.get(language, "events_updated")}: ${java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT).format(java.util.Date(eventUpdated))}", color = Muted, fontSize = 9.sp, modifier = Modifier.padding(top = 5.dp))
                            } else {
                                Text(AppText.get(language, "events_offline"), color = Muted, fontSize = 9.sp, modifier = Modifier.padding(top = 5.dp))
                            }
                        } else {
                            Text(if (language == "en") "Tap to read details" else if (language == "ur") "تفصیل کے لیے چھوئیں" else if (language == "ar") "اضغط لعرض التفاصيل" else "विस्तार जानने के लिए छुएँ", color = Muted, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
        item {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(AppText.get(language, "quick_tools"), color = PaleGold, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { onNavigate(AppPage.MORE) }) { Text(AppText.get(language, "view_all"), color = Gold) }
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FeatureCard("☾", AppText.get(language, "zakat"), AppText.get(language, "source"), Modifier.weight(1f), onReferenceClick = { onReference(ReferenceCatalog.item(0, language)) }) {
                        onNavigate(AppPage.ZAKAT)
                    }
                    FeatureCard("🌾", AppText.get(language, "fitrah"), AppText.get(language, "source"), Modifier.weight(1f), onReferenceClick = { onReference(ReferenceCatalog.item(2, language)) }) {
                        onNavigate(AppPage.FITRAH)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FeatureCard("⌛", AppText.get(language, "hawl"), null, Modifier.weight(1f)) { onNavigate(AppPage.HAWL) }
                    FeatureCard("📚", AppText.get(language, "asset_guide"), null, Modifier.weight(1f)) { onNavigate(AppPage.ASSET_GUIDE) }
                }
                Text(
                    if (language == "en") "Calendar and prayer times are available in the main navigation; other references and tools are grouped under More."
                    else if (language == "ur") "کیلنڈر اور نماز کے اوقات نیچے کے مین مینو میں ہیں؛ باقی مراجع اور سہولتیں مزید میں جمع ہیں۔"
                    else if (language == "ar") "التقويم ومواقيت الصلاة في شريط التنقل؛ بقية المراجع والأدوات ضمن المزيد."
                    else "कैलेंडर और नमाज़ के वक़्त नीचे मुख्य मेन्यू में हैं; बाकी हवाले और औज़ार ‘और’ में रखे हैं।",
                    color = Muted, fontSize = 10.sp, lineHeight = 14.sp, modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
        item {
            Column(Modifier.padding(horizontal = 20.dp)) {
                CardSurface {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PrivacyTip, contentDescription = null, tint = Gold)
                        Spacer(Modifier.width(8.dp))
                        Text(AppText.get(language, "disclaimer"), color = Muted, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

private fun eventThemeColor(theme: String): Color = when (theme) {
    "ramadan" -> Color(0xFF173A4B)
    "eid" -> Color(0xFF3E351D)
    "ashura" -> Color(0xFF2D263D)
    "mawlid", "green_gold" -> Color(0xFF123B2F)
    "hajj" -> Color(0xFF293A2A)
    "night_sky" -> Color(0xFF182947)
    "new_year" -> Color(0xFF183A37)
    else -> CardGreen
}

@Composable
private fun CardSurface(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardGreen),
        border = BorderStroke(1.dp, Line),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun DatePill(label: String, value: String, icon: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = DeepGreen, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Line)) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
            Text("$icon  $label", color = Muted, fontSize = 11.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, color = PaleGold, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 2)
        }
    }
}

@Composable
private fun FeatureCard(icon: String, title: String, referenceLabel: String?, modifier: Modifier = Modifier, onReferenceClick: (() -> Unit)? = null, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(112.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardGreen),
        border = BorderStroke(1.dp, Line)
    ) {
        Column(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text(icon, fontSize = 26.sp)
            Text(title, color = SoftWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            if (referenceLabel != null) Text("📚 $referenceLabel", color = Gold, fontSize = 10.sp, modifier = if (onReferenceClick != null) Modifier.clickable(onClick = onReferenceClick) else Modifier)
        }
    }
}

private data class JewelleryDraft(
    val name: String = "",
    val grossWeight: String = "",
    val nonMetalWeight: String = "",
    val carat: String = "22",
    val unit: String = JewelleryWeightUnit.GRAMS.name
)

private val jewelleryDraftSaver = listSaver<List<JewelleryDraft>, String>(
    save = { rows -> rows.flatMap { listOf(it.name, it.grossWeight, it.nonMetalWeight, it.carat, it.unit) } },
    restore = { saved ->
        val restored = saved.chunked(5).filter { it.size == 5 }.map { fields ->
            JewelleryDraft(fields[0], fields[1], fields[2], fields[3], fields[4])
        }
        restored.ifEmpty { listOf(JewelleryDraft()) }
    }
)

private data class AssetDraft(
    val title: String = "",
    val type: String = "CASH",
    val amount: String = "",
    val ownershipPercent: String = "100"
)

private val assetDraftSaver = listSaver<List<AssetDraft>, String>(
    save = { rows -> rows.flatMap { listOf(it.title, it.type, it.amount, it.ownershipPercent) } },
    restore = { saved ->
        val restored = saved.chunked(4).filter { it.size == 4 }.map { AssetDraft(it[0], it[1], it[2], it[3]) }
        restored.ifEmpty { listOf(AssetDraft()) }
    }
)

private data class DebtDraft(
    val title: String = "",
    val type: String = "DEMANDABLE",
    val amount: String = ""
)

private data class PersonalCalendarEvent(
    val id: Long,
    val title: String,
    val gregorianDate: String
)

private fun loadPersonalCalendarEvents(context: Context): List<PersonalCalendarEvent> {
    val raw = context.getSharedPreferences("noor_personal_calendar", Context.MODE_PRIVATE)
        .getString("events_v1", null) ?: return emptyList()
    return runCatching {
        val array = JSONArray(raw)
        (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            val id = item.optLong("id", -1L)
            val title = item.optString("title", "").trim()
            val date = item.optString("date", "")
            if (id <= 0L || title.isBlank() || runCatching { LocalDate.parse(date) }.isFailure) null
            else PersonalCalendarEvent(id, title.take(80), date)
        }.sortedBy { it.gregorianDate }
    }.getOrDefault(emptyList())
}

private fun savePersonalCalendarEvents(context: Context, events: List<PersonalCalendarEvent>) {
    val array = JSONArray().apply {
        events.take(500).forEach { event ->
            put(JSONObject().apply {
                put("id", event.id)
                put("title", event.title.take(80))
                put("date", event.gregorianDate)
            })
        }
    }
    context.getSharedPreferences("noor_personal_calendar", Context.MODE_PRIVATE)
        .edit().putString("events_v1", array.toString()).apply()
}

private val debtDraftSaver = listSaver<List<DebtDraft>, String>(
    save = { rows -> rows.flatMap { listOf(it.title, it.type, it.amount) } },
    restore = { saved ->
        val restored = saved.chunked(3).filter { it.size == 3 }.map { DebtDraft(it[0], it[1], it[2]) }
        restored.ifEmpty { listOf(DebtDraft()) }
    }
)

@Composable
private fun LedgerTypeSelector(
    label: String,
    selected: String,
    options: List<Pair<String, String>>,
    onSelected: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.first == selected }?.second ?: selected
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Line)
        ) {
            Column(Modifier.fillMaxWidth()) {
                Text(label, color = Muted, fontSize = 10.sp)
                Text(selectedLabel, color = PaleGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (key, text) ->
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = { expanded = false; onSelected(key) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ZakatScreen(language: String, onReference: (ReferenceItem) -> Unit) {
    val context = LocalContext.current
    val vaultInitiallyHadDraft = remember(context) { ZakatDraftVault.hasStoredDraft(context) }
    val loadedDraft = remember(context) { ZakatDraftVault.load(context) }
    val savedDraft = remember(context) { loadedDraft ?: JSONObject() }
    val initialJewellery = remember(savedDraft) {
        val rows = savedDraft.optJSONArray("jewellery") ?: JSONArray()
        (0 until rows.length()).mapNotNull { rows.optJSONObject(it) }.take(12).map { row ->
            JewelleryDraft(
                name = row.optString("name", ""),
                grossWeight = row.optString("grossWeight", ""),
                nonMetalWeight = row.optString("nonMetalWeight", ""),
                carat = row.optString("carat", "22"),
                unit = row.optString("unit", JewelleryWeightUnit.GRAMS.name)
            )
        }.ifEmpty { listOf(JewelleryDraft()) }
    }
    val initialAssets = remember(savedDraft) {
        val rows = savedDraft.optJSONArray("assets") ?: JSONArray()
        (0 until rows.length()).mapNotNull { rows.optJSONObject(it) }.take(30).map { row ->
            AssetDraft(row.optString("title", ""), row.optString("type", "CASH"), row.optString("amount", ""), row.optString("ownershipPercent", "100"))
        }.ifEmpty { listOf(AssetDraft()) }
    }
    val initialDebts = remember(savedDraft) {
        val rows = savedDraft.optJSONArray("debts") ?: JSONArray()
        (0 until rows.length()).mapNotNull { rows.optJSONObject(it) }.take(20).map { row ->
            DebtDraft(row.optString("title", ""), row.optString("type", "DEMANDABLE"), row.optString("amount", ""))
        }.ifEmpty { listOf(DebtDraft()) }
    }
    var jewelleryEntries by rememberSaveable(stateSaver = jewelleryDraftSaver) { mutableStateOf(initialJewellery) }
    var goldRate by rememberSaveable { mutableStateOf(savedDraft.optString("goldRate", "")) }
    var silverWeight by rememberSaveable { mutableStateOf(savedDraft.optString("silverWeight", "")) }
    var silverRate by rememberSaveable { mutableStateOf(savedDraft.optString("silverRate", "")) }
    var assetEntries by rememberSaveable(stateSaver = assetDraftSaver) { mutableStateOf(initialAssets) }
    var debtEntries by rememberSaveable(stateSaver = debtDraftSaver) { mutableStateOf(initialDebts) }
    var adultAndSane by rememberSaveable { mutableStateOf(savedDraft.optBoolean("adultAndSane", true)) }
    var hawlComplete by rememberSaveable { mutableStateOf(savedDraft.optBoolean("hawlComplete", false)) }
    var hawlBasis by rememberSaveable { mutableStateOf(savedDraft.optString("hawlBasis", ZakatHawlBasis.QAMRI_354.name)) }
    var silverNisabSource by rememberSaveable { mutableStateOf(savedDraft.optString("silverNisabSource", SilverNisabSource.FAIZAN_E_ZAKAT_APPROX.name)) }
    var secureSaveAvailable by remember { mutableStateOf(!vaultInitiallyHadDraft || loadedDraft != null) }
    var showClearDraftDialog by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<ZakatCalculationResult?>(null) }
    var inputError by remember { mutableStateOf<String?>(null) }
    var spotRateLoading by remember { mutableStateOf(false) }
    var spotRatesLoaded by rememberSaveable { mutableStateOf(false) }
    var spotRatesConfirmed by rememberSaveable { mutableStateOf(false) }
    var spotRateUpdatedAt by rememberSaveable { mutableStateOf("") }
    var spotRateError by remember { mutableStateOf<String?>(null) }
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val t: (String) -> String = { AppText.get(language, it) }

    LaunchedEffect(jewelleryEntries, goldRate, silverWeight, silverRate, assetEntries, debtEntries, adultAndSane, hawlComplete, hawlBasis, silverNisabSource, secureSaveAvailable) {
        if (!secureSaveAvailable) return@LaunchedEffect
        delay(250L)
        val draft = JSONObject().apply {
            put("schemaVersion", 1)
            put("goldRate", goldRate)
            put("silverWeight", silverWeight)
            put("silverRate", silverRate)
            put("adultAndSane", adultAndSane)
            put("hawlComplete", hawlComplete)
            put("hawlBasis", hawlBasis)
            put("silverNisabSource", silverNisabSource)
            put("jewellery", JSONArray().apply { jewelleryEntries.forEach { row ->
                put(JSONObject().apply {
                    put("name", row.name); put("grossWeight", row.grossWeight)
                    put("nonMetalWeight", row.nonMetalWeight); put("carat", row.carat); put("unit", row.unit)
                })
            } })
            put("assets", JSONArray().apply { assetEntries.forEach { row ->
                put(JSONObject().apply { put("title", row.title); put("type", row.type); put("amount", row.amount); put("ownershipPercent", row.ownershipPercent) })
            } })
            put("debts", JSONArray().apply { debtEntries.forEach { row ->
                put(JSONObject().apply { put("title", row.title); put("type", row.type); put("amount", row.amount) })
            } })
        }
        secureSaveAvailable = withContext(Dispatchers.IO) { ZakatDraftVault.save(context, draft.toString()) }
    }

    val jewelleryStarted = jewelleryEntries.map { row ->
        row.name.isNotBlank() || row.grossWeight.isNotBlank() || row.nonMetalWeight.isNotBlank() || row.carat != "22"
    }
    val jewelleryResults = remember(jewelleryEntries, goldRate) {
        val rate = parseNonNegativeDecimal(goldRate)
        jewelleryEntries.map { row ->
            val started = row.name.isNotBlank() || row.grossWeight.isNotBlank() || row.nonMetalWeight.isNotBlank() || row.carat != "22"
            val gross = parseNonNegativeDecimal(row.grossWeight)
            val nonMetalWeight = parseNonNegativeDecimal(row.nonMetalWeight)
            val caratValue = row.carat.toIntOrNull()
            val unit = runCatching { JewelleryWeightUnit.valueOf(row.unit) }.getOrNull()
            if (!started || gross == null || gross <= BigDecimal.ZERO || nonMetalWeight == null || rate == null || rate <= BigDecimal.ZERO || caratValue == null || unit == null) {
                null
            } else {
                runCatching {
                    GoldPurityCalculator.calculate(unit.toGrams(gross), nonMetalWeight, caratValue, rate)
                }.getOrNull()
            }
        }
    }
    val activeJewelleryCount = jewelleryStarted.count { it }
    val invalidJewellery = jewelleryStarted.indices.any { jewelleryStarted[it] && jewelleryResults[it] == null }
    val goldPureTotal = jewelleryResults.filterNotNull().fold(BigDecimal.ZERO) { total, row -> total.add(row.pureGoldGrams) }
    val goldPortfolioValue = goldPureTotal.multiply(parseNonNegativeDecimal(goldRate) ?: BigDecimal.ZERO)
        .setScale(2, RoundingMode.HALF_UP)
    val assetStarted = assetEntries.map { row -> row.title.isNotBlank() || row.amount.isNotBlank() || row.type != "CASH" || row.ownershipPercent != "100" }
    val assetEffectiveAmounts = remember(assetEntries) {
        assetEntries.map { row ->
            val started = row.title.isNotBlank() || row.amount.isNotBlank() || row.type != "CASH" || row.ownershipPercent != "100"
            val amount = parseNonNegativeDecimal(row.amount)
            val ownership = parseNonNegativeDecimal(row.ownershipPercent)
            if (!started || row.ownershipPercent.isBlank() || amount == null || amount <= BigDecimal.ZERO || ownership == null || ownership < BigDecimal.ZERO || ownership > BigDecimal("100")) null
            else runCatching { AssetOwnershipCalculator.ownedValue(amount, ownership) }.getOrNull()
        }
    }
    val invalidAssets = assetStarted.indices.any { assetStarted[it] && assetEffectiveAmounts[it] == null }
    val assetTotalsByType = buildMap {
        assetEntries.indices.forEach { index ->
            val effective = assetEffectiveAmounts[index] ?: return@forEach
            val type = assetEntries[index].type
            put(type, (get(type) ?: BigDecimal.ZERO).add(effective))
        }
    }
    fun assetTotal(vararg kinds: String): BigDecimal = kinds.fold(BigDecimal.ZERO) { total, kind -> total.add(assetTotalsByType[kind] ?: BigDecimal.ZERO) }
    val cashTotal = assetTotal("CASH")
    val bankTotal = assetTotal("BANK")
    val stockTotal = assetTotal("TRADE_STOCK")
    val rawMaterialTotal = assetTotal("RAW_MATERIAL")
    val tradePropertyTotal = assetTotal("TRADE_PROPERTY")
    val committeeTotal = assetTotal("SAVED_COMMITTEE")
    val receivablesTotal = assetTotal("RECEIVABLE_STRONG", "RECEIVABLE_MEDIUM", "RECEIVABLE_WEAK")
    val otherComplexAssetsTotal = assetTotal("OTHER_REVIEW")
    val exemptAssetTotal = assetTotal("PERSONAL_HOME", "PERSONAL_VEHICLE", "TOOLS_EQUIPMENT", "HOUSEHOLD_USE", "RENTAL_PROPERTY", "AGRICULTURAL_LAND")
    val bankInterestTotal = assetTotal("BANK_INTEREST")
    val debtStarted = debtEntries.map { row -> row.title.isNotBlank() || row.amount.isNotBlank() || row.type != "DEMANDABLE" }
    val debtAmounts = remember(debtEntries) {
        debtEntries.map { row ->
            val started = row.title.isNotBlank() || row.amount.isNotBlank() || row.type != "DEMANDABLE"
            val amount = parseNonNegativeDecimal(row.amount)
            if (!started || amount == null || amount <= BigDecimal.ZERO) null else amount
        }
    }
    val invalidDebts = debtStarted.indices.any { debtStarted[it] && debtAmounts[it] == null }
    val debtTotal = debtAmounts.filterNotNull().fold(BigDecimal.ZERO, BigDecimal::add)

    fun updateJewellery(index: Int, updated: JewelleryDraft) {
        jewelleryEntries = jewelleryEntries.toMutableList().also { it[index] = updated }
    }

    val number: (String) -> BigDecimal = { parseNonNegativeDecimal(it) ?: BigDecimal.ZERO }
    val selectedHawl = runCatching { ZakatHawlBasis.valueOf(hawlBasis) }.getOrDefault(ZakatHawlBasis.QAMRI_354)
    val selectedSilverMeasure = runCatching { SilverNisabSource.valueOf(silverNisabSource) }.getOrDefault(SilverNisabSource.FAIZAN_E_ZAKAT_APPROX)
    val assetKindOptions = listOf(
        "CASH" to t("asset_cash"), "BANK" to t("asset_bank"), "TRADE_STOCK" to t("asset_stock"),
        "RAW_MATERIAL" to t("asset_raw"), "TRADE_PROPERTY" to t("asset_trade_property"),
        "SAVED_COMMITTEE" to t("asset_committee"), "RECEIVABLE_STRONG" to t("asset_receivable_strong"),
        "RECEIVABLE_MEDIUM" to t("asset_receivable_medium"), "RECEIVABLE_WEAK" to t("asset_receivable_weak"),
        "PERSONAL_HOME" to t("asset_personal_home"), "PERSONAL_VEHICLE" to t("asset_personal_vehicle"),
        "TOOLS_EQUIPMENT" to t("asset_tools"), "HOUSEHOLD_USE" to t("asset_household"),
        "RENTAL_PROPERTY" to t("asset_rental_property"), "AGRICULTURAL_LAND" to t("asset_agricultural_land"),
        "BANK_INTEREST" to t("asset_bank_interest"), "OTHER_REVIEW" to t("asset_other_review")
    )
    val debtKindOptions = listOf(
        "DEMANDABLE" to t("debt_demandable"), "INSTALLMENT" to t("debt_installment"),
        "COMMITTEE_DUE" to t("debt_committee"), "MAHR_OTHER" to t("debt_mahr"),
        "OTHER" to t("debt_other")
    )

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(t("zakat_title"), t("zakat_subtitle"))
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { showClearDraftDialog = true }) {
                Text(
                    if (language == "en") "Clear saved draft"
                    else if (language == "ur") "محفوظ مسودہ صاف کریں"
                    else if (language == "ar") "مسح المسودة المحفوظة"
                    else "सेव किया ड्राफ्ट मिटाएँ",
                    color = Muted, fontSize = 10.sp
                )
            }
        }
        if (!secureSaveAvailable) {
            Text(
                if (vaultInitiallyHadDraft && loadedDraft == null) {
                    if (language == "en") "An encrypted draft could not be opened. The stored data is preserved and will not be overwritten. Do not clear app storage until you have checked your backup."
                    else if (language == "ur") "محفوظ مسودہ نہیں کھل سکا؛ اصل ڈیٹا محفوظ ہے اور اس پر نیا ڈیٹا نہیں لکھا جائے گا۔ بیک اَپ دیکھے بغیر ایپ اسٹوریج صاف نہ کریں۔"
                    else if (language == "ar") "تعذر فتح المسود المشفر؛ البيانات الأصلية محفوظَة ولن تُستبدل. لا تمسح بيانات التطبيق قبل التحقق من النسخة الاحتياطية."
                    else "एन्क्रिप्टेड ड्राफ्ट नहीं खुला; पुराना डेटा सुरक्षित है और overwrite नहीं होगा। बैकअप जाँचे बिना ऐप स्टोरेज साफ न करें।"
                } else if (language == "en") "Secure on-device saving is unavailable. Your draft may not persist after closing the app."
                else if (language == "ur") "محفوظ مقامی سیو دستیاب نہیں؛ ایپ بند ہونے پر مسودہ محفوظ نہ رہ سکے۔"
                else if (language == "ar") "الحفظ المحلي المشفر غير متاح؛ قد لا يبقى المسود بعد إغلاق التطبيق."
                else "सुरक्षित लोकल सेव उपलब्ध नहीं है; ऐप बंद करने पर आपका ड्राफ्ट सुरक्षित न रह पाए।",
                color = Color(0xFFFFD59A), fontSize = 10.sp, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }
        LazyColumn(
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 26.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                CardSurface {
                    Text(t("zakat_guard"), color = PaleGold, fontSize = 12.sp, lineHeight = 18.sp)
                    Spacer(Modifier.height(7.dp))
                    Text(t("complex_notice"), color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
                    TextButton(onClick = { onReference(ReferenceCatalog.item(0, language)) }) {
                        Text("📚 ${t("source")}", color = Gold)
                    }
                }
            }
            item {
                CardSurface {
                    Text(t("owner_condition"), color = PaleGold, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = adultAndSane, onCheckedChange = { adultAndSane = it; result = null })
                        Text(t("adult_sane"), color = SoftWhite, fontSize = 12.sp)
                    }
                    Divider(color = Line, modifier = Modifier.padding(vertical = 7.dp))
                    Text(t("hawl_condition"), color = PaleGold, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = hawlComplete, onCheckedChange = { hawlComplete = it; result = null })
                        Text(t("hawl_complete"), color = SoftWhite, fontSize = 12.sp)
                    }
                    Text(t("hawl_basis"), color = Muted, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChoice(t("qamri_354"), hawlBasis == ZakatHawlBasis.QAMRI_354.name) {
                            hawlBasis = ZakatHawlBasis.QAMRI_354.name; result = null
                        }
                        FilterChoice(t("shamsi_365"), hawlBasis == ZakatHawlBasis.SHAMSI_365_DOCUMENT_VALUE.name) {
                            hawlBasis = ZakatHawlBasis.SHAMSI_365_DOCUMENT_VALUE.name; result = null
                        }
                    }
                }
            }
            item {
                CardSurface {
                    Text(t("market_rates"), color = PaleGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(t("market_rates_note"), color = Muted, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 5.dp, bottom = 8.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                spotRateLoading = true
                                spotRateError = null
                                val fetched = withContext(Dispatchers.IO) { runCatching { MarketRatesRepository.fetchIndiaSpotEstimate() } }
                                fetched.onSuccess { rates ->
                                    goldRate = rates.gold24kInrPerGram.stripTrailingZeros().toPlainString()
                                    silverRate = rates.silverInrPerGram.stripTrailingZeros().toPlainString()
                                    spotRatesLoaded = true
                                    spotRatesConfirmed = false
                                    spotRateUpdatedAt = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.getDefault()).withZone(ZoneId.systemDefault()).format(rates.updatedAt)
                                    result = null
                                }.onFailure { spotRateError = it.message ?: t("market_rates_error") }
                                spotRateLoading = false
                            }
                        },
                        enabled = !spotRateLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = CardGreen2, contentColor = PaleGold),
                        shape = RoundedCornerShape(13.dp), modifier = Modifier.fillMaxWidth()
                    ) { Text(if (spotRateLoading) t("market_rates_loading") else t("market_rates_update"), fontWeight = FontWeight.Bold) }
                    if (spotRateUpdatedAt.isNotBlank()) {
                        Text("${t("market_updated")}: $spotRateUpdatedAt", color = Gold, fontSize = 10.sp, modifier = Modifier.padding(top = 5.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = spotRatesConfirmed, onCheckedChange = { spotRatesConfirmed = it; result = null })
                            Text(t("market_rates_confirm"), color = SoftWhite, fontSize = 11.sp, lineHeight = 15.sp)
                        }
                    }
                    spotRateError?.let { Text("${t("market_rates_error")}: $it", color = Color(0xFFFFB7A8), fontSize = 11.sp) }
                }
            }
            item {
                CardSurface {
                    Text(t("gold_studio"), color = PaleGold, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(t("multi_jewellery_note"), color = Muted, fontSize = 11.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))
                    jewelleryEntries.forEachIndexed { index, row ->
                        Surface(
                            color = DeepGreen,
                            shape = RoundedCornerShape(15.dp),
                            border = BorderStroke(1.dp, Line),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                        ) {
                            Column(Modifier.padding(11.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${t("jewellery_item")} ${index + 1}", color = PaleGold, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    if (jewelleryEntries.size > 1) {
                                        TextButton(onClick = {
                                            jewelleryEntries = jewelleryEntries.filterIndexed { rowIndex, _ -> rowIndex != index }
                                            result = null
                                        }) { Text(t("remove_item"), color = Color(0xFFFFB7A8), fontSize = 11.sp) }
                                    }
                                }
                                OutlinedTextField(
                                    value = row.name,
                                    onValueChange = { updateJewellery(index, row.copy(name = it)); result = null },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    label = { Text(t("jewellery_name"), fontSize = 12.sp) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = SoftWhite, unfocusedTextColor = SoftWhite, focusedBorderColor = Gold, unfocusedBorderColor = Line, focusedLabelColor = Gold, unfocusedLabelColor = Muted, cursorColor = Gold),
                                    shape = RoundedCornerShape(13.dp)
                                )
                                Text(t("weight_unit"), color = Muted, fontSize = 11.sp)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    FilterChoice(t("grams"), row.unit == JewelleryWeightUnit.GRAMS.name) { updateJewellery(index, row.copy(unit = JewelleryWeightUnit.GRAMS.name)); result = null }
                                    FilterChoice(t("market_tola"), row.unit == JewelleryWeightUnit.MARKET_TOLA.name) { updateJewellery(index, row.copy(unit = JewelleryWeightUnit.MARKET_TOLA.name)); result = null }
                                    FilterChoice(t("shari_tola"), row.unit == JewelleryWeightUnit.SHARI_TOLA.name) { updateJewellery(index, row.copy(unit = JewelleryWeightUnit.SHARI_TOLA.name)); result = null }
                                }
                                NumberField(t("gross_weight"), row.grossWeight, { updateJewellery(index, row.copy(grossWeight = it)); result = null })
                                NumberField(t("stones_weight"), row.nonMetalWeight, { updateJewellery(index, row.copy(nonMetalWeight = it)); result = null })
                                NumberField(t("carat"), row.carat, { updateJewellery(index, row.copy(carat = it)); result = null }, integer = true)
                                jewelleryResults[index]?.let { line ->
                                    MetricRow(t("pure_gold"), "${line.pureGoldGrams.setScale(3, RoundingMode.HALF_UP)} g")
                                    MetricRow(t("estimated_gold_value"), "₹${money(line.estimatedValue)}")
                                }
                            }
                        }
                    }
                    TextButton(
                        onClick = { if (jewelleryEntries.size < 12) jewelleryEntries = jewelleryEntries + JewelleryDraft(); result = null },
                        enabled = jewelleryEntries.size < 12
                    ) { Text("＋ ${t("add_jewellery")}", color = Gold) }
                    NumberField(t("gold_rate"), goldRate, { goldRate = it; result = null })
                    if (activeJewelleryCount > 0 && !invalidJewellery) {
                        Divider(color = Line, modifier = Modifier.padding(vertical = 8.dp))
                        MetricRow(t("combined_pure_gold"), "${goldPureTotal.setScale(3, RoundingMode.HALF_UP)} g")
                        MetricRow(t("estimated_gold_value"), "₹${money(goldPortfolioValue)}")
                    } else if (invalidJewellery) {
                        Text(t("gold_input_error"), color = Color(0xFFFFB7A8), fontSize = 11.sp, lineHeight = 15.sp)
                    }
                    TextButton(onClick = { onReference(ReferenceCatalog.item(1, language)) }) {
                        Text("📚 ${t("reference_label")}", color = Gold)
                    }
                }
            }
            item {
                CardSurface {
                    Text(t("silver_studio"), color = PaleGold, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    NumberField(t("silver_weight"), silverWeight, { silverWeight = it; result = null })
                    NumberField(t("silver_rate"), silverRate, { silverRate = it; result = null })
                    Text(t("silver_nisab_source"), color = Muted, fontSize = 11.sp)
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        FilterChoice(t("project_measure"), silverNisabSource == SilverNisabSource.PROJECT_RESEARCH.name) {
                            silverNisabSource = SilverNisabSource.PROJECT_RESEARCH.name; result = null
                        }
                        FilterChoice(t("published_measure"), silverNisabSource == SilverNisabSource.FAIZAN_E_ZAKAT_APPROX.name) {
                            silverNisabSource = SilverNisabSource.FAIZAN_E_ZAKAT_APPROX.name; result = null
                        }
                    }
                    TextButton(onClick = { onReference(ReferenceCatalog.item(0, language)) }) {
                        Text("📚 ${t("reference_label")}", color = Gold)
                    }
                }
            }
            item {
                CardSurface {
                    Text(t("asset_inputs"), color = PaleGold, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(t("asset_ledger_note"), color = Muted, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 5.dp, bottom = 8.dp))
                    assetEntries.forEachIndexed { index, row ->
                        Surface(color = DeepGreen, shape = RoundedCornerShape(15.dp), border = BorderStroke(1.dp, Line), modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                            Column(Modifier.padding(11.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${t("asset_category")} ${index + 1}", color = PaleGold, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    if (assetEntries.size > 1) TextButton(onClick = {
                                        assetEntries = assetEntries.filterIndexed { rowIndex, _ -> rowIndex != index }; result = null
                                    }) { Text(t("remove_item"), color = Color(0xFFFFB7A8), fontSize = 11.sp) }
                                }
                                OutlinedTextField(
                                    value = row.title,
                                    onValueChange = { value -> assetEntries = assetEntries.toMutableList().also { it[index] = row.copy(title = value) }; result = null },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), label = { Text(t("asset_title"), fontSize = 12.sp) }, singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = SoftWhite, unfocusedTextColor = SoftWhite, focusedBorderColor = Gold, unfocusedBorderColor = Line, focusedLabelColor = Gold, unfocusedLabelColor = Muted, cursorColor = Gold), shape = RoundedCornerShape(13.dp)
                                )
                                LedgerTypeSelector(t("asset_category"), row.type, assetKindOptions) { chosen -> assetEntries = assetEntries.toMutableList().also { it[index] = row.copy(type = chosen) }; result = null }
                                NumberField(t("asset_amount"), row.amount, { value -> assetEntries = assetEntries.toMutableList().also { it[index] = row.copy(amount = value) }; result = null })
                                NumberField(t("ownership_share"), row.ownershipPercent, { value -> assetEntries = assetEntries.toMutableList().also { it[index] = row.copy(ownershipPercent = value) }; result = null })
                                assetEffectiveAmounts[index]?.let { MetricRow(t("effective_share_value"), "₹${money(it)}") }
                            }
                        }
                    }
                    TextButton(onClick = { if (assetEntries.size < 30) assetEntries = assetEntries + AssetDraft(); result = null }, enabled = assetEntries.size < 30) {
                        Text("＋ ${t("add_asset")}", color = Gold)
                    }
                    Text(t("asset_guide_caution"), color = Muted, fontSize = 10.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 4.dp))
                    TextButton(onClick = { onReference(ReferenceCatalog.item(4, language)) }) {
                        Text("📚 ${t("reference_label")}", color = Gold)
                    }
                }
            }
            item {
                CardSurface {
                    Text(t("debt_ledger_title"), color = PaleGold, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(t("debt_ledger_note"), color = Muted, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 5.dp, bottom = 8.dp))
                    debtEntries.forEachIndexed { index, row ->
                        Surface(color = DeepGreen, shape = RoundedCornerShape(15.dp), border = BorderStroke(1.dp, Line), modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                            Column(Modifier.padding(11.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${t("debt_ledger_title")} ${index + 1}", color = PaleGold, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    if (debtEntries.size > 1) TextButton(onClick = {
                                        debtEntries = debtEntries.filterIndexed { rowIndex, _ -> rowIndex != index }; result = null
                                    }) { Text(t("remove_item"), color = Color(0xFFFFB7A8), fontSize = 11.sp) }
                                }
                                OutlinedTextField(
                                    value = row.title,
                                    onValueChange = { value -> debtEntries = debtEntries.toMutableList().also { it[index] = row.copy(title = value) }; result = null },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), label = { Text(t("debt_title"), fontSize = 12.sp) }, singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = SoftWhite, unfocusedTextColor = SoftWhite, focusedBorderColor = Gold, unfocusedBorderColor = Line, focusedLabelColor = Gold, unfocusedLabelColor = Muted, cursorColor = Gold), shape = RoundedCornerShape(13.dp)
                                )
                                LedgerTypeSelector(t("debt_type"), row.type, debtKindOptions) { chosen -> debtEntries = debtEntries.toMutableList().also { it[index] = row.copy(type = chosen) }; result = null }
                                NumberField(t("debt_amount"), row.amount, { value -> debtEntries = debtEntries.toMutableList().also { it[index] = row.copy(amount = value) }; result = null })
                            }
                        }
                    }
                    TextButton(onClick = { if (debtEntries.size < 20) debtEntries = debtEntries + DebtDraft(); result = null }, enabled = debtEntries.size < 20) {
                        Text("＋ ${t("add_debt")}", color = Gold)
                    }
                    TextButton(onClick = { onReference(ReferenceCatalog.item(4, language)) }) {
                        Text("📚 ${t("reference_label")}", color = Gold)
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        val typedNumbers = listOf(goldRate, silverWeight, silverRate) +
                            jewelleryEntries.flatMap { listOf(it.grossWeight, it.nonMetalWeight) } +
                            assetEntries.flatMap { listOf(it.amount, it.ownershipPercent) } +
                            debtEntries.map { it.amount }
                        val hasInvalidNumber = typedNumbers.any { it.isNotBlank() && parseNonNegativeDecimal(it) == null }
                        val invalidGold = invalidJewellery
                        val invalidSilver = silverWeight.isNotBlank() && (number(silverWeight) <= BigDecimal.ZERO || number(silverRate) <= BigDecimal.ZERO)
                        when {
                            hasInvalidNumber || invalidAssets || invalidDebts -> inputError = t("enter_valid")
                            invalidGold -> inputError = t("gold_input_error")
                            invalidSilver -> inputError = t("silver_input_error")
                            spotRatesLoaded && !spotRatesConfirmed -> inputError = t("market_rates_confirm")
                            else -> {
                                inputError = null
                                val goldPure = if (invalidJewellery) BigDecimal.ZERO else goldPureTotal
                                val goldPerGram = number(goldRate)
                                val silverPure = number(silverWeight)
                                val silverPerGram = number(silverRate)
                                result = runCatching {
                            FiqhZakatEngine.calculate(
                                ZakatCalculationInput(
                                    adultAndSane = adultAndSane,
                                    hawlComplete = hawlComplete,
                                    hawlBasis = selectedHawl,
                                    silverNisabSource = selectedSilverMeasure,
                                    goldPureGrams = goldPure,
                                    goldRatePerGram = goldPerGram,
                                    silverPureGrams = silverPure,
                                    silverRatePerGram = silverPerGram,
                                    cash = cashTotal,
                                    bankBalance = bankTotal,
                                    savedCommittee = committeeTotal,
                                    tradeStock = stockTotal,
                                    rawMaterial = rawMaterialTotal,
                                    tradeProperty = tradePropertyTotal,
                                    debtForReview = debtTotal,
                                    receivablesForReview = receivablesTotal,
                                    hasOtherComplexAssets = otherComplexAssetsTotal > BigDecimal.ZERO
                                )
                            )
                                }.getOrNull()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink),
                    shape = RoundedCornerShape(15.dp)
                ) { Text(t("run_calculation"), fontWeight = FontWeight.Bold) }
                inputError?.let { Text(it, color = Color(0xFFFFB7A8), fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp)) }
            }
            result?.let { calculated ->
                item {
                    CardSurface {
                        val statusLabel = when (calculated.status) {
                            ZakatDecisionStatus.INDICATIVE_ESTIMATE -> t("indicative_estimate")
                            ZakatDecisionStatus.MUFTI_REVIEW_REQUIRED -> t("review_required")
                            ZakatDecisionStatus.NEED_MARKET_RATES -> t("need_rates")
                            ZakatDecisionStatus.NOT_DUE -> t("not_due")
                        }
                        Text(statusLabel, color = if (calculated.status == ZakatDecisionStatus.INDICATIVE_ESTIMATE) PaleGold else Gold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(7.dp))
                        if (goldPureTotal > BigDecimal.ZERO && !invalidJewellery) MetricRow(t("gold_studio"), "₹${money(goldPortfolioValue)}")
                        val silverAssetValue = number(silverWeight).multiply(number(silverRate))
                        if (silverAssetValue > BigDecimal.ZERO) MetricRow(t("silver_studio"), "₹${money(silverAssetValue)}")
                        if (cashTotal > BigDecimal.ZERO) MetricRow(t("asset_cash"), "₹${money(cashTotal)}")
                        if (bankTotal > BigDecimal.ZERO) MetricRow(t("asset_bank"), "₹${money(bankTotal)}")
                        if (stockTotal > BigDecimal.ZERO) MetricRow(t("asset_stock"), "₹${money(stockTotal)}")
                        if (rawMaterialTotal > BigDecimal.ZERO) MetricRow(t("asset_raw"), "₹${money(rawMaterialTotal)}")
                        if (tradePropertyTotal > BigDecimal.ZERO) MetricRow(t("asset_trade_property"), "₹${money(tradePropertyTotal)}")
                        if (committeeTotal > BigDecimal.ZERO) MetricRow(t("asset_committee"), "₹${money(committeeTotal)}")
                        if (receivablesTotal > BigDecimal.ZERO) MetricRow(t("recorded_receivables"), "₹${money(receivablesTotal)}")
                        if (otherComplexAssetsTotal > BigDecimal.ZERO) MetricRow(t("asset_other_review"), "₹${money(otherComplexAssetsTotal)}")
                        if (debtTotal > BigDecimal.ZERO) MetricRow(t("recorded_debts"), "₹${money(debtTotal)}")
                        if (exemptAssetTotal > BigDecimal.ZERO) MetricRow(t("exempt_assets"), "₹${money(exemptAssetTotal)}")
                        if (bankInterestTotal > BigDecimal.ZERO) MetricRow(t("asset_bank_interest"), "₹${money(bankInterestTotal)}")
                        if (assetEntries.indices.any { assetStarted[it] }) {
                            Spacer(Modifier.height(5.dp))
                            Text(t("asset_line_breakdown"), color = PaleGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            assetEntries.indices.filter { assetStarted[it] }.forEach { index ->
                                val row = assetEntries[index]
                                val categoryLabel = assetKindOptions.firstOrNull { it.first == row.type }?.second ?: row.type
                                val lineLabel = row.title.trim().takeIf { it.isNotBlank() }?.let { "$it · $categoryLabel" } ?: categoryLabel
                                assetEffectiveAmounts[index]?.let { MetricRow(lineLabel, "₹${money(it)}") }
                            }
                        }
                        if (debtEntries.indices.any { debtStarted[it] }) {
                            Spacer(Modifier.height(5.dp))
                            Text(t("debt_line_breakdown"), color = PaleGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            debtEntries.indices.filter { debtStarted[it] }.forEach { index ->
                                val row = debtEntries[index]
                                val debtLabel = debtKindOptions.firstOrNull { it.first == row.type }?.second ?: row.type
                                val lineLabel = row.title.trim().takeIf { it.isNotBlank() }?.let { "$it · $debtLabel" } ?: debtLabel
                                debtAmounts[index]?.let { MetricRow(lineLabel, "₹${money(it)}") }
                            }
                        }
                        MetricRow(t("gross_assets"), "₹${money(calculated.grossAssetValue)}")
                        calculated.nisabValue?.let { MetricRow(t("nisab"), "₹${money(it)}") }
                        calculated.zakatableBase?.let { MetricRow(t("eligible_base"), "₹${money(it)}") }
                        calculated.zakatAmount?.let {
                            Divider(color = Line, modifier = Modifier.padding(vertical = 8.dp))
                            Text(t("zakat_due_estimate"), color = Muted, fontSize = 12.sp)
                            Text("₹${money(it)}", color = PaleGold, fontWeight = FontWeight.ExtraBold, fontSize = 27.sp)
                            Text("${calculated.zakatRate.multiply(BigDecimal("100")).stripTrailingZeros().toPlainString()}%", color = Muted, fontSize = 11.sp)
                            if ((calculated.category == "GOLD_ONLY" || calculated.category == "SILVER_ONLY") && calculated.grossAssetValue > (calculated.zakatableBase ?: BigDecimal.ZERO)) {
                                val wholeValueAmount = calculated.grossAssetValue.multiply(calculated.zakatRate, java.math.MathContext.DECIMAL128)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    if (language == "en") "Cash-payment option: full value at the selected year rate (may include nafl excess)"
                                    else if (language == "ur") "نقد ادائیگی: منتخب سال کی شرح سے پوری قیمت (زائد حصہ نفلی ہو سکتا ہے)"
                                    else if (language == "ar") "الدفع نقداً: كامل القيمة وفق معدل السنة المختار (قد يتضمن زيادة نافلة)"
                                    else "नक़द भुगतान: चुने हुए साल की दर से पूरी क़ीमत (अतिरिक्त हिस्सा नफ़्ल हो सकता है)",
                                    color = SoftWhite, fontSize = 10.sp, lineHeight = 14.sp
                                )
                                Text("₹${money(wholeValueAmount)}", color = Gold, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    if (language == "en") "Faizan-e-Zakat p.15 describes 2.5% of full year-end value for cash payment. Its metal-payment method is separate; a solar-year setting changes the selected rate. Confirm your choice with a Mufti."
                                    else if (language == "ur") "فیضانِ زکوٰۃ صفحہ 15 میں نقد ادائیگی کے لیے سال کے اختتام کی پوری قیمت کا 2.5٪ ہے؛ سونے/چاندی کی صورت میں ادائیگی کا الگ طریقہ ہے۔ شمسی سال میں شرح بدلتی ہے؛ مفتی صاحب سے تصدیق کریں۔"
                                    else if (language == "ar") "تذكر فيضان الزكاة ص. 15 نسبة 2.5٪ من كامل قيمة نهاية الحول عند الدفع نقداً، وللدفع بالذهب/الفضة طريقة أخرى. يتغير المعدل عند اختيار السنة الشمسية؛ أكّد مع المفتي."
                                    else "फ़ैज़ान-ए-ज़कात पेज 15 में नक़द भुगतान के लिए हौल की पूरी क़ीमत का 2.5% है; सोना/चाँदी देकर चुकाने का अलग तरीक़ा है। सौर वर्ष में दर बदलती है; मुफ़्ती से पुष्टि करें।",
                                    color = Muted, fontSize = 9.sp, lineHeight = 13.sp
                                )
                            }
                        }
                        Spacer(Modifier.height(7.dp))
                        Text(calculated.explanation, color = SoftWhite, fontSize = 12.sp, lineHeight = 17.sp)
                        calculated.reviewReasons.forEach { reason ->
                            Text("• $reason", color = Color(0xFFFFD59A), fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 5.dp))
                        }
                        calculated.nisabBasis.takeIf { it.isNotBlank() }?.let {
                            Text(it, color = Muted, fontSize = 10.sp, lineHeight = 14.sp, modifier = Modifier.padding(top = 6.dp))
                        }
                        val referenceIndex = when {
                            calculated.category == "GOLD_ONLY" -> 1 // Khums examples for gold-only wealth.
                            calculated.category == "COMPLEX_CASE" && committeeTotal > BigDecimal.ZERO -> 5 // Committee / BC.
                            calculated.category == "COMPLEX_CASE" -> 4 // Debt and receivables.
                            else -> 0 // Nisab and standard Zakat basis.
                        }
                        TextButton(onClick = { onReference(ReferenceCatalog.item(referenceIndex, language)) }) {
                            Text("📚 ${t("reference_label")}", color = Gold)
                        }
                        calculated.sourceNotes.forEach { note -> Text(note, color = Muted, fontSize = 10.sp, lineHeight = 14.sp) }
                    }
                }
            }
            item {
                ReferenceMiniCard(t("source_note"), language, onClick = { onReference(ReferenceCatalog.item(0, language)) })
            }
        }
        if (showClearDraftDialog) {
            AlertDialog(
                onDismissRequest = { showClearDraftDialog = false },
                containerColor = CardGreen,
                titleContentColor = PaleGold,
                textContentColor = SoftWhite,
                title = { Text(if (language == "en") "Clear the Zakat worksheet?" else if (language == "ur") "زکوٰۃ کا مسودہ صاف کریں؟" else if (language == "ar") "مسح ورقة الزكاة؟" else "ज़कात वर्कशीट मिटाएँ?") },
                text = { Text(if (language == "en") "This removes the locally saved encrypted draft and resets the current entries on this device. It does not change your Salah log or calendar settings." else if (language == "ur") "اس سے اس ڈیوائس پر محفوظ زکوٰۃ مسودہ مٹ جائے گا اور موجودہ اندراجات ری سیٹ ہوں گے۔ نماز کا ریکارڈ یا کیلنڈر کی ترتیب نہیں بدلے گی۔" else if (language == "ar") "سيؤدي ذلك إلى حذف مسودة الزكاة المشفرة وإعادة ضبط الإدخالات الحالية على هذا الجهاز، دون تغيير سجل الصلاة أو إعدادات التقويم." else "इससे इसी फोन पर सेव एन्क्रिप्टेड ज़कात ड्राफ्ट और मौजूदा entries मिटेंगी। नमाज़ रिकॉर्ड और कैलेंडर सेटिंग नहीं बदलेंगी।") },
                confirmButton = {
                    TextButton(onClick = {
                        showClearDraftDialog = false
                        val cleared = ZakatDraftVault.clear(context)
                        secureSaveAvailable = cleared
                        if (cleared) {
                            jewelleryEntries = listOf(JewelleryDraft())
                            goldRate = ""; silverWeight = ""; silverRate = ""
                            assetEntries = listOf(AssetDraft()); debtEntries = listOf(DebtDraft())
                            adultAndSane = true; hawlComplete = false
                            hawlBasis = ZakatHawlBasis.QAMRI_354.name
                            silverNisabSource = SilverNisabSource.FAIZAN_E_ZAKAT_APPROX.name
                            result = null; inputError = null
                            spotRatesLoaded = false; spotRatesConfirmed = false; spotRateUpdatedAt = ""
                        }
                    }) { Text(if (language == "en") "Clear" else if (language == "ur") "صاف کریں" else if (language == "ar") "مسح" else "मिटाएँ", color = Gold) }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDraftDialog = false }) { Text(if (language == "en") "Cancel" else if (language == "ur") "منسوخ" else if (language == "ar") "إلغاء" else "रद्द करें", color = Muted) }
                }
            )
        }
    }
}

@Composable
private fun FilterChoice(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = if (selected) CardGreen2 else DeepGreen,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, if (selected) Gold else Line)
    ) {
        Text(label, color = if (selected) PaleGold else Muted, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp))
    }
}

@SuppressLint("MissingPermission")
private suspend fun findCurrentLocation(context: Context): Location? {
    val client = LocationServices.getFusedLocationProviderClient(context)
    return client.getCurrentLocation(
        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
        CancellationTokenSource().token
    ).await()
}

private fun hasLocationPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

@Composable
private fun PrayerScreen(language: String) {
    val context = LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val t: (String) -> String = { AppText.get(language, it) }
    var method by rememberSaveable { mutableIntStateOf(PrayerTimeBackgroundSync.savedMethod(context)) }
    var latitude by rememberSaveable { mutableStateOf(PrayerTimeBackgroundSync.savedLocation(context)?.latitude?.let { String.format(Locale.US, "%.5f", it) } ?: "") }
    var longitude by rememberSaveable { mutableStateOf(PrayerTimeBackgroundSync.savedLocation(context)?.longitude?.let { String.format(Locale.US, "%.5f", it) } ?: "") }
    var times by remember { mutableStateOf<PrayerTimes?>(null) }
    var countdownRefresh by remember { mutableIntStateOf(0) }
    LaunchedEffect(times) {
        if (times != null) while (true) {
            delay(30_000L)
            countdownRefresh += 1
        }
    }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val salahDateKey = LocalDate.now().toString()
    val salahPrefs = remember { context.getSharedPreferences("noor_salah_log", Context.MODE_PRIVATE) }
    var fajrDone by rememberSaveable(salahDateKey) { mutableStateOf(salahPrefs.getBoolean("${salahDateKey}_fajr", false)) }
    var dhuhrDone by rememberSaveable(salahDateKey) { mutableStateOf(salahPrefs.getBoolean("${salahDateKey}_dhuhr", false)) }
    var asrDone by rememberSaveable(salahDateKey) { mutableStateOf(salahPrefs.getBoolean("${salahDateKey}_asr", false)) }
    var maghribDone by rememberSaveable(salahDateKey) { mutableStateOf(salahPrefs.getBoolean("${salahDateKey}_maghrib", false)) }
    var ishaDone by rememberSaveable(salahDateKey) { mutableStateOf(salahPrefs.getBoolean("${salahDateKey}_isha", false)) }
    val salahDoneCount = listOf(fajrDone, dhuhrDone, asrDone, maghribDone, ishaDone).count { it }

    // Re-open the last user-approved coordinates and selected method without requesting GPS again.
    LaunchedEffect(method, language) {
        val saved = PrayerTimeBackgroundSync.savedLocation(context)
        if (saved != null) {
            latitude = String.format(Locale.US, "%.5f", saved.latitude)
            longitude = String.format(Locale.US, "%.5f", saved.longitude)
            loading = true
            error = null
            try {
                val fetched = withContext(Dispatchers.IO) {
                    PrayerTimesRepository.fetch(saved.latitude, saved.longitude, method = method, school = 1)
                }
                times = fetched
                PrayerTimeBackgroundSync.saveLocation(context, saved.latitude, saved.longitude, method, language)
            } catch (e: Exception) {
                error = e.message ?: t("prayer_error")
            } finally {
                loading = false
            }
        }
    }

    val loadCurrent: () -> Unit = {
        scope.launch {
            loading = true
            error = null
            try {
                val location = findCurrentLocation(context)
                    ?: throw IllegalStateException("No recent location fix. Turn on Location and try again.")
                latitude = String.format(Locale.US, "%.5f", location.latitude)
                longitude = String.format(Locale.US, "%.5f", location.longitude)
                val fetched = withContext(Dispatchers.IO) {
                    PrayerTimesRepository.fetch(location.latitude, location.longitude, method = method, school = 1)
                }
                times = fetched
                PrayerTimeBackgroundSync.saveLocation(context, location.latitude, location.longitude, method, language)
                val now = LocalTime.now(ZoneId.of(fetched.timeZone))
                val nextPrayer = listOf(
                    t("fajr") to fetched.fajr, t("dhuhr") to fetched.dhuhr,
                    t("asr") to fetched.asr, t("maghrib") to fetched.maghrib, t("isha") to fetched.isha
                ).mapNotNull { (name, value) -> runCatching { name to LocalTime.parse(value) }.getOrNull() }
                    .firstOrNull { it.second.isAfter(now) }
                val prayerLine = if (nextPrayer != null) "${t("next_prayer")}: ${nextPrayer.first} ${nextPrayer.second}" else "${t("isha")}: ${fetched.isha}"
                context.getSharedPreferences("noor_widget_cache", Context.MODE_PRIVATE)
                    .edit().putString("next_prayer_line", prayerLine).apply()
                HijriWidgetProvider.refreshAll(context)
            } catch (e: Exception) {
                error = e.message ?: t("prayer_error")
            } finally {
                loading = false
            }
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants[Manifest.permission.ACCESS_FINE_LOCATION] == true || grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            loadCurrent()
        } else {
            error = t("location_permission_needed")
        }
    }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(t("prayer_title"))
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                CardSurface {
                    Text(t("method_select"), color = PaleGold, fontWeight = FontWeight.Bold)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 8.dp)) {
                        FilterChoice(t("method_karachi"), method == 1) { method = 1; times = null; PrayerTimeBackgroundSync.updateMethod(context, 1) }
                        FilterChoice(t("method_mwl"), method == 3) { method = 3; times = null; PrayerTimeBackgroundSync.updateMethod(context, 3) }
                        FilterChoice(t("method_ummalqura"), method == 4) { method = 4; times = null; PrayerTimeBackgroundSync.updateMethod(context, 4) }
                    }
                    Text(t("hanafi_asr"), color = Gold, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
                    Text(t("prayer_method_note"), color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
                }
            }
            item {
                CardSurface {
                    Button(
                        onClick = {
                            if (hasLocationPermission(context)) loadCurrent()
                            else permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
                        },
                        enabled = !loading,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)
                    ) { Text(if (loading) "…" else t("use_current_location"), fontWeight = FontWeight.Bold) }
                    Text(t("prayer_location_note"), color = Muted, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 8.dp))
                    Text("${if (language == "en") "Coordinates" else if (language == "ur") "مقام کے نقاط" else if (language == "ar") "الإحداثيات" else "लोकेशन कोऑर्डिनेट"}: ${latitude.ifBlank { "—" }}, ${longitude.ifBlank { "—" }}", color = Muted, fontSize = 10.sp)
                }
            }
            item {
                CardSurface {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(t("salah_tracker"), color = PaleGold, fontWeight = FontWeight.Bold)
                        Text("$salahDoneCount / 5", color = Gold, fontWeight = FontWeight.Bold)
                    }
                    Text(t("salah_tracker_note"), color = Muted, fontSize = 10.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 4.dp))
                    listOf(
                        Triple("fajr", "fajr", fajrDone), Triple("dhuhr", "dhuhr", dhuhrDone),
                        Triple("asr", "asr", asrDone), Triple("maghrib", "maghrib", maghribDone),
                        Triple("isha", "isha", ishaDone)
                    ).forEach { (key, labelKey, checked) ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = checked, onCheckedChange = { value ->
                                when (key) {
                                    "fajr" -> fajrDone = value
                                    "dhuhr" -> dhuhrDone = value
                                    "asr" -> asrDone = value
                                    "maghrib" -> maghribDone = value
                                    "isha" -> ishaDone = value
                                }
                                salahPrefs.edit().putBoolean("${salahDateKey}_$key", value).apply()
                            })
                            Text(t(labelKey), color = if (checked) Gold else SoftWhite, fontSize = 13.sp)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Divider(color = Line)
                    Text(
                        if (language == "en") "Last 7 days · self-recorded"
                        else if (language == "ur") "گزشتہ 7 دن · ذاتی اندراج"
                        else if (language == "ar") "آخر 7 أيام · تسجيل شخصي"
                        else "पिछले 7 दिन · आपका अपना रिकॉर्ड",
                        color = PaleGold, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 7.dp, bottom = 5.dp)
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (6 downTo 0).forEach { daysAgo ->
                            val day = LocalDate.now().minusDays(daysAgo.toLong())
                            val keys = listOf("fajr", "dhuhr", "asr", "maghrib", "isha")
                            val count = keys.count { key -> salahPrefs.getBoolean("${day}_$key", false) }
                            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(day.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.forLanguageTag(language)), color = Muted, fontSize = 9.sp)
                                Text(day.dayOfMonth.toString(), color = SoftWhite, fontSize = 10.sp)
                                Text("$count/5", color = if (count == 5) Gold else Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            error?.let { message -> item { CardSurface { Text(t("prayer_error"), color = Color(0xFFFFB7A8), fontWeight = FontWeight.Bold); Text(message, color = Muted, fontSize = 11.sp) } } }
            times?.let { p ->
                item {
                    CardSurface {
                        Text(HijriCalendar.gregorianShort(p.date, language), color = Muted, fontSize = 11.sp)
                        Text("${p.method} • ${p.asrSchool}", color = Gold, fontSize = 11.sp)
                        Text(p.timeZone, color = Muted, fontSize = 10.sp)
                        // Reading this state causes a light refresh every 30 seconds for the countdown.
                        val now = LocalDateTime.now(ZoneId.of(p.timeZone)).plusNanos(countdownRefresh.toLong())
                        val prayerEntries = listOf(
                            t("fajr") to p.fajr,
                            t("dhuhr") to p.dhuhr,
                            t("asr") to p.asr,
                            t("maghrib") to p.maghrib,
                            t("isha") to p.isha
                        )
                        val next = prayerEntries.mapNotNull { (name, value) ->
                            runCatching {
                                var candidate = now.toLocalDate().atTime(LocalTime.parse(value))
                                if (!candidate.isAfter(now)) candidate = candidate.plusDays(1)
                                Triple(name, candidate, value)
                            }.getOrNull()
                        }.minByOrNull { it.second }
                        if (next != null) {
                            val minutes = Duration.between(now, next.second).toMinutes().coerceAtLeast(0)
                            Text("${t("next_prayer")}: ${next.first} • ${next.third}", color = PaleGold, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(top = 10.dp))
                            Text("${minutes / 60}h ${minutes % 60}m", color = Gold, fontSize = 12.sp)
                        }
                        Divider(color = Line, modifier = Modifier.padding(vertical = 8.dp))
                        MetricRow(t("fajr"), p.fajr)
                        MetricRow(t("sunrise"), p.sunrise)
                        MetricRow(t("dhuhr"), p.dhuhr)
                        MetricRow(t("asr"), p.asr)
                        MetricRow(t("maghrib"), p.maghrib)
                        MetricRow(t("isha"), p.isha)
                    }
                }
            }
            item {
                CardSurface {
                    Text(t("prayer_method_note"), color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
                    TextButton(onClick = { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://aladhan.com/prayer-times-api"))) } }) {
                        Text("📚 ${t("reference_label")} — AlAdhan API", color = Gold)
                    }
                }
            }
        }
    }
}

@Composable
private fun FitrahScreen(language: String, onReference: (ReferenceItem) -> Unit) {
    var people by rememberSaveable { mutableStateOf("1") }
    var price by rememberSaveable { mutableStateOf("") }
    var food by remember { mutableStateOf(FitrahFood.WHEAT) }
    val parsedCount = people.toIntOrNull()
    val countIsValid = parsedCount != null && parsedCount in 1..500
    val count = parsedCount ?: 1
    val rateParsed = parseNonNegativeDecimal(price)
    val rate = rateParsed ?: BigDecimal.ZERO
    val result = remember(food, rate, count, countIsValid, rateParsed) {
        if (countIsValid && rateParsed != null) runCatching { FitrahCalculator.calculate(food, rate, count) }.getOrNull() else null
    }
    val t: (String) -> String = { AppText.get(language, it) }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(t("fitrah_title"))
        LazyColumn(contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                CardSurface {
                    Text(t("fitrah_guard"), color = PaleGold, fontSize = 12.sp, lineHeight = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(t("not_final"), color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { onReference(ReferenceCatalog.item(2, language)) }) { Text("📚 ${t("source")}", color = Gold) }
                }
            }
            item {
                CardSurface {
                    Text(t("grain"), color = PaleGold, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FoodChoice(t("wheat"), food == FitrahFood.WHEAT) { food = FitrahFood.WHEAT }
                        FoodChoice(t("barley"), food == FitrahFood.BARLEY) { food = FitrahFood.BARLEY }
                        FoodChoice(t("dates"), food == FitrahFood.DATES) { food = FitrahFood.DATES }
                        FoodChoice(t("raisins"), food == FitrahFood.RAISINS) { food = FitrahFood.RAISINS }
                    }
                }
            }
            item {
                CardSurface {
                    NumberField(t("people_count"), people, { people = it }, integer = true)
                    NumberField(t("price_per_kg"), price, { price = it })
                    if (!countIsValid || (price.isNotBlank() && rateParsed == null)) {
                        Text(t("enter_valid"), color = Color(0xFFFFB7A8), fontSize = 11.sp)
                    }
                    Text(t("fitrah_rate_note"), color = Muted, fontSize = 11.sp)
                }
            }
            item {
                CardSurface {
                    Text(t("per_person"), color = Muted, fontSize = 12.sp)
                    Text(if (result != null) "₹${money(result.valuePerPerson)}" else "—", color = PaleGold, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Text(t("total_fitrah"), color = Muted, fontSize = 12.sp)
                    Text(if (result != null) "₹${money(result.totalValue)}" else "—", color = Gold, fontSize = 31.sp, fontWeight = FontWeight.ExtraBold)
                    Text("${result?.kilogramsPerPerson ?: food.kilogramsPerPerson} ${t("kg_unit")} × $count", color = Muted, fontSize = 11.sp)
                    TextButton(onClick = { onReference(ReferenceCatalog.item(3, language)) }) { Text("📚 ${t("source")}", color = Gold) }
                }
            }
        }
    }
}

@Composable
private fun FoodChoice(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        color = if (selected) Color(0xFF26483A) else DeepGreen,
        shape = RoundedCornerShape(13.dp),
        border = BorderStroke(1.dp, if (selected) Gold else Line)
    ) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(9.dp).background(if (selected) Gold else Muted, CircleShape))
            Spacer(Modifier.width(10.dp))
            Text(label, color = if (selected) PaleGold else SoftWhite, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Composable
private fun CalendarScreen(language: String, hijriOffset: Int, eventsVersion: Int) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf(HijriCalendar.today(hijriOffset)) }
    var visibleMonth by remember { mutableStateOf(HijriCalendar.today(hijriOffset)) }
    var personalEvents by remember(context) { mutableStateOf(loadPersonalCalendarEvents(context)) }
    var newEventTitle by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(hijriOffset) {
        selected = HijriCalendar.today(hijriOffset)
        visibleMonth = selected
    }
    val year = visibleMonth.get(ChronoField.YEAR)
    val month = visibleMonth.get(ChronoField.MONTH_OF_YEAR)
    val firstGregorian = HijriCalendar.firstDayGregorian(year, month)
    val dayCount = HijriCalendar.monthLength(year, month)
    val blankCount = firstGregorian.dayOfWeek.value % 7
    val days = (1..dayCount).toList()
    val eventDays = remember(eventsVersion, month) { IslamicEventsFeed.eventDays(context, month) }
    val personalEventDays = personalEvents.mapNotNull { event ->
        runCatching {
            val hijri = HijriCalendar.fromGregorian(LocalDate.parse(event.gregorianDate), hijriOffset)
            if (hijri.get(ChronoField.YEAR) == year && hijri.get(ChronoField.MONTH_OF_YEAR) == month) hijri.get(ChronoField.DAY_OF_MONTH) else null
        }.getOrNull()
    }.toSet()
    val selectedFeedEvents = remember(eventsVersion, selected, language) {
        IslamicEventsFeed.findEvents(context, selected.get(ChronoField.MONTH_OF_YEAR), selected.get(ChronoField.DAY_OF_MONTH), language)
    }
    val selectedEvent = selectedFeedEvents.takeIf { it.isNotEmpty() }?.joinToString(" • ") { it.title }
        ?: HijriCalendar.eventName(selected.get(ChronoField.MONTH_OF_YEAR), selected.get(ChronoField.DAY_OF_MONTH), language)
    val todayEpoch = HijriCalendar.today(hijriOffset).toEpochDay()
    val selectedGregorian = LocalDate.ofEpochDay(selected.toEpochDay())
    val t: (String) -> String = { AppText.get(language, it) }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(t("calendar_title"))
        Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
            CardSurface {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    IconButton(onClick = {
                        val changed = HijriCalendar.previousMonth(visibleMonth)
                        visibleMonth = changed
                        selected = changed
                    }) { Icon(Icons.Default.ChevronLeft, contentDescription = t("previous"), tint = Gold) }
                    Text(HijriCalendar.monthTitle(visibleMonth, language), color = PaleGold, fontSize = 17.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    IconButton(onClick = {
                        val changed = HijriCalendar.nextMonth(visibleMonth)
                        visibleMonth = changed
                        selected = changed
                    }) { Icon(Icons.Default.ChevronRight, contentDescription = t("next"), tint = Gold) }
                }
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    HijriCalendar.weekdayLabels(language).forEach { Text(it, modifier = Modifier.weight(1f), color = Muted, fontSize = 10.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) }
                }
                Spacer(Modifier.height(8.dp))
                LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxWidth().height(if (dayCount > 30) 310.dp else 270.dp), userScrollEnabled = false) {
                    items(blankCount) { Spacer(Modifier.aspectRatio(0.83f)) }
                    items(days) { day ->
                        val date = HijriCalendar.dateInMonth(year, month, day)
                        val isSelected = date.toEpochDay() == selected.toEpochDay()
                        val isToday = date.toEpochDay() == todayEpoch
                        val gregorian = LocalDate.ofEpochDay(date.toEpochDay())
                        CalendarCell(
                            day = day,
                            gregorianDay = gregorian.dayOfMonth,
                            selected = isSelected,
                            today = isToday,
                            hasEvent = day in eventDays || day in personalEventDays,
                            onClick = { selected = date }
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            CardSurface {
                Text(t("selected_date"), color = Muted, fontSize = 11.sp)
                Text(HijriCalendar.format(selected, language), color = PaleGold, fontWeight = FontWeight.Bold, fontSize = 19.sp)
                Text(HijriCalendar.gregorianShort(LocalDate.ofEpochDay(selected.toEpochDay()), language), color = Muted, fontSize = 12.sp)
                Spacer(Modifier.height(7.dp))
                Text(selectedEvent ?: t("no_event"), color = if (selectedEvent != null) Gold else SoftWhite, fontSize = 13.sp)
                if (selectedFeedEvents.isNotEmpty()) {
                    selectedFeedEvents.forEach { item ->
                        Text(item.title, color = PaleGold, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 7.dp))
                        Text(item.description, color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
                        TextButton(onClick = {
                            runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.sourceUrl))) }
                        }, contentPadding = PaddingValues(0.dp)) {
                            Text("📚 ${t("event_reference")} — ${item.sourceName}", color = Gold, fontSize = 10.sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            CardSurface {
                Text(
                    if (language == "en") "Personal calendar events"
                    else if (language == "ur") "ذاتی کیلنڈر کے واقعات"
                    else if (language == "ar") "المناسبات الشخصية في التقويم"
                    else "आपके निजी कैलेंडर कार्यक्रम",
                    color = PaleGold, fontSize = 14.sp, fontWeight = FontWeight.Bold
                )
                Text(
                    if (language == "en") "Selected date: ${selectedGregorian.toString()}"
                    else if (language == "ur") "منتخب تاریخ: ${selectedGregorian}"
                    else if (language == "ar") "التاريخ المحدد: ${selectedGregorian}"
                    else "चुनी तारीख़: ${selectedGregorian}",
                    color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )
                OutlinedTextField(
                    value = newEventTitle,
                    onValueChange = { newEventTitle = it.take(80) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text(if (language == "en") "Event title" else if (language == "ur") "واقعے کا نام" else if (language == "ar") "اسم المناسبة" else "कार्यक्रम का नाम") },
                    placeholder = { Text(if (language == "en") "e.g. Zakat review" else if (language == "ur") "مثلاً زکوٰۃ کا جائزہ" else if (language == "ar") "مثلاً مراجعة الزكاة" else "जैसे ज़कात की समीक्षा") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SoftWhite, unfocusedTextColor = SoftWhite,
                        focusedBorderColor = Gold, unfocusedBorderColor = Line,
                        focusedLabelColor = Gold, unfocusedLabelColor = Muted,
                        cursorColor = Gold
                    )
                )
                Button(
                    onClick = {
                        val title = newEventTitle.trim()
                        if (title.isNotEmpty() && personalEvents.size < 500) {
                            val nextId = maxOf(System.currentTimeMillis(), (personalEvents.maxOfOrNull { it.id } ?: 0L) + 1L)
                            val event = PersonalCalendarEvent(nextId, title, selectedGregorian.toString())
                            personalEvents = (personalEvents + event).sortedBy { it.gregorianDate }
                            savePersonalCalendarEvents(context, personalEvents)
                            newEventTitle = ""
                        }
                    },
                    enabled = newEventTitle.trim().isNotEmpty() && personalEvents.size < 500,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)
                ) {
                    Text(if (language == "en") "Save for selected date" else if (language == "ur") "منتخب تاریخ کے لیے محفوظ کریں" else if (language == "ar") "احفظ للتاريخ المحدد" else "चुनी तारीख़ के लिए सेव करें", fontWeight = FontWeight.Bold)
                }
                val selectedPersonalEvents = personalEvents.filter { it.gregorianDate == selectedGregorian.toString() }
                if (selectedPersonalEvents.isEmpty()) {
                    Text(if (language == "en") "No personal event saved for this date." else if (language == "ur") "اس تاریخ کے لیے کوئی ذاتی واقعہ محفوظ نہیں۔" else if (language == "ar") "لا توجد مناسبة شخصية محفوظَة لهذا التاريخ." else "इस तारीख़ के लिए कोई निजी कार्यक्रम सेव नहीं है।", color = Muted, fontSize = 11.sp)
                } else {
                    selectedPersonalEvents.forEach { event ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f).padding(vertical = 4.dp)) {
                                Text(event.title, color = SoftWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(event.gregorianDate, color = Muted, fontSize = 9.sp)
                            }
                            TextButton(onClick = {
                                personalEvents = personalEvents.filterNot { it.id == event.id }
                                savePersonalCalendarEvents(context, personalEvents)
                            }) { Text(if (language == "en") "Delete" else if (language == "ur") "حذف" else if (language == "ar") "حذف" else "हटाएँ", color = Color(0xFFFFB7A8), fontSize = 10.sp) }
                        }
                    }
                }
                Text(
                    if (language == "en") "Events are stored only on this device. This app does not schedule alarm notifications; add important events to your phone calendar for dependable alerts."
                    else if (language == "ur") "واقعات صرف اسی ڈیوائس پر محفوظ ہوتے ہیں۔ ایپ الارم نوٹیفکیشن شیڈول نہیں کرتی؛ قابلِ اعتماد یاددہانی کے لیے فون کیلنڈر میں بھی شامل کریں۔"
                    else if (language == "ar") "تُحفظ المناسبات على هذا الجهاز فقط. لا يحدد التطبيق إشعارات منبهة؛ أضف المناسبات المهمة إلى تقويم الهاتف للتنبيهات الموثوقة."
                    else "कार्यक्रम सिर्फ़ इसी फोन में सेव होते हैं। यह ऐप alarm notification schedule नहीं करता; भरोसेमंद alert के लिए ज़रूरी तारीख़ें फ़ोन कैलेंडर में भी जोड़ें।",
                    color = Muted, fontSize = 9.sp, lineHeight = 14.sp, modifier = Modifier.padding(top = 8.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(t("moon_note"), color = Muted, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 4.dp))
        }
    }
}

@Composable
private fun CalendarCell(day: Int, gregorianDay: Int, selected: Boolean, today: Boolean, hasEvent: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.padding(2.dp).aspectRatio(0.83f).clip(RoundedCornerShape(10.dp))
            .background(when { selected -> Color(0xFF3D633D); today -> Color(0xFF29483A); else -> DeepGreen })
            .clickable(onClick = onClick)
            .padding(vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(day.toString(), color = if (selected) PaleGold else SoftWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(gregorianDay.toString(), color = Muted, fontSize = 9.sp)
        if (today || hasEvent) {
            Box(Modifier.padding(top = 2.dp).size(if (today) 5.dp else 3.dp).background(if (today) Gold else Color(0xFF8EBC9F), CircleShape))
        }
    }
}

@Composable
private fun HawlScreen(language: String, onReference: (ReferenceItem) -> Unit) {
    var startDate by rememberSaveable { mutableStateOf("") }
    var result by remember { mutableStateOf<Pair<String, String>?>(null) }
    var error by remember { mutableStateOf(false) }
    val t: (String) -> String = { AppText.get(language, it) }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(t("hawl_title"))
        Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            CardSurface {
                Text(t("hawl_note"), color = PaleGold, fontSize = 12.sp, lineHeight = 18.sp)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it.take(10) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(t("hawl_start"), fontSize = 12.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SoftWhite, unfocusedTextColor = SoftWhite,
                        focusedBorderColor = Gold, unfocusedBorderColor = Line,
                        focusedLabelColor = Gold, unfocusedLabelColor = Muted, cursorColor = Gold
                    ),
                    shape = RoundedCornerShape(13.dp)
                )
                Button(
                    onClick = {
                        val parsed = runCatching { LocalDate.parse(startDate.trim()) }.getOrNull()
                        if (parsed == null || parsed.isAfter(LocalDate.now())) {
                            result = null
                            error = true
                        } else {
                            val startHijri = HijrahDate.from(parsed)
                            val anniversary = startHijri.plus(1, ChronoUnit.YEARS) as HijrahDate
                            val gregorianAnniversary = LocalDate.ofEpochDay(anniversary.toEpochDay())
                            result = HijriCalendar.format(anniversary, language) to HijriCalendar.gregorianShort(gregorianAnniversary, language)
                            error = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink),
                    modifier = Modifier.fillMaxWidth()
                ) { Text(t("hawl_calculate"), fontWeight = FontWeight.Bold) }
                if (error) Text(t("hawl_date_error"), color = Color(0xFFFFB7A8), fontSize = 11.sp)
                result?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(t("hawl_result"), color = Muted, fontSize = 11.sp)
                    Text(it.first, color = PaleGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(it.second, color = Muted, fontSize = 12.sp)
                }
                Spacer(Modifier.height(6.dp))
                TextButton(onClick = { onReference(ReferenceCatalog.item(7, language)) }) {
                    Text("📚 ${t("source")}", color = Gold)
                }
            }
        }
    }
}

@Composable
private fun ReferencesScreen(language: String, onReference: (ReferenceItem) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(AppText.get(language, "references"))
        LazyColumn(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(ReferenceCatalog.itemsFor(language).size) { index ->
                val item = ReferenceCatalog.itemsFor(language)[index]
                Card(modifier = Modifier.fillMaxWidth().clickable { onReference(item) }, colors = CardDefaults.cardColors(containerColor = CardGreen), shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, Line)) {
                    Column(Modifier.padding(15.dp)) {
                        Text(item.title, color = PaleGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(Modifier.height(5.dp))
                        Text(item.summary, color = SoftWhite, fontSize = 12.sp, lineHeight = 17.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(item.printedReference, color = Muted, fontSize = 10.sp)
                        Text("${AppText.get(language, "source")}: ${item.onlineSource}", color = Gold, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReferenceDialog(item: ReferenceItem, language: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardGreen,
        titleContentColor = PaleGold,
        textContentColor = SoftWhite,
        title = { Text(item.title, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(item.summary, fontSize = 13.sp)
                Text(item.printedReference, color = Gold, fontSize = 11.sp)
                Text(item.reviewNote, color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
            }
        },
        confirmButton = {
            if (item.url.isNotBlank()) {
                TextButton(onClick = {
                    runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.url))) }
                    onDismiss()
                }) { Text(AppText.get(language, "open_source"), color = Gold) }
            } else {
                TextButton(onClick = onDismiss) { Text(AppText.get(language, "back"), color = Gold) }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(AppText.get(language, "back"), color = Muted) } }
    )
}

@Composable
private fun MasarifScreen(language: String, onReference: (ReferenceItem) -> Unit) {
    val t: (String) -> String = { AppText.get(language, it) }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(t("masarif_title"))
        Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            CardSurface {
                Text("سورۃ التوبہ • آیت 60", color = Gold, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("إِنَّمَا الصَّدَقَاتُ لِلْفُقَرَاءِ وَالْمَسَاكِينِ وَالْعَامِلِينَ عَلَيْهَا وَالْمُؤَلَّفَةِ قُلُوبُهُمْ وَفِي الرِّقَابِ وَالْغَارِمِينَ وَفِي سَبِيلِ اللَّهِ وَابْنِ السَّبِيلِ", color = PaleGold, fontSize = 18.sp, lineHeight = 30.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                Text(t("tamleek"), color = SoftWhite, fontSize = 13.sp, lineHeight = 19.sp)
                Spacer(Modifier.height(8.dp))
                Text(t("masarif_note"), color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
                TextButton(onClick = { onReference(ReferenceCatalog.item(6, language)) }) { Text("📚 ${t("source")}", color = Gold) }
            }
            ReferenceMiniCard(t("source_note"), language) { onReference(ReferenceCatalog.item(0, language)) }
        }
    }
}

@Composable
private fun AssetGuideScreen(language: String, onReference: (ReferenceItem) -> Unit) {
    val t: (String) -> String = { AppText.get(language, it) }
    val guideItems = AssetGuideCatalog.items(language)
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(t("asset_guide"))
        LazyColumn(
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                CardSurface {
                    Text(t("asset_guide_caution"), color = PaleGold, fontSize = 12.sp, lineHeight = 18.sp)
                }
            }
            items(guideItems) { guide ->
                CardSurface {
                    Text(guide.title, color = PaleGold, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(guide.explanation, color = SoftWhite, fontSize = 12.sp, lineHeight = 18.sp)
                    TextButton(onClick = { onReference(ReferenceCatalog.item(guide.referenceIndex, language)) }) {
                        Text("📚 ${t("source")}", color = Gold)
                    }
                }
            }
        }
    }
}

@Composable
private fun TasbihScreen(language: String) {
    var count by rememberSaveable { mutableIntStateOf(0) }
    val t: (String) -> String = { AppText.get(language, it) }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(t("tasbih_title"))
        Column(Modifier.fillMaxSize().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(t("count"), color = Muted, fontSize = 14.sp)
            Text(count.toString(), color = PaleGold, fontSize = 74.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(22.dp))
            Button(onClick = { count += 1 }, modifier = Modifier.size(190.dp), shape = CircleShape, colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)) {
                Text("ذِكْر", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(22.dp))
            TextButton(onClick = { count = 0 }) { Icon(Icons.Default.Refresh, contentDescription = null, tint = Muted); Spacer(Modifier.width(5.dp)); Text(t("reset"), color = Muted) }
        }
    }
}

@Composable
private fun SettingsScreen(language: String, offset: Int, onLanguage: (String) -> Unit, onOffset: (Int) -> Unit) {
    val context = LocalContext.current
    val settingsPrefs = remember { context.getSharedPreferences("noor_settings", Context.MODE_PRIVATE) }
    var widgetDesign by remember { mutableStateOf(settingsPrefs.getString("widget_design", "auto") ?: "auto") }
    val t: (String) -> String = { AppText.get(language, it) }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(t("settings_title"))
        Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            CardSurface {
                Text(t("language"), color = PaleGold, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                AppText.languages.forEach { option ->
                    Row(Modifier.fillMaxWidth().clickable { onLanguage(option.code) }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(option.nativeName, color = if (option.code == language) Gold else SoftWhite, modifier = Modifier.weight(1f), fontWeight = if (option.code == language) FontWeight.Bold else FontWeight.Normal)
                        if (option.code == language) Text("✓", color = Gold, fontWeight = FontWeight.Bold)
                    }
                }
            }
            CardSurface {
                Text(t("hijri_adjust"), color = PaleGold, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { onOffset((offset - 1).coerceIn(-2, 2)) }) { Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Gold) }
                    Text("$offset ${t("days")}", color = SoftWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { onOffset((offset + 1).coerceIn(-2, 2)) }) { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Gold) }
                }
                Text(t("moon_note"), color = Muted, fontSize = 11.sp)
            }
            CardSurface {
                Text(t("widget_design"), color = PaleGold, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.padding(top = 8.dp)) {
                    listOf("auto" to t("widget_auto"), "emerald" to t("widget_green"), "night" to t("widget_night"), "ramadan" to t("widget_ramadan"), "eid" to t("widget_eid")).forEach { (key, label) ->
                        FilterChoice(label, widgetDesign == key) {
                            widgetDesign = key
                            settingsPrefs.edit().putString("widget_design", key).apply()
                            HijriWidgetProvider.refreshAll(context)
                        }
                    }
                }
            }
            CardSurface {
                Text(t("prayer_location_note"), color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
                TextButton(onClick = {
                    PrayerTimeBackgroundSync.clearLocation(context)
                    Toast.makeText(context, t("location_cleared"), Toast.LENGTH_SHORT).show()
                }) { Text(t("clear_prayer_location"), color = Gold) }
            }
            CardSurface { Text(t("privacy"), color = Muted, fontSize = 12.sp, lineHeight = 18.sp) }
            CardSurface { Text(t("disclaimer"), color = PaleGold, fontSize = 12.sp, lineHeight = 18.sp) }
        }
    }
}

@Composable
private fun MoreScreen(language: String, onNavigate: (AppPage) -> Unit, onAddWidget: () -> Unit) {
    val t: (String) -> String = { AppText.get(language, it) }
    val items = listOf(
        Triple("📖", AppPage.REFERENCES, t("references")),
        Triple("🤲", AppPage.MASARIF, t("masarif")),
        Triple("📿", AppPage.TASBIH, t("tasbih")),
        Triple("⚙", AppPage.SETTINGS, t("settings"))
    )
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(t("more_title"))
        LazyColumn(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            items(items.size) { index ->
                val item = items[index]
                Card(modifier = Modifier.fillMaxWidth().clickable { onNavigate(item.second) }, colors = CardDefaults.cardColors(containerColor = CardGreen), border = BorderStroke(1.dp, Line), shape = RoundedCornerShape(17.dp)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(item.first, fontSize = 22.sp)
                        Spacer(Modifier.width(14.dp))
                        Text(item.third, color = SoftWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Gold)
                    }
                }
            }
            item {
                CardSurface {
                    Text(t("add_widget"), color = PaleGold, fontWeight = FontWeight.Bold)
                    Text(t("widget_help"), color = Muted, fontSize = 11.sp)
                    Button(onClick = onAddWidget, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)) { Text(t("widget_add")) }
                }
            }
            item { Text(t("privacy"), color = Muted, fontSize = 11.sp, modifier = Modifier.padding(vertical = 8.dp)) }
        }
    }
}

@Composable
private fun NumberField(label: String, value: String, onValue: (String) -> Unit, integer: Boolean = false, decimal: Boolean = true) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValue(it.filter { ch -> ch.isDigit() || (!integer && decimal && (ch == '.' || ch == ',')) }) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        label = { Text(label, fontSize = 12.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = if (integer || !decimal) KeyboardType.Number else KeyboardType.Decimal),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = SoftWhite,
            unfocusedTextColor = SoftWhite,
            focusedBorderColor = Gold,
            unfocusedBorderColor = Line,
            focusedLabelColor = Gold,
            unfocusedLabelColor = Muted,
            cursorColor = Gold
        ),
        shape = RoundedCornerShape(13.dp)
    )
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Muted, fontSize = 11.sp, modifier = Modifier.weight(1f))
        Text(value, color = PaleGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ReferenceMiniCard(message: String, language: String, onClick: () -> Unit) {
    CardSurface {
        Text(message, color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
        TextButton(onClick = onClick) { Text("📚 ${AppText.get(language, "reference_label")}", color = Gold) }
    }
}

private fun money(value: BigDecimal): String =
    value.setScale(2, RoundingMode.HALF_UP).toPlainString()
