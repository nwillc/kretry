package com.github.nwillc.kretry

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.lang.Exception
import java.util.concurrent.TimeUnit

class RetryTest {

    @Test
    fun `should calculate no backoff delay`() {
        val config = Config<Int>().copy(backOff = BackOff.NONE, delay = Delay(TimeUnit.SECONDS, 1))

        val delay = delay(10, config)
        assertThat(delay.unit).isEqualTo(TimeUnit.SECONDS)
        assertThat(delay.amount).isEqualTo(1)
    }

    @Test
    fun `should calculate liner backoff delay`() {
        val config = Config<Int>().copy(backOff = BackOff.LINER, delay = Delay(TimeUnit.MINUTES, 1))

        val delay = delay(10, config)
        assertThat(delay.unit).isEqualTo(TimeUnit.MINUTES)
        assertThat(delay.amount).isEqualTo(10)
    }

    @Test
    fun `should be able to retry`() {
        val config = Config<String>(20)
        val fail = 10
        var attempt: Int = 0
       val result = retry(config) {
           attempt++
           if (attempt < fail) {
               throw Exception("kaboom")
           }
           "hello"
       }

        println("result: $result")
    }
}
