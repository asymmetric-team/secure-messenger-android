package com.safechat.encryption.base

import java.security.Key
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object KeysBase64Cipher {

    fun decodePublicKey(base64String: String): PublicKey {
        return KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(Base64Encoder.decode(base64String)))
    }

    fun decodePrivateKey(base64String: String): PrivateKey {
        return KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(Base64Encoder.decode(base64String)))
    }

    fun decodeSymmetricKey(base64String: String): SecretKey {
        return SecretKeySpec(Base64Encoder.decode(base64String), "AES");
    }

    fun toBase64String(key: Key): String {
        return String(Base64Encoder.encode(key.encoded))
    }
}