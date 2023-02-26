package com.example.auth


import io.ktor.util.hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

 // 1
val hashKey = hex(System.getenv("SECRET_KEY")) // 2


val hmacKey = SecretKeySpec(hashKey, "HmacSHA1") // 3


fun hash(password: String): String { // 4
    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}
