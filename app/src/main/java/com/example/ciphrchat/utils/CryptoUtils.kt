package com.example.ciphrchat.utils

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

object CryptoUtils {
    suspend fun generateKeyPair(): Pair<String, String> = withContext(Dispatchers.Default) {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(2048)
        val keyPair = keyGen.generateKeyPair()
        val pubKeyBase64 = Base64.encodeToString(keyPair.public.encoded, Base64.NO_WRAP)
        val privKeyBase64 = Base64.encodeToString(keyPair.private.encoded, Base64.NO_WRAP)
        Pair(pubKeyBase64, privKeyBase64)
    }

    suspend fun encrypt(plaintext: String, pubKeyBase64: String): String =
        withContext(Dispatchers.Default) {
            val pubKeyBytes = Base64.decode(pubKeyBase64, Base64.NO_WRAP)
            val keyFactory = KeyFactory.getInstance("RSA")
            val publicKeySpec = X509EncodedKeySpec(pubKeyBytes)
            val publicKey = keyFactory.generatePublic(publicKeySpec)

            val encryptCipher = Cipher.getInstance("RSA")
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey)
            val secretMessageBytes = plaintext.toByteArray(StandardCharsets.UTF_8)
            val encryptedBytes = encryptCipher.doFinal(secretMessageBytes)

            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        }

    suspend fun decrypt(ciphertextBase64: String, privKeyBase64: String): String =
        withContext(Dispatchers.Default) {
            val privKeyBytes = Base64.decode(privKeyBase64, Base64.NO_WRAP)
            val keyFactory = KeyFactory.getInstance("RSA")
            val privKeySpec = PKCS8EncodedKeySpec(privKeyBytes)
            val privKey = keyFactory.generatePrivate(privKeySpec)

            val encryptedBytes = Base64.decode(ciphertextBase64, Base64.NO_WRAP)
            val decryptCipher = Cipher.getInstance("RSA")
            decryptCipher.init(Cipher.DECRYPT_MODE, privKey)
            val decryptedBytes = decryptCipher.doFinal(encryptedBytes)

            decryptedBytes.decodeToString()
        }
}
