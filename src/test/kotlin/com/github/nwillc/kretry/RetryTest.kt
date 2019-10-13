package com.github.nwillc.kretry

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class RetryTest {
    @Test
    fun `should calculate with no back off delay`() {
        val config = Config<Int>().apply {
            backOff = BackOff.NONE
            delay = Delay(TimeUnit.SECONDS, 1)
        }

        val delay = delay(10, config)
        assertThat(delay.unit).isEqualTo(TimeUnit.SECONDS)
        assertThat(delay.amount).isEqualTo(1)
    }

    @Test
    fun `should calculate liner back off delay`() {
        val config = Config<Int>().apply {
            backOff = BackOff.LINER
            delay = Delay(TimeUnit.MINUTES, 1)
        }

        val delay = delay(10, config)
        assertThat(delay.unit).isEqualTo(TimeUnit.MINUTES)
        assertThat(delay.amount).isEqualTo(10)
    }

    @Test
    fun `should calculate fibonachi back off delay`() {
        val config = Config<Int>().apply {
            backOff = BackOff.FIBONACHI
            delay = Delay(TimeUnit.MINUTES, 1)
        }

        val delay = delay(9, config)
        assertThat(delay.unit).isEqualTo(TimeUnit.MINUTES)
        assertThat(delay.amount).isEqualTo(34)
    }

    @Test
    fun `should retry with defaults`() {
        val expected = "hello"
        val fail = 10
        var attempt: Int = 0
        val result = retry {
            attempt++
            if (attempt < fail) {
                throw Exception()
            }
            expected
        }

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `should perform basic retry`() {
        val expected = "hello"
        val config = Config<String>()
        val fail = 10
        var attempt: Int = 0
        val result = retry(config) {
            attempt++
            if (attempt < fail) {
                throw Exception()
            }
            expected
        }

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `should apply predicate for retry`() {
        val expected = "6"
        val config = Config<String>().apply {
            predicate = { it == expected }
        }
        val fail = 4
        var attempt: Int = 0
        val result = retry(config) {
            attempt++
            if (attempt < fail) {
                throw Exception("kaboom")
            }
            attempt.toString()
        }

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `should throw exception if attempt count exceeded`() {
        val config= Config<String>().apply {
            attempts = 5
        }
        assertThatThrownBy {
            retry(config) {
                throw Exception()
            }
        }
            .isInstanceOf(RetryExceededException::class.java)
            .hasMessage("Max retries reached: 5")
    }

    @Test
    fun `should generate fibonachi numbers`() {
        assertThat(fibonacci(10)).isEqualTo(55)
    }
}
