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
import org.junit.jupiter.api.Test

class RetryTest {
    @Test
    fun `should be able to Retry until Failure`() {
        val result = Retry<String> {
            throw java.lang.Exception()
        }

        assertThat(result is Failure).isTrue()
        assertThatThrownBy { result.get() }.isInstanceOf(RetryExceededException::class.java)
        assertThat(result.getOrElse("foo")).isEqualTo("foo")
        assertThat(result.toString()).startsWith(("Failure(")).endsWith(")")
    }

    @Test
    fun `should be able to use Retry for Success`() {
        val expected = "hello"
        val config = Config<String>().apply {
            delay = Delay(TimeUnit.MILLISECONDS, 50)
        }
        val fail = 5
        var attempt = 0

        val result = Retry(config) {
            attempt++
            if (attempt <= fail) {
                throw Exception()
            }
            expected
        }

        assertThat(result is Success).isTrue()
        assertThat(result.get()).isEqualTo(expected)
        assertThat(result.getOrElse("foo")).isEqualTo(expected)
        assertThat(result.toString()).isEqualTo("Success($expected)")
    }
}
