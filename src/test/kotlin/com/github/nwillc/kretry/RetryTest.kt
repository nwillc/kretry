package com.github.nwillc.kretry

import org.junit.jupiter.api.Test
import java.lang.Exception

class RetryTest {

    @Test
    fun `should be able to retry`() {
        val maxAllowed = 10
        val fail = 10
        var attempt: Int = 0
       val result = retry(maxAllowed) {
           attempt++
           if (attempt < fail) {
               throw Exception("kaboom")
           }
           "hello"
       }

        println("result: $result")
    }
}
