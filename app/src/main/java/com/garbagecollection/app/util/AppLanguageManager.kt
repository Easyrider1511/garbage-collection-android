package com.garbagecollection.app.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import com.garbagecollection.app.R
import java.util.Locale

object AppLanguageManager {

    private const val PREFS_NAME = "gc_app_settings"
    private const val KEY_LANGUAGE_TAG = "language_tag"

    const val LANGUAGE_EN = "en"
    const val LANGUAGE_PT_PT = "pt-PT"

    @JvmStatic
    fun applySavedLocale(context: Context) {
        val languageTag = getSavedLanguageTag(context)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
    }

    fun applyLanguage(context: Context, languageTag: String) {
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit { putString(KEY_LANGUAGE_TAG, languageTag) }

        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
    }

    fun getSavedLanguageTag(context: Context): String {
        val savedLanguage = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE_TAG, null)

        return normalizeLanguageTag(savedLanguage ?: defaultLanguageTag())
    }

    fun getCurrentLanguageDisplayName(context: Context): String {
        return when (getSavedLanguageTag(context)) {
            LANGUAGE_PT_PT -> context.getString(R.string.language_portuguese_portugal)
            else -> context.getString(R.string.language_english)
        }
    }

    fun supportedLanguageTags(): Array<String> = arrayOf(LANGUAGE_EN, LANGUAGE_PT_PT)

    fun supportedLanguageLabels(context: Context): Array<String> = arrayOf(
        context.getString(R.string.language_english),
        context.getString(R.string.language_portuguese_portugal)
    )

    private fun defaultLanguageTag(): String {
        val systemLanguage = LocaleListCompat.getAdjustedDefault().toLanguageTags()
        return if (systemLanguage.lowercase(Locale.ROOT).startsWith("pt")) {
            LANGUAGE_PT_PT
        } else {
            LANGUAGE_EN
        }
    }

    private fun normalizeLanguageTag(languageTag: String): String {
        return if (languageTag.equals(LANGUAGE_PT_PT, ignoreCase = true)) {
            LANGUAGE_PT_PT
        } else {
            LANGUAGE_EN
        }
    }
}
