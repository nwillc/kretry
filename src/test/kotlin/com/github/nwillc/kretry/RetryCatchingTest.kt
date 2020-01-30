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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.temporal.ChronoUnit

class RetryCatchingTest {
    @Test
    fun `should be able to retryCatching until failure`() {
        val result = retryCatching<String> {
            throw java.lang.Exception()
        }
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(RetryExceededException::class.java)
        assertThat(result.getOrDefault("foo")).isEqualTo("foo")
        assertThat(result.toString()).startsWith(("Failure(")).endsWith(")")
    }

    @Test
    fun `should be able to use retryCatching with success`() {
        val expected = "hello"
        val config = Config<String>().apply {
            delay = Duration.of(50, ChronoUnit.MILLIS)
        }
        val fail = 5
        var attempt = 0

        val result = retryCatching(config) {
            attempt++
            if (attempt <= fail) {
                throw Exception()
            }
            expected
        }

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrThrow()).isEqualTo(expected)
        assertThat(result.getOrDefault("foo")).isEqualTo(expected)
        assertThat(result.toString()).isEqualTo("Success($expected)")
    }
}
