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

import com.github.nwillc.slf4jkext.getLogger

/**
 * A retrying extension that will retry a given function based on a configuration.
 * The [block] is run repeatedly until succeeding based on the [config] predicate, or
 * until the [config] attempts exceeded.
 * @param config The retrying [Config].
 * @param block The function to retry.
 * @throws RetryExceededException If the [config] attempts exceeded.
 */
@SuppressWarnings("TooGenericExceptionCaught")
inline fun <R> retry(config: Config<R> = Config(), block: () -> R): R {
    val logger = getLogger("retry")
    var attempted = 0
    while (attempted < config.attempts) {
        try {
            val result = block()
            if (config.predicate(result)) return result else logger.info("Predicate failed.")
        } catch (e: Exception) {
            logger.error("Block failed with $e.")
        }
        attempted++
        config.delay(attempted).sleep()
    }
    val msg = "Retry, max attempts reached: ${config.attempts}."
    logger.error(msg)
    throw RetryExceededException(msg)
}

/**
 * Parallels Kotlin's [runCatching] employing [retry].
 * @param config The retrying [Config].
 * @param block The code block to retry.
 * @return A [Result] for the success of failure.
 */
inline fun <R> retryCatching(config: Config<R> = Config(), block: () -> R): Result<R> =
    runCatching { retry(config, block) }

/**
 * Parallels Kotlin's [runCatching] employing [retry].
 * @param config The retrying [Config].
 * @param block The code block to retry, with `this` value as its receiver.
 * @return A [Result] for the success of failure.
 */
inline fun <T, R> T.retryCatching(config: Config<R> = Config(), block: () -> R): Result<R> =
    runCatching { retry(config, block) }
