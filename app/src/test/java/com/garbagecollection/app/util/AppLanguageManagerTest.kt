package com.garbagecollection.app.util

import com.garbagecollection.app.R
import com.garbagecollection.app.testsupport.TestFixtures
import java.util.Locale
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AppLanguageManagerTest {

    private val originalLocale: Locale = Locale.getDefault()

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
        TestFixtures.clearAppState()
    }

    @Test
    fun `returns English as default language when no preference is saved`() {
        val context = TestFixtures.appContext()

        assertEquals(AppLanguageManager.LANGUAGE_EN, AppLanguageManager.getSavedLanguageTag(context))
        assertEquals(
            context.getString(R.string.language_english),
            AppLanguageManager.getCurrentLanguageDisplayName(context)
        )
    }

    @Test
    fun `applyLanguage stores Portuguese Portugal and exposes translated display name`() {
        val context = TestFixtures.appContext()

        AppLanguageManager.applyLanguage(context, AppLanguageManager.LANGUAGE_PT_PT)
        AppLanguageManager.applySavedLocale(context)

        assertEquals(AppLanguageManager.LANGUAGE_PT_PT, AppLanguageManager.getSavedLanguageTag(context))
        assertEquals(
            context.getString(R.string.language_portuguese_portugal),
            AppLanguageManager.getCurrentLanguageDisplayName(context)
        )
    }

    @Test
    fun `normalizes unsupported language tags to English`() {
        val context = TestFixtures.appContext()
        context.getSharedPreferences("gc_app_settings", 0)
            .edit()
            .putString("language_tag", "fr-FR")
            .commit()

        assertEquals(AppLanguageManager.LANGUAGE_EN, AppLanguageManager.getSavedLanguageTag(context))
    }

    @Test
    fun `supported languages expose stable tags and localized labels`() {
        val context = TestFixtures.appContext()

        assertArrayEquals(
            arrayOf(AppLanguageManager.LANGUAGE_EN, AppLanguageManager.LANGUAGE_PT_PT),
            AppLanguageManager.supportedLanguageTags()
        )
        assertArrayEquals(
            arrayOf(
                context.getString(R.string.language_english),
                context.getString(R.string.language_portuguese_portugal)
            ),
            AppLanguageManager.supportedLanguageLabels(context)
        )
    }
}
