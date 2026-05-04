package com.nammamela.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.mindrot.jbcrypt.BCrypt

object PinManager {

    private const val PREFS_FILE = "namma_mela_secure_prefs"
    private const val KEY_PIN_HASH = "manager_pin_hash"
    private const val DEFAULT_PIN = "1234"

    private fun getPrefs(context: Context) = try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        // Fallback to regular SharedPreferences if encryption fails
        context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
    }

    fun isPinSet(context: Context): Boolean {
        return getPrefs(context).contains(KEY_PIN_HASH)
    }

    fun setPin(context: Context, pin: String) {
        val hash = BCrypt.hashpw(pin, BCrypt.gensalt())
        getPrefs(context).edit().putString(KEY_PIN_HASH, hash).apply()
    }

    fun verifyPin(context: Context, pin: String): Boolean {
        val hash = getPrefs(context).getString(KEY_PIN_HASH, null) ?: return false
        return try {
            BCrypt.checkpw(pin, hash)
        } catch (e: Exception) {
            false
        }
    }

    fun ensureDefaultPin(context: Context) {
        if (!isPinSet(context)) {
            setPin(context, DEFAULT_PIN)
        }
    }
}
