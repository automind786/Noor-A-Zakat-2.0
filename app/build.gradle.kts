import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

// ---------- Release signing (keystore.properties file se; git mein commit NA karein) ----------
val keystoreProps = Properties().apply {
    val f = rootProject.file("keystore.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

// ---------- AdMob IDs ----------
val testAppId = "ca-app-pub-3940256099942544~3347511713"
val testBannerId = "ca-app-pub-3940256099942544/9214589741"
val realAppId = (project.findProperty("ADMOB_APP_ID") as String?)?.trim().orEmpty()
val realBannerId = (project.findProperty("ADMOB_BANNER_ID") as String?)?.trim().orEmpty()

android {
    namespace = "com.hanafi.noorezakat"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hanafi.noorezakat"
        minSdk = 24
        targetSdk = 35          // Google Play: 31 Aug 2026 se naye apps/updates ke liye zaruri
        versionCode = 9
        versionName = "4.0.1"
    }

    signingConfigs {
        if (keystoreProps.containsKey("storeFile")) {
            create("release") {
                storeFile = rootProject.file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        debug {
            // Test build ko production app se alag rakhein: existing app/data overwrite nahi hoga.
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            // Debug mein hamesha TEST ads (apni real ads par khud click karna ban/suspend ka sabab banta hai)
            manifestPlaceholders["admobAppId"] = testAppId
            buildConfigField("String", "ADMOB_BANNER_ID", "\"$testBannerId\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            manifestPlaceholders["admobAppId"] = realAppId.ifEmpty { testAppId }
            buildConfigField("String", "ADMOB_BANNER_ID", "\"${realBannerId.ifEmpty { testBannerId }}\"")
            signingConfigs.findByName("release")?.let { signingConfig = it }
            if (realAppId.isEmpty() || realBannerId.isEmpty()) {
                logger.warn("WARNING: ADMOB_APP_ID / ADMOB_BANNER_ID khali hain -> release build mein TEST ads rahengi.")
            }
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.webkit:webkit:1.12.1")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Google AdMob + consent (GDPR/UMP)
    implementation("com.google.android.gms:play-services-ads:23.6.0")
    implementation("com.google.android.ump:user-messaging-platform:3.1.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20231013")
}
