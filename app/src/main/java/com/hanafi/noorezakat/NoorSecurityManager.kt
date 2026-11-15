package com.hanafi.noorezakat

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** User ka hisab Android Keystore (AES-256-GCM) se encrypt hokar device par hi rehta hai. */
object NoorSecurityManager {

    private const val KEY_ALIAS = "NoorZakatMasterKey_v1"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"

    private fun getOrCreateSecretKey(): SecretKey {
        val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val existing = ks.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existing != null) return existing.secretKey

        val kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        kg.init(spec)
        return kg.generateKey()
    }

    /** Encrypt na ho sake to null (plain text kabhi save nahi hota). */
    fun encryptPayload(plainText: String): String? {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
            val iv = Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
            val enc = Base64.encodeToString(cipher.doFinal(plainText.toByteArray(Charsets.UTF_8)), Base64.NO_WRAP)
            "$iv:$enc"
        } catch (e: Exception) {
            null
        }
    }

    /** Decrypt na ho sake to null. Purane (plain JSON) versions ka data bhi padh leta hai. */
    fun decryptPayload(stored: String): String? {
        if (stored.trimStart().startsWith("{")) return stored // legacy plain JSON
        return try {
            val parts = stored.split(":", limit = 2)
            if (parts.size != 2) return null
            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val data = Base64.decode(parts[1], Base64.NO_WRAP)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(128, iv))
            String(cipher.doFinal(data), Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }
}
