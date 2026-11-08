package com.noorazakat.app.core

import com.noorazakat.app.data.ReferenceCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalizationAndReferencesTest {
    @Test
    fun everySupportedLanguageHasAllUiKeys() {
        listOf("hi", "ur", "en", "ar").forEach { language ->
            assertTrue("Missing translations for $language: ${AppText.missingKeys(language)}", AppText.missingKeys(language).isEmpty())
        }
    }

    @Test
    fun allReferenceItemsAreAvailableInAllSupportedLanguages() {
        val count = ReferenceCatalog.items.size
        listOf("hi", "ur", "en", "ar").forEach { language ->
            val localized = ReferenceCatalog.itemsFor(language)
            assertEquals(count, localized.size)
            assertTrue(localized.all { it.title.isNotBlank() && it.summary.isNotBlank() && it.reviewNote.isNotBlank() })
        }
    }
}
