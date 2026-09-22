package com.example.cashbookneo.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class PreferenceManager(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "secure_settings",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val LAST_BACKGROUND_TIME = longPreferencesKey("last_background_time")
        val LOCK_TIMEOUT = longPreferencesKey("lock_timeout")
        val ACTIVE_PROFILE_ID = stringPreferencesKey("active_profile_id")
        
        // Appearance
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val ACCENT_COLOR_HEX = stringPreferencesKey("accent_color_hex")
        val CHART_TYPE = stringPreferencesKey("chart_type") // "Donut", "Bars"
        val COMPACT_VIEW = booleanPreferencesKey("compact_view")
        
        // Accounting & General
        val FIRST_DAY_OF_WEEK = stringPreferencesKey("first_day_of_week") // "Mon", "Sun"
        val DEFAULT_ENTRY_TYPE = stringPreferencesKey("default_entry_type") // "Outflow", "Inflow"
        val FISCAL_MONTH_START = longPreferencesKey("fiscal_month_start")
        val SCREEN_MASKING = booleanPreferencesKey("screen_masking")
        val MULTI_CURRENCY_CONVERTER = booleanPreferencesKey("multi_currency_converter")
        val BIOMETRICS_ENABLED = booleanPreferencesKey("biometrics_enabled")
        val IS_APP_LOCK_ENABLED = booleanPreferencesKey("is_app_lock_enabled")
        val SCRAMBLE_KEYPAD = booleanPreferencesKey("scramble_keypad")
        val LOCK_ON_SCREEN_OFF = booleanPreferencesKey("lock_on_screen_off")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[IS_DARK_MODE] ?: true }
    val accentColorHex: Flow<String> = context.dataStore.data.map { it[ACCENT_COLOR_HEX] ?: "#818CF8" }
    val chartType: Flow<String> = context.dataStore.data.map { it[CHART_TYPE] ?: "Donut" }
    val compactView: Flow<Boolean> = context.dataStore.data.map { it[COMPACT_VIEW] ?: false }
    val firstDayOfWeek: Flow<String> = context.dataStore.data.map { it[FIRST_DAY_OF_WEEK] ?: "Mon" }
    val defaultEntryType: Flow<String> = context.dataStore.data.map { it[DEFAULT_ENTRY_TYPE] ?: "Outflow" }
    val fiscalMonthStart: Flow<Long> = context.dataStore.data.map { it[FISCAL_MONTH_START] ?: 1L }
    val screenMasking: Flow<Boolean> = context.dataStore.data.map { it[SCREEN_MASKING] ?: true }
    val multiCurrencyConverter: Flow<Boolean> = context.dataStore.data.map { it[MULTI_CURRENCY_CONVERTER] ?: true }
    val biometricsEnabled: Flow<Boolean> = context.dataStore.data.map { it[BIOMETRICS_ENABLED] ?: true }
    val isAppLockEnabled: Flow<Boolean> = context.dataStore.data.map { it[IS_APP_LOCK_ENABLED] ?: true }
    val scrambleKeypad: Flow<Boolean> = context.dataStore.data.map { it[SCRAMBLE_KEYPAD] ?: false }
    val lockOnScreenOff: Flow<Boolean> = context.dataStore.data.map { it[LOCK_ON_SCREEN_OFF] ?: true }

    suspend fun setDarkMode(enabled: Boolean) { context.dataStore.edit { it[IS_DARK_MODE] = enabled } }
    suspend fun setAccentColor(hex: String) { context.dataStore.edit { it[ACCENT_COLOR_HEX] = hex } }
    suspend fun setChartType(type: String) { context.dataStore.edit { it[CHART_TYPE] = type } }
    suspend fun setCompactView(enabled: Boolean) { context.dataStore.edit { it[COMPACT_VIEW] = enabled } }
    suspend fun setFirstDayOfWeek(day: String) { context.dataStore.edit { it[FIRST_DAY_OF_WEEK] = day } }
    suspend fun setDefaultEntryType(type: String) { context.dataStore.edit { it[DEFAULT_ENTRY_TYPE] = type } }
    suspend fun setFiscalMonthStart(day: Long) { context.dataStore.edit { it[FISCAL_MONTH_START] = day } }
    suspend fun setScreenMasking(enabled: Boolean) { context.dataStore.edit { it[SCREEN_MASKING] = enabled } }
    suspend fun setMultiCurrencyConverter(enabled: Boolean) { context.dataStore.edit { it[MULTI_CURRENCY_CONVERTER] = enabled } }
    suspend fun setBiometricsEnabled(enabled: Boolean) { context.dataStore.edit { it[BIOMETRICS_ENABLED] = enabled } }
    suspend fun setAppLockEnabled(enabled: Boolean) { context.dataStore.edit { it[IS_APP_LOCK_ENABLED] = enabled } }
    suspend fun setScrambleKeypad(enabled: Boolean) { context.dataStore.edit { it[SCRAMBLE_KEYPAD] = enabled } }
    suspend fun setLockOnScreenOff(enabled: Boolean) { context.dataStore.edit { it[LOCK_ON_SCREEN_OFF] = enabled } }

    val activeProfileId: Flow<String?> = context.dataStore.data
        .map { it[ACTIVE_PROFILE_ID] }

    suspend fun setActiveProfileId(id: String) {
        context.dataStore.edit { it[ACTIVE_PROFILE_ID] = id }
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false
        }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    fun savePin(pinHash: String, salt: String) {
        encryptedPrefs.edit().putString("pin_hash", pinHash).putString("pin_salt", salt).apply()
    }

    fun getPinHash(): String? = encryptedPrefs.getString("pin_hash", null)
    fun getPinSalt(): String? = encryptedPrefs.getString("pin_salt", null)

    val lastBackgroundTime: Flow<Long> = context.dataStore.data
        .map { it[LAST_BACKGROUND_TIME] ?: 0L }

    suspend fun setLastBackgroundTime(time: Long) {
        context.dataStore.edit { it[LAST_BACKGROUND_TIME] = time }
    }

    val lockTimeout: Flow<Long> = context.dataStore.data
        .map { it[LOCK_TIMEOUT] ?: 0L } // 0 means Immediately

    suspend fun setLockTimeout(timeout: Long) {
        context.dataStore.edit { it[LOCK_TIMEOUT] = timeout }
    }
}
