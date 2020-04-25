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

internal const val DEFAULT_DELAY_MILLIS: Long = 500

/**
 * The retrying configuration.
 *
 * @property attempts The maximum number of attempts to make.
 * @property delay The base delay between attempts.
 * @property backOff How the base delay should be extended with each attempt.
 * @property predicate How to determine success, allowing more than a binary complete/exception.
 */
data class Config<T>(
    var attempts: Int = 10,
    var delay: Duration = Duration.of(DEFAULT_DELAY_MILLIS, ChronoUnit.MILLIS),
    var backOff: BackOff = BackOff.NONE,
    var predicate: (T) -> Boolean = { true }
) {
    fun delay(attempted: Int): Duration = when (backOff) {
        BackOff.NONE -> delay
        BackOff.ATTEMPT_MULTIPLE -> delay.multipliedBy(attempted.toLong())
        BackOff.FIBONACCI -> delay.multipliedBy(fibonacci(attempted).toLong())
    }
}

/**
 * Ways to extend the delay between attempts.
 */
enum class BackOff {
    /** Use the delay with no extending. */
    NONE,
    /** Extend the delay by multiplying by the attempt. */
    ATTEMPT_MULTIPLE,
    /** Extend the delay by multiplying by the Fibonacci number of the attempt. */
    FIBONACCI
}

/**
 * [Thread] sleep for a [Duration].
 */
fun Duration.sleep() = Thread.sleep(toMillis())

/**
 * Calculate a fibonacci number for a given position in the sequence.
 * @param n The position in the sequence to calculate.
 */
fun fibonacci(n: Int): Int {
    tailrec fun fibTail(n: Int, first: Int, second: Int): Int = if (n == 0)
        first
    else
        fibTail(n - 1, second, first + second)
    return fibTail(n, 0, 1)
}
