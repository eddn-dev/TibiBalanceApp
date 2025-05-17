package com.app.tibibalance.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences              // <- Para el DataStore<Preferences>
import androidx.datastore.preferences.preferencesDataStore        // <- Trae el delegado preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1) Define aquí la extensión en Context para crear tu DataStore
private val Context.dataStore by preferencesDataStore(name = "tibibalance_prefs")

class OnboardingPreferences(private val context: Context) {

    companion object {
        // 2) Clave para tu flag booleano
        private val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
    }

    // 3) Flow que emite true/false (default = false)
    val hasSeenOnboarding: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[ONBOARDING_DONE] ?: false
        }

    // 4) Suspender para cambiar el flag
    suspend fun setSeenOnboarding(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_DONE] = value
        }
    }
}
