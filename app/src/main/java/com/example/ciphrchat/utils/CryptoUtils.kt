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
            // first we need to transform the base 64 encoded pub key to a byte array
            val pubKeyBytes = Base64.decode(pubKeyBase64, Base64.NO_WRAP)
            // now we need to wrap our raw bytes in a spec, RSA uses X509
            val keyFactory = KeyFactory.getInstance("RSA")
            val publicKeySpec = X509EncodedKeySpec(pubKeyBytes)
            // now we need to unpack our raw bytes, this function uses the type wrapping our bytes to determine
            // the correct unpacking logic to use
            val publicKey = keyFactory.generatePublic(publicKeySpec)
            // now we need to setup our Cipher Engine to then use to encrypt
            val encryptCipher = Cipher.getInstance("RSA")
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey)
            // now we need to convert our plain text to bytes for the cypher engine
            val secretMessageBytes = plaintext.toByteArray(StandardCharsets.UTF_8)
            // now we encrypt
            val encryptedBytes = encryptCipher.doFinal(secretMessageBytes)
            // now we convert back to base 64 for safe transit over networks
            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        }

    suspend fun decrypt(ciphertextBase64: String, privKeyBase64: String): String =
        withContext(Dispatchers.Default) {
            // first we need to decode our private key to byte array
            val privKeyBytes = Base64.decode(privKeyBase64, Base64.NO_WRAP)
            // now we need to generate a spec that matches RSA private keys to wrap our bytes
            val keyFactory = KeyFactory.getInstance("RSA")
            val privKeySpec = PKCS8EncodedKeySpec(privKeyBytes)
            // now we need to actually generate a priv key object
            val privKey = keyFactory.generatePrivate(privKeySpec)
            // now we need to decode the ciphertext to a byte array
            val encryptedBytes = Base64.decode(ciphertextBase64, Base64.NO_WRAP)
            // now we need to setup our cipher engine for decoding with this priv key
            val decryptCipher = Cipher.getInstance("RSA")
            decryptCipher.init(Cipher.DECRYPT_MODE, privKey)
            // now we have to actually decode
            val decryptedBytes = decryptCipher.doFinal(encryptedBytes)
            // now convert back to a string
            decryptedBytes.decodeToString()
        }
}
