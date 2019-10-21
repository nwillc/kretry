/*
 * Copyright (c) 2019,  nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.kretry

import java.util.concurrent.TimeUnit
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import uk.org.lidalia.slf4jtest.TestLoggerFactory

class RetryTest {
    private val logger = TestLoggerFactory.getTestLogger("retry")

    @BeforeEach
    fun setUp() {
        logger.clear()
    }

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
        val fail = 5
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
        val config = Config<String>().apply {
            delay = Delay(TimeUnit.MILLISECONDS, 50)
        }
        val fail = 10
        var attempt: Int = 0
        val result = retry(config) {
            attempt++
            if (attempt <= fail) {
                throw Exception()
            }
            expected
        }

        assertThat(result).isEqualTo(expected)
        val failures = logger.loggingEvents
            .filter { it.message.startsWith("Block failed") || it.message.startsWith("Predicate failed") }
            .count()
        assertThat(failures).isEqualTo(fail)
    }

    @Test
    fun `should apply predicate as well as exceptions for retry`() {
        val expected = "6"
        val config = Config<String>().apply {
            predicate = { it == expected }
            delay = Delay(TimeUnit.MILLISECONDS, 50)
        }
        val fail = 3
        var attempt: Int = 0
        val result = retry(config) {
            attempt++
            if (attempt <= fail) {
                throw Exception("kaboom")
            }
            attempt.toString()
        }

        assertThat(result).isEqualTo(expected)
        val failures = logger.loggingEvents
            .filter { it.message.startsWith("Block failed") || it.message.startsWith("Predicate failed") }
            .count()
        assertThat(failures + 1).isEqualTo(6)
    }

    @Test
    fun `should throw exception if attempt count exceeded`() {
        val config = Config<String>().apply {
            attempts = 5
            delay = Delay(TimeUnit.MILLISECONDS, 50)
        }
        assertThatThrownBy {
            retry(config) {
                throw Exception()
            }
        }
            .isInstanceOf(RetryExceededException::class.java)
            .hasMessage("Retry, max attempts reached: 5.")
        val failures = logger.loggingEvents
            .filter { it.message.startsWith("Block failed") || it.message.startsWith("Predicate failed") }
            .count()
        assertThat(failures).isEqualTo(config.attempts)
    }

    @Test
    fun `should generate fibonachi numbers`() {
        assertThat(fibonacci(10)).isEqualTo(55)
    }
}
