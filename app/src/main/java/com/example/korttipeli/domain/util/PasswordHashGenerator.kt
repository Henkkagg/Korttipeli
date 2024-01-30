package com.example.korttipeli.domain.util

import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode

//Password gets rehashed with random salt server side. This is just to prevent plaintext in logs
object PasswordHashGenerator {

    operator fun invoke(password: String): String {
        val argon2Kt = Argon2Kt()

        val hashedPassword = argon2Kt.hash(
            mode = Argon2Mode.ARGON2_ID,
            password = password.toByteArray(),
            salt = ByteArray(32)
        )

        return hashedPassword.rawHashAsHexadecimal()
    }

}