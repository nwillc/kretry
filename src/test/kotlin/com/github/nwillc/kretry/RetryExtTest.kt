/*
 * Copyright (c) 2020,  nwillc@gmail.com
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

import java.time.Duration
import java.time.temporal.ChronoUnit
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import uk.org.lidalia.slf4jtest.TestLoggerFactory

@SuppressWarnings("TooGenericExceptionThrown")
class RetryExtTest {
    private var testLogger = TestLoggerFactory.getTestLogger("retry")

    @BeforeEach
    fun setUp() {
        testLogger.clear()
    }

    @Test
    fun `should calculate with no back off delay`() {
        val config = Config<Int>().apply {
            backOff = BackOff.NONE
            delay = Duration.of(1, ChronoUnit.SECONDS)
        }

        val delay = config.delay(10)
        assertThat(delay.seconds).isEqualTo(1)
    }

    @Test
    fun `should calculate liner back off delay`() {
        val config = Config<Int>().apply {
            backOff = BackOff.ATTEMPT_MULTIPLE
            delay = Duration.of(1, ChronoUnit.MINUTES)
        }

        val delay = config.delay(10)
        assertThat(delay.toMinutes()).isEqualTo(10)
    }

    @Test
    fun `should calculate fibonachi back off delay`() {
        val config = Config<Int>().apply {
            backOff = BackOff.FIBONACCI
            delay = Duration.of(1, ChronoUnit.MINUTES)
        }

        val delay = config.delay(9)
        assertThat(delay.toMinutes()).isEqualTo(34)
    }

    @Test
    fun `should retry with defaults`() {
        val expected = "hello"
        val fail = 5
        var attempt = 0
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
            delay = Duration.of(1, ChronoUnit.MILLIS)
        }
        val fail = 5
        var attempt = 0
        val result = retry(config) {
            attempt++
            if (attempt <= fail) {
                throw Exception()
            }
            expected
        }

        assertThat(result).isEqualTo(expected)
        val failures = testLogger.loggingEvents
            .filter { it.message.startsWith("Block failed") || it.message.startsWith("Predicate failed") }
            .count()
        assertThat(failures).isEqualTo(fail)
    }

    @Test
    fun `should apply predicate as well as exceptions for retry`() {
        val expected = "6"
        val config = Config<String>().apply {
            predicate = { it == expected }
            delay = Duration.of(50, ChronoUnit.MILLIS)
        }
        val fail = 3
        var attempt = 0
        val result = retry(config) {
            attempt++
            if (attempt <= fail) {
                throw Exception("kaboom")
            }
            attempt.toString()
        }

        assertThat(result).isEqualTo(expected)
        val failures = testLogger.loggingEvents
            .filter { it.message.startsWith("Block failed") || it.message.startsWith("Predicate failed") }
            .count()
        assertThat(failures + 1).isEqualTo(6)
    }

    @Test
    fun `should throw exception if attempt count exceeded`() {
        val config = Config<String>().apply {
            attempts = 5
            delay = Duration.of(50, ChronoUnit.MILLIS)
        }
        assertThatThrownBy {
            retry(config) {
                throw Exception()
            }
        }
            .isInstanceOf(RetryExceededException::class.java)
            .hasMessage("Retry, max attempts reached: 5.")
        val failures = testLogger.loggingEvents
            .filter { it.message.startsWith("Block failed") || it.message.startsWith("Predicate failed") }
            .count()
        assertThat(failures).isEqualTo(config.attempts)
    }

    @Test
    fun `should generate fibonachi numbers`() {
        assertThat(fibonacci(10)).isEqualTo(55)
    }
}
