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

/**
 * An enhanced Try which will retry until [Success] or [Failure].
 */
sealed class Retry<out T> {
    companion object {
        /**
         * Invoke an instance of [Retry] with a [Config] and function to retry. Will result in either an instance of
         * [Success] or [Failure].
         * @param config The retrying [Config].
         * @param func The function to retry.
         */
        @SuppressWarnings("TooGenericExceptionCaught")
        operator fun <T> invoke(config: Config<T> = Config(), func: () -> T): Retry<T> =
            try {
                Success(retry(config) { func() })
            } catch (error: RetryExceededException) {
                Failure(error)
            }
    }

    /**
     * Get the value of the [Retry].
     */
    abstract fun get(): T

    /**
     * Get the value of the [Retry], or a default on [Failure].
     *
     * @param default Value to return if this [Retry] is a [Failure].
     */
    abstract fun getOrElse(default: @UnsafeVariance T): T
}

/**
 * A [Retry] that succeeded.
 * @param value The value returned by function invoked by the [Retry].
 */
class Success<T>(val value: T) : Retry<T>() {
    override fun get() = value
    override fun getOrElse(default: T) = value
    override fun toString() = "Success($value)"
}

/**
 * A [Retry] that failed.
 * @param value The [RetryExceededException] describing the eventual failure of the [Retry].
 */
class Failure<T>(val value: RetryExceededException) : Retry<T>() {
    override fun get(): T = throw value
    override fun getOrElse(default: T) = default
    override fun toString() = "Failure(${value.message})"
}
