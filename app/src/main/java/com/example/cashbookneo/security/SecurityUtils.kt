package com.example.cashbookneo.security

import java.security.MessageDigest
import java.security.SecureRandom
import android.util.Base64

object SecurityUtils {
    
    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return Base64.encodeToString(salt, Base64.NO_WRAP)
    }

    fun hashPin(pin: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(Base64.decode(salt, Base64.NO_WRAP))
        val hashedBytes = md.digest(pin.toByteArray())
        return Base64.encodeToString(hashedBytes, Base64.NO_WRAP)
    }
}
