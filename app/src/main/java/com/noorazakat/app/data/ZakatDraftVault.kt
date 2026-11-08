package com.noorazakat.app.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import org.json.JSONObject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Encrypted on-device storage for the Zakat worksheet.
 * Values are encrypted with AES-256-GCM and a non-exportable Android Keystore key.
 * If encryption/decryption fails, the class does not fall back to plaintext storage.
 */
object ZakatDraftVault {
    private const val PREFS = "noor_secure_zakat_v1"
    private const val STORAGE_KEY = "encrypted_working_draft"
    private const val KEY_ALIAS = "NoorZakatAmanahDraftKeyV1"
    private const val KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"

    @Synchronized
    private fun secretKey(): SecretKey {
        val store = KeyStore.getInstance(KEYSTORE).apply { load(null) }
        (store.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generator.generateKey()
    }

    fun hasStoredDraft(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).contains(STORAGE_KEY)

    fun load(context: Context): JSONObject? {
        val stored = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(STORAGE_KEY, null) ?: return null
        return try {
            val parts = stored.split(":", limit = 2)
            require(parts.size == 2) { "Encrypted draft is malformed" }
            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val ciphertext = Base64.decode(parts[1], Base64.NO_WRAP)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(128, iv))
            JSONObject(String(cipher.doFinal(ciphertext), Charsets.UTF_8))
        } catch (_: Exception) {
            null
        }
    }

    @Synchronized
    fun save(context: Context, json: String): Boolean = try {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val iv = Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
        val encrypted = Base64.encodeToString(cipher.doFinal(json.toByteArray(Charsets.UTF_8)), Base64.NO_WRAP)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(STORAGE_KEY, "$iv:$encrypted")
            .commit()
    } catch (_: Exception) {
        false
    }

    @Synchronized
    fun clear(context: Context): Boolean = try {
        val removed = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .remove(STORAGE_KEY).commit()
        val store = KeyStore.getInstance(KEYSTORE).apply { load(null) }
        if (store.containsAlias(KEY_ALIAS)) store.deleteEntry(KEY_ALIAS)
        removed
    } catch (_: Exception) {
        false
    }
}
