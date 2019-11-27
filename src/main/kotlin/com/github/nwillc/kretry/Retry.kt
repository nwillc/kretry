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

sealed class Retry<out T> {
    companion object {
        operator fun <T> invoke(config: Config<T> = Config(), func: () -> T): Retry<T> =
            try {
                Success(retry(config) { func() })
            } catch (error: Exception) {
                Failure(error)
            }
    }

    abstract fun get(): T
    abstract fun getOrElse(default: @UnsafeVariance T): T
}

class Success<T>(val value: T) : Retry<T>() {
    override fun get(): T = value
    override fun getOrElse(default: T): T = value
    override fun toString() = "Success($value)"
}

class Failure<T>(val error: Exception) : Retry<T>() {
    override fun get(): T = throw error
    override fun getOrElse(default: T): T = default
    override fun toString() = "Failure(${error.message})"
}
